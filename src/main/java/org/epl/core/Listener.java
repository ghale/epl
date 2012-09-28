package org.epl.core;

/**
 * The Listener interface represents any abstract listener on the bus.
 *
 */
public interface Listener {

	public Event getEvent();
	public void setName(String name);
	public String getName();
	
}
