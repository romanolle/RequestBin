package com.unitron.requestbin.service;

import java.util.Collection;

import com.unitron.requestbin.model.Event;

public interface EventService {
	//uses long polling technique (reuse from UUI)
	//DefaultEventService implementation will also implement EventListener and UserSessionListener next to the EventService
	Collection<Event> getEvents(String key, String tabKey);
}
