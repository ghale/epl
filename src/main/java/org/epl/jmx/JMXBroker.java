package org.epl.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.epl.core.Broker;
import org.epl.core.Listener;
import org.epl.core.RetryableBroker;


/**
 * This class represents a Broker that listens for JMX notifications.  It extends 
 * RetryableBroker so that if the JMX registry is not up, it will continue to attempt
 * to reconnect.
 *
 */
public class JMXBroker extends RetryableBroker implements Broker, NotificationListener {
	MBeanServerConnection _mbsc;
	Boolean connected = Boolean.FALSE;
	private String host;
	private String port;
	private String name;
	
	// This keeps track of listeners that have been registered.  In the event
	// that the broker becomes disconnected, he can re-register all listeners 
	// once the JMX registry becomes available again.
	private Hashtable<Listener, String> registrations = new Hashtable<Listener, String>();
	
	/**
	 * Creates a new JMXBroker which attaches to the JMX server at the given hostname and port
	 * @param host the hostname to connect to
	 * @param port the port to connect to
	 */
	public JMXBroker(String host, String port) {
		this.host = host;
		this.port = port;
	}
	
	// When a broker reconnects to a JMX registry, it must re-register all of its listeners.
	private void reRegisterListeners() {
		for (Enumeration<Listener> keys = registrations.keys(); keys.hasMoreElements();) {
			Listener listener = (Listener) keys.nextElement();
			String eventType = registrations.get(listener);
			registerListener(listener, eventType);
		}		
	}

	// Register the listener in the JMX registry for the given MBean and Notification type
	public boolean _registerListener(Listener listener, String eventType) {
		// If the listener has already been registered, don't do so again.
		if (! registrations.containsKey(listener)) registrations.put(listener, eventType);
		
		boolean success = false;
		try {
			// Split the eventType into the MBean object name and notification type
			String objectName = eventType;
			String[] components = objectName.split("!");
			ObjectName name = new ObjectName(components[0]);
			
			// If a notification type has been specified, create a filter.
			// Otherwise listen for any notification coming from that MBean.
			NotificationFilterSupport filter = null;
			if (components.length > 1 && components[1] != null) {
				filter = new NotificationFilterSupport();
				filter.enableType(components[1]);
			}
			
			// Register the listener for any MBeans that match the object name.
			Set<ObjectName> names = _mbsc.queryNames(name, null);
			for (Iterator<ObjectName> i=names.iterator(); i.hasNext();) {
				ObjectName next = i.next();
				_mbsc.addNotificationListener(next, (JMXListener)listener, filter, null);
				success = true;
			}
			
			// Print an error when the listener doesn't register for any MBeans.
			if (names.size() < 1) {
				System.out.println("Zero MBeans found matching the object name: " + name);
			}
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return success;
	}

	
	public Boolean isConnected() {
		return connected;
	}

	// Connect to the JMX Registry
	public void _connect() {
		try {
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi");
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			_mbsc = jmxc.getMBeanServerConnection();
			connected = Boolean.TRUE;
			
			// Register a listener for connection failures so we know when we should try 
			// to reconnect.
			NotificationFilterSupport filter = new NotificationFilterSupport();
			filter.enableType(JMXConnectionNotification.FAILED);
			jmxc.addConnectionNotificationListener(this, filter, null);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	
	public Listener getListener() {
		return new JMXListener();
	}

	// Listen for connection failures and reconnect whenever we lose our connection.
	public void handleNotification(Notification notification, Object handback) {
		System.out.println("Lost connection to broker at " + host + " on port " + port + "!");
		connected = Boolean.FALSE;
		connect();
		reRegisterListeners();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
