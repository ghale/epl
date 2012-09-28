package org.epl.jmx;
import java.lang.management.ManagementFactory;

import javax.management.AttributeChangeNotification;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;


public class EventProducer extends NotificationBroadcasterSupport implements EventProducerMBean {

	public EventProducer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
		ObjectName name;
		try {
			name = new ObjectName("edu.unf.epl:type=EventProducer");
			EventProducer mbean = new EventProducer(); 
			mbs.registerMBean(mbean, name);
			
			int count = 0;
			long sequence = 0;
			while (true) {
				if (++count > 3) {
					System.out.println("Sending Different Event...");
					Notification x = 
			            new Notification("DifferentNotification", name, sequence, "A Different Notification");
					mbean.sendNotification(x);
					count = 0;
				}
				
				System.out.println("Sending Test event: " + sequence);
				Notification n = 
		            new Notification("TestNotification", name, sequence, "Event: " + sequence++);		 
				mbean.sendNotification(n);
				
				Thread.sleep(5000);
			}
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (InstanceAlreadyExistsException e) {
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}

	public MBeanNotificationInfo[] getNotificationInfo() { 
        String[] types = new String[] { 
            "TestNotification" 
        }; 
        String name = Notification.class.getName(); 
        String description = "Periodic notification for testing purposes"; 
        MBeanNotificationInfo info = 
            new MBeanNotificationInfo(types, name, description); 
        return new MBeanNotificationInfo[] {info}; 
    }
	
	
	public long getTime() {
		return System.currentTimeMillis();
	}

}
