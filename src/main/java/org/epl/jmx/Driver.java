package org.epl.jmx;
import java.io.IOException;
import java.util.ArrayList;
import java.io.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.ObjectName;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Driver class for producing JMX events.  Invoke with:

 * <p>

 * <pre>
 * java -Dcom.sun.management.jmxremote.port=9999           \
 *      -Dcom.sun.management.jmxremote.authenticate=false  \
 *      -Dcom.sun.management.jmxremote.ssl=false           \
 *      Driver                                             \
 *        [events.xml]
 * </pre>
 * <p>

 * A single optional argument specifies the location of an XML file containing
 * hardcoded events.
 * <p>

 * After the program launches, it may be driven interactively from the console.
 * 
 */
public class Driver {

	/**
	 * Represents a notification event, with scheduling details as necessary
	 *
	 */
	private static class Event {
		int sequence = 0;
		int maxOccurrences = 0;
		int interval;
		String domain;
		String type;
		String notificationType;
		String message;
		ObjectName name;
		EventProducer mbean;
		
		Timer timer = null;
		FireTask fireTask = null;
		
		/**
		 * 
		 * @param domain the domain for the event (ex. "edu.unf.epl")
		 * @param type the type for the event (ex. "EventProducer")
		 * @param notificationType the notification type (ex. "TestNotification")
		 * @param message a message that will appear in the notification
		 */
		Event(String domain, String type, String notificationType, String message) {						
			this.domain = domain;
			this.type = type;
			this.notificationType = notificationType;
			this.message = message;
			
			// register with the MBean server
			// TODO if the domain + type has already been registered, don't reregister...
			try {
				mbean = new EventProducer();
				name = new ObjectName(domain + ":" + "type=" + type); 			     
			    mbs.registerMBean(mbean, name);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Schedule this event for repeated execution
		 * @param interval how often to repeat the event, in milliseconds
		 * @param maxOccurrences the maximum number of times to fire the event; specify 0 to run forever
		 */
		void schedule(int interval, int maxOccurrences) {
			this.interval = interval;
			this.maxOccurrences = maxOccurrences;

			// Cancel any existing timer
			if (fireTask != null) {
				fireTask.cancel();
			}
			if (timer != null) {
				timer.cancel();
				timer.purge();				
			}

			timer = new Timer(true);
			fireTask = new FireTask();
			timer.scheduleAtFixedRate(fireTask, 0, interval);
		}
		
		/**
		 * Send the event notification
		 */
		void fire() {
			Notification notif = new Notification(notificationType, name, sequence, message);
			mbean.sendNotification(notif);
			sequence++;			
		}

		/**
		 * This class simply calls the fire() method; it exists so that Java's Timer facility 
		 * can be used
		 */
		class FireTask extends TimerTask {
		    public void run() {
		        fire();
		        if ((maxOccurrences > 0) && (sequence >= maxOccurrences)) {
		            cancel();		            
		        }
		    }
		}
		
		public String toString() {
			return (domain + ":" + "type=" + type);
		}
		
		
	}
	
	
	static ArrayList<Event> eventList = new ArrayList<Event>(); 
	static MBeanServer mbs;
	
   /**
    * Add an event to the list of events
    * 
    * @param domain the domain for the new event
    * @param type the type for the new event
    * @param notificationType the notification type for the new event
    * @param message a message for the new event
    * @return the position of the event in the ArrayList
    */
	public static int addEvent(String domain, String type, String notificationType, String message) {
		Event event = new Event(domain, type, notificationType, message);
		eventList.add(event);
		return (eventList.size() - 1);
	}

	/**
	 * Schedule an event for repeated execution; the event must already exist
	 * 
	 * @param index the position of the event in the ArrayList
	 * @param interval the interval in milliseconds
	 * @param maxOccurrences the maximum number of times to run the event; 0 means forever
	 */
	public static void scheduleEvent(int index, int interval, int maxOccurrences) {
		((Event)eventList.get(index)).schedule(interval, maxOccurrences);
	}

	/**
	 * Prompt the user to enter a number
	 * @return the integer typed by the user, or -1 if an error occurs or the
	 * number was invalid
	 */
	public static int readUserInt(String prompt, int min, int max) {
	    int retval;
	    System.out.print(prompt);
	    try {
	        retval = Integer.parseInt(in.readLine());
	        if (retval < min || retval > max)
	            retval = -1;
	    }
	    catch (Exception e) {
	        retval = -1;
	    }
	    return retval;
	}

	
	/**
	 * Prompt the user to enter a string
	 * @return the String typed by the user
	 */
	public static String readUserString(String prompt) {
	    String retval;
		System.out.print(prompt);
		try {
            retval = in.readLine();
		}
		catch (IOException e) {
			retval = "";
		}
	    return retval;
	}

	
    /**
     * Handle a command.  If the user needs to specify a value for a particular
     * command, prompt the user for input.
     * @param cmd the command to process (ex. 'l' for list)
     */
    public static void processCommand(char cmd) {
    	int i;
        switch (cmd) {
            case 'a':
                System.out.println("Add a new event:");
                String domain = readUserString("Enter event domain > ");
                String type = readUserString("Enter event type > ");
                String notificationType = readUserString("Enter notification type > ");
                String message = readUserString("Enter a message > ");
                i = addEvent(domain, type, notificationType, message);
                System.out.println("Added event " + (i + 1));
                break;
            case 'l':
                System.out.println("List of existing events:");
                for (i = 0; i < eventList.size(); i++) {
                	System.out.println((i + 1) + ".  " + eventList.get(i).toString());
                }                
                break;
            case 's':
                System.out.println("Send an event now:");
                // prompt them for the event number
                i = readUserInt("Enter number of event to send >", 1, eventList.size());
                if (i != -1) {
                    ((Event)eventList.get(i - 1)).fire();
                } else
                	System.out.println("Invalid entry.");
                break;
            case 'e':
                System.out.println("Schedule a series of events:");
                i = readUserInt("Enter number of event to schedule >", 1, eventList.size());
                int interval = readUserInt("How often should the event occur (in milliseconds) >", 0, Integer.MAX_VALUE);
                int maxOccurrences = readUserInt("How many times should the event occur (0 means forever) >", 0, Integer.MAX_VALUE);	
                scheduleEvent(i - 1, interval, maxOccurrences);
                break;
            case 'q':
                System.out.println("quit");
                break;
            case '?':  // display help
                System.out.println("Commands:");
                System.out.println("a:  Add a new event");
                System.out.println("l:  List existing events");
                System.out.println("s:  Send an event now");
                System.out.println("e:  schedule a sEries of events");                    
                System.out.println("q:  quit");
                System.out.println("?:  show this help display\n");
                break;
        }
    }

    
    /**
     * a single argument, if given, is used as the filename of the configuration file
     */
    public static void main(String[] args) {
       
        // set up the MBean stuff
        mbs = ManagementFactory.getPlatformMBeanServer();        
        
        // if an argument is given on the command line, it's a filename
        // read and process the configuration
        if (args.length > 0) { 
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = null;
            Document doc = null;
            try {
                documentBuilder = documentBuilderFactory.newDocumentBuilder();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                doc = (Document) documentBuilder.parse(new FileInputStream( args[0] ));
            } catch (Exception e) {
        	    e.printStackTrace();
            }    

            Element docElem = doc.getDocumentElement();
            NodeList eventList = docElem.getElementsByTagName("event");
            if (eventList != null) {
                for (int i = 0; i < eventList.getLength(); i++) {
                    org.w3c.dom.Node currentEvent = eventList.item(i);

                    // For everything in the config file, create a notification entry             
                    NamedNodeMap attrs = currentEvent.getAttributes();
                    int index = addEvent(attrs.getNamedItem("domain").getNodeValue(),
                	                     attrs.getNamedItem("type").getNodeValue(), 
                		                 attrs.getNamedItem("notificationType").getNodeValue(),
                		                 attrs.getNamedItem("message").getNodeValue());
                    if (attrs.getNamedItem("repeat").getNodeValue().equalsIgnoreCase("true")) {
                    	scheduleEvent(index,
                    			Integer.parseInt(attrs.getNamedItem("repeatTime").getNodeValue()),
                    			Integer.parseInt(attrs.getNamedItem("repeatCount").getNodeValue()));                    			
                    }
                }
            }
        }
               
        // menu loop
        char command = '?';
        String stringInput = "";
        do {            
            processCommand(command);

            // prompt user for next command
            try {
                System.out.print("> ");
                stringInput = in.readLine( );
                if (!stringInput.equals("")) {
                    command = Character.toLowerCase(stringInput.charAt(0));
                    stringInput = stringInput.substring(1).trim();
                } else
                    command = '?';
            }
            catch (IOException e) {
            }
        } while (command != 'q');
    }
    static BufferedReader in =
        new BufferedReader(new InputStreamReader(System.in));
}
