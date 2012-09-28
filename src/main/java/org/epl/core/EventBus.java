package org.epl.core;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.epl.parser.EplParser;


/**
 * This is the main runtime for the EPL system.  It coordinates all activity within the system.
 * 
 * Tables of all defined brokers, listeners, conditions, actions and situations are defined here.
 */
public class EventBus {
	private static EventBus _bus;
	
	// These are the hashtables that maintain the different types of objects in the language 
	 public final Hashtable<String, Broker> brokers = new java.util.Hashtable<String, Broker>();
	 public final Hashtable<String, Listener> listeners = new java.util.Hashtable<String, Listener>();
	 public final Hashtable<String, Condition> conditions = new java.util.Hashtable<String, Condition>();
	 public final Hashtable<String, Object> actions = new java.util.Hashtable<String, Object>();
	 public final LinkedList<SituationThread> situations = new LinkedList<SituationThread>();

	 // Locks are used to coordinate execution of the situation processing loop
	 // Situation threads block on a lock until the bus notifies them that an 
	 // event has occurred that they may be interested in.
	 private LinkedList<Object> locks = new LinkedList<Object>();
	 private long processTime = System.currentTimeMillis();
	 
	 // The SituationThread is what actually does the processing when events arrive on the 
	 // bus.  These threads start up and block until an event related to their condition
	 // arrives on the bus.
	 private class SituationThread extends Thread {
			private Situation _situation;
			private int index = situations.size();
			
			public SituationThread(Situation situation, int index) {
				this._situation = situation;
				this.index = index;
			}
			
			// Determines whether the event that has occurred pertains to this 
			// situation.
			public boolean containsEvent(Event event) {
				Condition condition = _situation.getCondition();
				Set<String> s = condition.getEventSet();
				for (Iterator<String> i=s.iterator(); i.hasNext();) {
					String next = i.next();
					if (event.equals(listeners.get(next).getEvent())) {
						return true;
					}
				}
				
				return false;
			}
			
			
			public void run() {
				System.out.println("Starting situation " + index + "...");
				
				// Create a new lock for this situation
				Object lock = new Object();
				synchronized (locks) {
					locks.add(lock);
				}
				
				// Execute forever...
				while (true) {
					// Wait for the bus to notify us that an event has occurred that
					// we should process
					try {
						synchronized (lock) {
							lock.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					// Check to see if the condition has been triggered - if so, tell the
					// Situation object that he has been triggered.
					if (EplParser.debug) System.out.println("Situation " + index + ": Processing event...");
					if (_situation.getCondition().isTriggered()) {
						if (EplParser.debug) System.out.println("Situation " + index + " has triggered...");
						  _situation.trigger();
					}
				}
			}
		}
	 
	 // Returns a Singleton of the bus
	  public static EventBus getBus() {
		  if (_bus == null) {
			  _bus = new EventBus();
		  }
		  
		  return _bus;
	  }
	  
	  
	  /**
	   * Whenever an event occurs, processEvent will be called.  This causes each situation thread
	   * to be checked to see if the event pertains to it and if so, causes it to evaluate the 
	   * conditions that it depends upon.
	   */
	public synchronized void processEvent(Event e) {
		  long now = System.currentTimeMillis();
		  synchronized (locks) {
			  // Set the time that this was called in case we get delayed
			  processTime = now;
			  for (int i=0; i<locks.size(); i++) {
				  Object lock = locks.get(i);
				  SituationThread situation = situations.get(i);
				  synchronized (lock) {
					  // If the situation in question pertains to a given situation,
					  // notify that situation.
					  if (situation.containsEvent(e))
						  lock.notifyAll();
				  }
			  }
			  
		  }
	  }
	  
	  public void registerListener(String name, Listener listener) {
		  listeners.put(name, listener);
	  }
	  
	  public void registerCondition(String name, Condition condition) {
		  conditions.put(name, condition);
	  }
	  
	  public void registerBroker(String name, Broker broker) {
		  brokers.put(name, broker);
	  }

	  // When a situation is registered, generate a new situation thread
	  // and add it to the situations list
	public synchronized void registerSituation(final Situation situation) {
		SituationThread thread = new SituationThread(situation, situations.size()) ;
		
		situations.add(thread);		
	}
	
	// Start all the situations on the bus
	public void start() {
		for (int i = 0; i < situations.size(); i++) {
			Thread thread = situations.get(i);
			thread.start();
		}
	}

	public long getProcessTime() {
		return processTime;
	}
}
