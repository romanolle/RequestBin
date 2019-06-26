package com.unitron.requestbin.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	private static final Logger Logger = LoggerFactory.getLogger(RequestController.class);
	
	
	@PostMapping(value="/event")
	public @ResponseBody Collection<Event> getEvent(@RequestHeader("Tab-Key") String tabKey) {
		return eventService.getEvents(tabKey);
	}
	
	@PostMapping(value="/handle")
	@ResponseStatus(code=HttpStatus.OK)
	public void handleRequest(@RequestBody String obj) {
		String objAsString = obj.toString();

		ObjectMapper mapper = new ObjectMapper();
		try {
			objAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(obj, Object.class));
		} catch (JsonParseException e) {
			Logger.error("", e.toString());
		} catch (JsonMappingException e) {
			Logger.error("", e.toString());
		} catch (JsonProcessingException e) {
			Logger.error("", e.toString());
		} catch (IOException e) {
			Logger.error("", e.toString());
		}
		JsonObjectEvent event = new JsonObjectEvent();
		event.setTimestamp(new Date());
		event.setObject(objAsString);
		event.setEventId(UUID.randomUUID().toString());
		eventListener.onEvent(event);
	}

}
