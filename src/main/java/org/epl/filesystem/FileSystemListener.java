package org.epl.filesystem;

import java.io.File;

import org.epl.core.Event;
import org.epl.core.Listener;


/**
 * This class represents a listener on a given file in the directory specified by the broker.
 * No smarts here - everything is done in the FileSystemBroker.
 *
 */
public class FileSystemListener implements Listener {
	private Event _event;
	private String filename;
	private String name;

	
	public Event getEvent() {
		return _event;
	}
	
	public void setEvent(File file) {
		Event event = new Event(this);
		event.setText(file.getAbsolutePath());
		event.setTime(String.valueOf(file.lastModified()));
		_event = event;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
