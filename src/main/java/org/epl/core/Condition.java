package org.epl.core;
import java.util.HashSet;

/**
 * A Condition represents one or more events, that must occur in a certain combination,
 * in order to trigger a Situation in EPL.
 *
 * Classes that implement this interface must be able to look at a set of events, and
 * evaluate those events in order to return a Boolean answer as to whether their part
 * of the condition has been triggered.  They must also be able to return a Set of
 * event names that they have considered.
 *
 */
public interface Condition {
	/**
	 * Determine whether this item and its children should be marked as "triggered"
	 * @return whether this item, and the items beneath it, report being triggered
	 */
	public Boolean isTriggered();
	/**
	 * Get a Set of events that exist beneath this item.  The Set ensures that there are
	 * no duplicates.
	 * @return the set of all events considered as children
	 */
	public HashSet<String> getEventSet(); }
