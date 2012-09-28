package org.epl.jmx;
import org.epl.core.Broker;
import org.epl.core.BrokerFactory;



/**
 * Allows the creation of a new {@link JMXBroker}
 */
public class JMXBrokerFactory implements BrokerFactory {
	/* (non-Javadoc)
	 * @see BrokerFactory#getBroker(java.lang.String)
	 */
	/**
	 * Get an instance of a JMXBroker
	 * @param connectString defines how to connect to the broker, in the format <code>hostname:port</code>
	 * @return a new instance of a JMXBroker
	 */
	public Broker getBroker(String connectString) {
		String[] strings = connectString.split(":");
		if (strings.length < 2) {
			throw new RuntimeException("Invalid broker URL: " + connectString);
		}
		JMXBroker broker = new JMXBroker(strings[0], strings[1]);
		return broker;
	}
}
