package org.epl.core;

/**
 * Represents an <em>event</em> in EPL.  An event is defined with syntax like:
 * <p>
 * <code>listener event1 for "edu.unf.epl:type=EventProducer!DifferentNotification" in jmx;</code> 
 * 
 * <em>Events</em> are received from {@link Broker brokers}. 
 * 
 * @see ASTListener
 * @see ASTBroker
 * @see ASTAction
 * @see Broker
 */
public class Event implements Cloneable {
	private String text;
	private String time;
	private Listener listener;
	
	public Event(Listener listener) {
		super();
		this.listener = listener;
	}

	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	
}
