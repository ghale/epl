package org.epl.jmx;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.epl.core.Event;
import org.epl.core.EventBus;
import org.epl.core.Listener;
import org.epl.parser.EplParser;


/**
 * This class represents a JMX Notification listener.  The JMXBroker registers this
 * class for a given MBean and notification type.
 *
 */
public class JMXListener implements NotificationListener, Listener {
	private EventBus bus = EventBus.getBus();
	private Event _event;
	private String name;

	
	/* (non-Javadoc)
	 * @see javax.management.NotificationListener#handleNotification(javax.management.Notification, java.lang.Object)
	 */
	public void handleNotification(Notification notification, Object handback) {
		if (EplParser.debug) System.out.println("\nReceived notification:");
		if (EplParser.debug) System.out.println("\tClassName: " + notification.getClass().getName());
		if (EplParser.debug) System.out.println("\tSource: " + notification.getSource());
		if (EplParser.debug) System.out.println("\tType: " + notification.getType());
		if (EplParser.debug) System.out.println("\tMessage: " + notification.getMessage());
		if (EplParser.debug) System.out.println("\tTimestamp: " + notification.getTimeStamp());
		
		Event event = new Event(this);
		event.setText(notification.getMessage());
		event.setTime(String.valueOf(notification.getTimeStamp()));
		_event = event;
		
		// Call the bus to process the received notification
		bus.processEvent(event);
	}

	
	public Event getEvent() {
		return _event;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
}
