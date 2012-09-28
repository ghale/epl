package org.epl.parser;
import org.epl.core.Broker;
import org.epl.core.Listener;


/* Generated By:JJTree: Do not edit this line. ASTListener.java */

/**
 * Represents the Listener production in the EPL grammar.
 * <pre>
 * Listener ::= "listener" &lt;ID&gt; "for" &lt;STRING&gt; "in" &lt;ID&gt;
 * </pre>
 */
public class ASTListener extends SimpleNode {
	String name;
	String broker;
	String eventType;
	
  public ASTListener(int id) {
    super(id);
  }

  public ASTListener(EplParser p, int id) {
    super(p, id);
  }

	
	public void interpret() {
		if (EplParser.debug) System.out.println("Declared Listener: " + name);
		// Make sure the broker is valid
		if (! bus.brokers.containsKey(broker)) {
			throw new RuntimeException("Invalid broker name in listener statement: " + broker);
		}
		
		// Get the broker from the bus
		Broker myBroker = (Broker) bus.brokers.get(broker);
		// Replace the quotation marks in the event string
		eventType = eventType.replace("\"", "");
		// Generate a listener from the broker
		Listener listener = myBroker.getListener();
		listener.setName(name);
		// Register the new listener on the bus
		bus.registerListener(name, listener);
		symtable.put(name, listener);
		// Register the listener with the broker
		myBroker.registerListener(listener, eventType);
	}

}