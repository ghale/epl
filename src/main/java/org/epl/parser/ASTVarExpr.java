package org.epl.parser;
/* Generated By:JJTree: Do not edit this line. ASTVarExpr.java */

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.epl.core.Condition;
import org.epl.core.Event;
import org.epl.core.Listener;
import org.epl.core.TimedCondition;
/**
 * Represents the VarExpr production in the EPL grammar.
 * <pre>
 * VarExpr ::= &lt;ID&gt; [ "." &lt;ID&gt; ]
 * </pre>
 */
public class ASTVarExpr extends SimpleNode implements Condition {
	String name;
	String member;
	
  public ASTVarExpr(int id) {
    super(id);
  }

  public ASTVarExpr(EplParser p, int id) {
    super(p, id);
  }

	
	public void interpret() {
		super.interpret();
		
		String id = name + "-" + currentCondition.get();
		
		// If this is the "events" special variable, handle it differently
		if ("events".equals(name)) {
			Set<String> eventNames = eventset.get();
			HashSet<Event> events = new HashSet<Event>();
			// Cycle through the events in the eventset and get the current event 
			// for the listener
			for (Iterator<String> i=eventNames.iterator(); i.hasNext();) {
				String ename = i.next();
				Event next = bus.listeners.get(ename).getEvent();
				if (next != null) events.add(next);
			}
			// push the events onto the stack for use by things like foreach
			stack.push(events);
		// Otherwise, check to see if the variable name is valid
		} else if (symtable.containsKey(name)) {
			Object variable = symtable.get(name);
			// If a member is specified (ie varname.member), get the member field from the object
			if (member != null) {			
				Object value = expandMember(variable);
				stack.push(value);
			} else			
			// If we're in a condition, generate a timed condition for any listeners
			if (currentCondition.get() != null) {
				if (bus.listeners.containsKey(name)) {
					TimedCondition condition = new TimedCondition((Listener) bus.listeners.get(name));
					condition.setTimeout(currentTimeout);
					bus.conditions.put(id, condition);
					stack.push(condition);
				// If we're in a condition, and the variable is not a listener
				// and not a condition, then it's an error
				} else if (! bus.conditions.containsKey(id)) {
					throw new RuntimeException("Invalid variable used in condition statement: " + name);
				}
			// Otherwise, they just want the object referenced by the variable
			} else {
				stack.push(variable);
			}
		} else {
			throw new RuntimeException("Unrecognized Variable: " + name);
		}
	}

	/**
	 * This method takes a variable from the symtable and accesses a field 
	 * referenced by the member (ie variable.member).  Any field accessed 
	 * this way must be default (package-scoped).
	 */
	private Object expandMember(Object variable) {
		Object value = null;
		try {
			value = PropertyUtils.getSimpleProperty(variable, member);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return value;
	}

	// If a VarExpr is being accessed in a conditional, it has to be either a condition or a Boolean
	public Boolean isTriggered() {
		Boolean isTriggered = Boolean.FALSE;
		String id = name + "-" + currentCondition.get();
		// Either it's a condition
		if (bus.conditions.containsKey(id)) {
			Condition condition = (Condition) bus.conditions.get(id);
			isTriggered = condition.isTriggered();
		// Or it's a Boolean
		} else {
			if (symtable.containsKey(name)) {
				Object variable = symtable.get(name);
				Boolean object = null;
				try {
					// Get the member field if that's what's being referenced
					if (member != null) {
						object = (Boolean) expandMember(variable);	
					} else {
						object = (Boolean) variable;
					}
				} catch (ClassCastException e) {
					throw new RuntimeException("Variable " + name + ((member!=null) ? "." + member : "") + " is not a Boolean!");
				}
				return object.booleanValue();
			}
			throw new RuntimeException("Variable " + name + " was not found in the conditions table.");
		}
		return isTriggered;
	}
  
	public String interpretAsString() {	
		// the VarExpr() production has either a single <ID>, or is of the form <ID>.<ID>
		interpret();
		Object value = stack.pop();
		return value.toString();
	}
	public HashSet<String> getEventSet() {
		HashSet<String> eventSet = new HashSet<String>();
		eventSet.add(name);
		return eventSet;
	}
}