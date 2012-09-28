package org.epl.core;


/**
 * This defines the functionality that brokers must implement.  Brokers provide an interface between the
 * event bus and the events being monitored.
 *
 */
public interface Broker {
	public void registerListener(Listener listener, String eventType);
        /**
         * Get a {@link Listener} instance
         * @return a new Listener instance appropriate to the type of broker that implements this interface
         */
	public Listener getListener();
	public void connect();
	public Boolean isConnected();
	public void setName(String name);
	public String getName();
}
