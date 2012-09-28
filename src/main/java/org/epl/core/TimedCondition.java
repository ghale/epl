package org.epl.core;
import java.util.HashSet;

/**
 * TimedCondition represents a condition that has a time factor.  All listeners referenced 
 * within a condition are wrapped by a TimedCondition with the timeout of the condition.
 * A listener referenced from multiple conditions will create multiple TimedConditions 
 * each with the timeout of the condition it is referenced in. 
 *
 */
public class TimedCondition implements Condition {
	private Listener listener;
	private Long timeout;
	private EventBus bus = EventBus.getBus();
	
	public TimedCondition(Listener listener) {
		super();
		this.listener = listener;
	}

	// Return true if the event time is inside of the timeout
	public Boolean isTriggered() {
		Boolean triggered = Boolean.FALSE;
		Event _event = listener.getEvent();
		if (_event != null) {
			if (Long.valueOf(_event.getTime()) > bus.getProcessTime() - timeout.longValue()) {
				return Boolean.TRUE;
			}
		}
		return triggered;
	}
	
	public Event getEvent() {
		return listener.getEvent();
	}

	public Long getTimeout() {
		return timeout;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	
	public HashSet<String> getEventSet() {
		// Conditional has either a single OrConditional() or a single AndConditional() child
		HashSet<String> eventSet = new HashSet<String>();
		return eventSet;
	}
}
