package org.epl.core;


/**
 * The RetryableBroker class represents a broker that can reconnect to an event source
 * if the event source fails or is not running at start time.  All classes that extend 
 * RetryableBroker must implement the _connect() and _registerListener() methods to
 * represent technology-specific connection and registration logic.  When a connection 
 * fails, RetryableBroker will wait 10 seconds before reconnecting and continue to try
 * until a connection is successful.
 *
 */
public abstract class RetryableBroker implements Broker {
	// Listeners block on registration until the broker is connected.
	private Object connectLock = new Object();

	// Classes extending RetryableBroker must implement technology-specific
	// connection logic in this method.
	public abstract void _connect();
	
	
	// Tries to connect to the event source every 10 seconds until successful.
	public void connect() {
		Thread connector = new Thread(getName()) {
			public void run() {
				boolean attempted = false;
				while (! isConnected()) {
					if (attempted)
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e1) {	}
					System.out.println("Attempting to connect to broker " + getName() + "...");
					attempted = true;
					_connect();
				}
				System.out.println("Broker " + getName() + " successfully connected!");
				
				// Let listeners know that the broker is connected and they can continue with 
				// registration.
				synchronized (connectLock) {
					connectLock.notifyAll();
				}
			}
		};
		connector.start();

	}
	
	public abstract boolean _registerListener(Listener listener, String eventType);

	
	public void registerListener(final Listener listener,
			final String eventType) {
		
		// Create a thread to register this listener
		Thread connector = new Thread(getName()) {
			
			public void run() {	
				System.out.println("Attempting to register listener " + listener.getName() + " in broker " + getName() + "...");
				// Block until the broker is connected, then continue with registration
				if (! isConnected()) synchronized(connectLock) {
					try {
						connectLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				boolean success = _registerListener(listener, eventType);
				
				if (success) {
					System.out.println("Listener " + listener.getName() + " successfully registered!");
				} else {
					System.out.println("Failed to register listener " + listener.getName() + " - check listener parameters.");
				}
			}
		};
		connector.start();

	}

}
