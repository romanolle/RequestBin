package com.unitron.requestbin.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unitron.requestbin.model.Event;
import com.unitron.requestbin.model.EventListener;
import com.unitron.requestbin.model.JsonObjectEvent;
import com.unitron.requestbin.service.EventService;

@Controller
public class RequestController {

	@Autowired
	private EventService eventService;

	@Autowired
	private EventListener eventListener;
	
	@Value("${server.servlet.context-path}")
	private String contextPath;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);

	private static final String PREFIX = "/services";

	public static final String EVENT_PATH = PREFIX + "/event";

	private static final String HANDLE_PATH = PREFIX + "/handle";

	//@PostMapping(value= {EVENT, EVENT +"/{key}"})
	@PostMapping(value= {EVENT_PATH, EVENT_PATH +"/**"})
	public @ResponseBody Collection<Event> getEvent(@RequestHeader("Tab-Key") String tabKey, HttpServletRequest request) {
//		if(key == null) {
//			key = "";
//		}
		String key = getKey(request.getRequestURI(), EVENT_PATH);
		return eventService.getEvents(key, tabKey);
	}

	//@PostMapping(value= {PREFIX + "/handle", PREFIX + "/handle/{key}"})
	@PostMapping(value= {HANDLE_PATH, HANDLE_PATH + "/**"})
	@ResponseStatus(code=HttpStatus.OK)
	public void handleRequest(@RequestHeader Map<String, String> headers, @RequestBody String obj, HttpServletRequest request) {//@RequestParam Map<String, String> reqParam, @PathVariable(required = false) String key) {
		
		String key = getKey(request.getRequestURI(), HANDLE_PATH);
		
		StringBuilder headersAsString = new StringBuilder("Headers");
		headersAsString.append("\r\n");
		for(Entry<String, String> entry : headers.entrySet()) {
			headersAsString.append(String.format("Header '%s' = %s", entry.getKey(), entry.getValue()));
			headersAsString.append("\r\n");
			LOGGER.info(String.format("Header '%s' = %s", entry.getKey(), entry.getValue()));
	    }
		LOGGER.error("new event {}", obj);
		headersAsString.append("=================================================\r\n\r\n");
		
		if(key == null) {
			key = "";
		}
		
		String objAsString = obj.toString();

		ObjectMapper mapper = new ObjectMapper();
		try {
			objAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(obj, Object.class));
		} catch (JsonParseException e) {
			LOGGER.error("", e.toString());
		} catch (JsonMappingException e) {
			LOGGER.error("", e.toString());
		} catch (JsonProcessingException e) {
			LOGGER.error("", e.toString());
		} catch (IOException e) {
			LOGGER.error("", e.toString());
		}
		JsonObjectEvent event = new JsonObjectEvent();
		event.setTimestamp(new Date());
		event.setObject(headersAsString + objAsString);
		event.setEventId(UUID.randomUUID().toString());
		eventListener.onEvent(event, key);
	}

	private String getKey(String uri, String path) {
		return uri.replaceFirst("/", "")
				.replaceFirst(
						contextPath.replaceFirst("/", ""),
						""
				)
				.replaceFirst(path, "")
				.replaceFirst("/", "");
	}

}
