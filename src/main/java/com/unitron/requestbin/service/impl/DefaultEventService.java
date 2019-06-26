package com.unitron.requestbin.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unitron.requestbin.model.Event;
import com.unitron.requestbin.model.EventListener;
import com.unitron.requestbin.model.TabKeyListener;
import com.unitron.requestbin.service.EventService;
import com.unitron.requestbin.utils.DateUtils;

@Component
public class DefaultEventService implements EventService, EventListener, TabKeyListener {

	private static final Logger logger = LoggerFactory.getLogger(DefaultEventService.class);

	
	private static final int EVENT_QUEUE_CAPACITY = 10000;	 
	private static final int EVENT_LONG_POLLING_TIMEOUT = 10;			// defines 10s long polling timeout
	private static final int NEXT_EVENT_COLLECTION_TIME_WINDOW = 50;	// defines 50ms window to collect next events for bulk events dispatch
	private static final int NEXT_EVENTS_TIMEOUT = 200;		
	
	private static final int TAB_EXPIRE_TIMEOUT = 60 * 1000;//in miliseconds
	private static final int TAB_EXPIRE_PERIOD = 3 * 60 * 1000;//in miliseconds
	private ExecutorService EXECUTOR_SERVICE;			// defines 1000ms bulk events collection timeout

	private final Object mutex=new Object();
	
	private final Map<String, TabData> tabs = Maps.newConcurrentMap();
				
	public BlockingQueue<Event> getEventQueue(String tabKey) {
		synchronized(mutex) {
			createTabKeyIfNotExists(tabKey);
			tabs.get(tabKey).updateLastAccess();
			return tabs.get(tabKey).getQueue();
		}
	}

	private void createTabKeyIfNotExists(String tabKey) {
		if(tabKey != null && !tabs.containsKey(tabKey)) {
			tabs.put(tabKey, new TabData(new LinkedBlockingQueue<Event>(EVENT_QUEUE_CAPACITY)));
		}
	}

	public void checkAliveTabs() {
		synchronized(mutex) {
			for(Entry<String, TabData> entry : tabs.entrySet()) {
				TabData data = entry.getValue();
				if(data.isTabExpired()) {
					tabs.remove(entry.getKey());
				}
			}
		}
	}

	public void offerToAllTabs(Event event) throws InterruptedException {
		synchronized(mutex) {
			for(Entry<String, TabData> tab : tabs.entrySet()) {
				BlockingQueue<Event> events = tab.getValue().getQueue();
				events.put(event);
			}
		}
	}

	private static class TabData {
		
		private final LinkedBlockingQueue<Event> queue;
		private Date lastAccess;
		
		public TabData(LinkedBlockingQueue<Event> queue) {
			super();
			lastAccess = new Date();
			this.queue = queue;
		}

		public boolean isTabExpired() {
			return DateUtils.plusMillies(lastAccess, TAB_EXPIRE_TIMEOUT).before(new Date());
		}

		public void updateLastAccess() {
			this.lastAccess = new Date();
		}

		public LinkedBlockingQueue<Event> getQueue() {
			return queue;
		}
	}

	@PostConstruct
	public void init(){
		runAliveTabChecker();
	}
	
	private void runAliveTabChecker() {
		CustomizableThreadFactory threadFactory = new CustomizableThreadFactory("AliveEventTabChecker");
		threadFactory.setDaemon(true);		
		EXECUTOR_SERVICE = Executors.newSingleThreadExecutor(threadFactory);
		EXECUTOR_SERVICE.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						try {
							Thread.sleep(TAB_EXPIRE_PERIOD);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
							
						//removes expired tabs
						checkAliveTabs();
					} catch(Exception e){
						logger.error("Error in user session checker thread", e);
					}
				}
			}
		});
	}

	@PreDestroy
	public void destroy(){
		EXECUTOR_SERVICE.shutdown();
	}

	@Override
	public void onRequest(String tabKey) {
		createTabKeyIfNotExists(tabKey);
	}

	@Override
	public void onEvent(Event event) {
		logger.info("Received event {} will go to tabs {}", event, tabs.keySet().toString());
		synchronized (mutex) {
			try {
				offerToAllTabs(event);
			} catch(InterruptedException e) {
				logger.error(e.toString());
			}
		}
	}

	@Override
	public Collection<Event> getEvents(String tabKey) {
		if(!tabs.containsKey(tabKey))
			return null;
		BlockingQueue<Event> eventQueue=getEventQueue(tabKey);
		List<Event> events=Lists.newArrayList();
		if(eventQueue==null)
			return events;
		
		Event event=null;
		try {
			event=eventQueue.poll(EVENT_LONG_POLLING_TIMEOUT, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		//setting timeout before sending events
		Date stopTime=DateUtils.plusMillies(new Date(), NEXT_EVENTS_TIMEOUT);
				
		while(event!=null){
			events.add(event);
			
			try {
				event=eventQueue.poll(NEXT_EVENT_COLLECTION_TIME_WINDOW, TimeUnit.MICROSECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
			if(stopTime.before(new Date())){
				if(event!=null)
					events.add(event);
				break;
			}
		}
		logger.info("Fire FE events {}", Iterables.toString(events));	
		return events;
	}

}
