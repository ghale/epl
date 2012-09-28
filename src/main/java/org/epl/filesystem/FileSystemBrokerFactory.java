package org.epl.filesystem;
import org.epl.core.Broker;
import org.epl.core.BrokerFactory;


/**
 * This class implements the BrokerFactory interface and generates FileSystemBrokers.
 *
 */
public class FileSystemBrokerFactory implements BrokerFactory {

	
	public Broker getBroker(String connectString) {
		return new FileSystemBroker(connectString);
	}

}
