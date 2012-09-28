package org.epl.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import org.epl.core.Broker;
import org.epl.core.Event;
import org.epl.core.EventBus;
import org.epl.core.Listener;


/**
 * The FileSystemBroker class implements the Broker interface and represents a broker 
 * that listens for changes in a directory.  It will trigger any time a file specified by
 * a registered listener has its modification time updated.
 *
 */
public class FileSystemBroker implements Broker {
	private EventBus bus = EventBus.getBus();
	private String filesystem;
	private String name;
	private File dir;
	private LinkedList<FileSystemListener> listeners = new LinkedList<FileSystemListener>();
	
	// This thread implements a polling watcher to determine when a file in the directory 
	// changes.  Don't like the polling aspect of it, but it gets the job done.
	private Thread watcher = new Thread() {
		
		public void run() {
			// LastCheck holds the last time the directory was checked for changes
			long lastCheck = System.currentTimeMillis();
			while (true) {
				// If the directory exists
				if (isConnected()) {
					boolean foundEvents = false;
					File[] files = dir.listFiles();
					ArrayList<Event> events = new ArrayList<Event>();
					// Cycle through the listeners 
					for (int i=0; i<listeners.size(); i++) {
						FileSystemListener listener = (FileSystemListener) listeners.get(i);
						// If the files contain the file watched for by this listener,
						// and it has changed, trigger the event.  Files should probably
						// be placed in a Hashtable for efficiency to avoid the O(n^2) 
						// behavior.
						for (int j=0; j<files.length; j++) {
							if (files[j].getName().equals(listener.getFilename())) {
								if (files[j].lastModified() > lastCheck) {
									listener.setEvent(files[j]);
									events.add(listener.getEvent());
									foundEvents = true;
								}
							}
						}
					}
					
					// Go through each event and process it on the bus
					if (foundEvents) 
						for (int i=0; i<events.size(); i++)
							bus.processEvent(events.get(i));
				}
				
				// Reset the check time and sleep for 3 seconds
				// This makes it tough to get fine granularity on the time
				// aspect of any conditions that contain a file event.
				// Would love to find a better way to do this.
				lastCheck = System.currentTimeMillis();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) { }
			}
		} 
	};
	
	public FileSystemBroker(String filesystem) {
		this.filesystem = filesystem;
	}

	
	public void connect() {
		System.out.println("Attempting to connect to broker " + getName() + "...");
		dir = new File(filesystem);
		watcher.start();
		System.out.println("Broker " + getName() + " successfully connected!");
	}

	
	public Listener getListener() {
		return new FileSystemListener();
	}

	// It's connected if the directory exists.
	public Boolean isConnected() {
		return (dir.exists() && dir.isDirectory());
	}

	
	public void registerListener(Listener listener,
			String eventType) {
		((FileSystemListener)listener).setFilename(eventType);
		System.out.println("Attempting to register listener " + listener.getName() + " in broker " + getName() + "...");
		synchronized (listeners) {
			listeners.add((FileSystemListener) listener);
		}
		System.out.println("Listener " + listener.getName() + " successfully registered!");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
