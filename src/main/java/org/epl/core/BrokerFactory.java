package org.epl.core;
import org.epl.jmx.JMXBroker;
import org.epl.jmx.JMXBrokerFactory;
import org.epl.parser.ASTBroker;

/**
 * This defines the functionality that broker factories must implement.
 * <p>
 * Broker factories allow the creation of EPL brokers of various types.
 * The programmer may specify the desired type in the connection string syntax of the EPL
 * {@link ASTBroker broker} statement.
 * <p>
 * For example, the following EPL statement specifies a broker type of <code>jmx</code>:
 * <p>
 * <code>broker jmx is "jmx://localhost:9999";</code 
 * <p>
 * In this example, <code>jmx</code> corresponds to the {@link JMXBrokerFactory}, which returns
 * a {@link JMXBroker} (which implements the {@link Broker} interface) as a result.  The portion
 * after the <code>://</code> is specific to the broker. 
 * @see ASTBroker  
 */
public interface BrokerFactory {

    /**
     * Get an instance of the particular class that implements this interface.
     * @param connectString has a format specific to the class that implements this interface
     * @return an object of the desired broker type
     */
	public abstract Broker getBroker(String connectString);

}