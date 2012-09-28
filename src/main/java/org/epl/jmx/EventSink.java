package org.epl.jmx;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;


public class EventSink implements NotificationListener {

	
	public void handleNotification(Notification notification, Object handback) {
		System.out.println("\nReceived notification:");
		System.out.println("\tClassName: " + notification.getClass().getName());
		System.out.println("\tSource: " + notification.getSource());
		System.out.println("\tType: " + notification.getType());
		System.out.println("\tMessage: " + notification.getMessage());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("\nCreate an RMI connector client and connect it to the RMI connector server");
		JMXServiceURL url;
		EventSink listener = new EventSink();
		try {
			url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi");
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			NotificationFilterSupport filter = new NotificationFilterSupport();
			filter.enableType("DifferentNotification");
			mbsc.addNotificationListener(new ObjectName("edu.unf.epl:type=EventProducer"), listener, filter, null);
			while (true) Thread.sleep(10000);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
