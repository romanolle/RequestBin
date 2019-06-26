package com.unitron.requestbin.model;

public class JsonObjectEvent extends Event {

	private Object object;

	public JsonObjectEvent() {
	}

	public JsonObjectEvent(Object object) {
		super();
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return "JsonObjectEvent [object=" + object.toString() + ", getEventId()=" + getEventId() + ", getTimestamp()="
				+ getTimestamp() + "]";
	}
	
}
