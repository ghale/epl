package org.epl.parser;

import java.util.HashSet;
import java.util.Stack;

import org.epl.core.EventBus;
import org.epl.core.Symtable;

/**
 * MyNode represents a customization to the Node interface for the parser.  All nodes
 * in the parse tree extend MyNode.  
 *
 */
public class MyNode {
	
  /** Symbol table */	 
	protected static Symtable symtable = new Symtable();
	
	// The Event bus 
	protected static EventBus bus = EventBus.getBus();

  /** Stack for logic and data passing between nodes. */
  protected static Stack<Object> stack = new Stack<Object>();
  
  // This ThreadLocal eventset contains the current eventset associated with a given condition.
  // This is ThreadLocal so that every situation (each running in a thread) will have its own view
  // of the events associated with it.  Since each situation fires off an evaluation of the 
  // condition it is based on, the eventset will be different for each situation.
  protected static ThreadLocal<HashSet<String>> eventset = new ThreadLocal<HashSet<String>>();
	  										
  // These variables hold information about the current condition being evaluated
  protected static Long currentTimeout;
  protected static ThreadLocal<String> currentCondition = new ThreadLocal<String>();

  public void interpret() {
	  if (EplParser.debug) System.out.println("In interpret() of " + this.getClass().getName());
    // throw new UnsupportedOperationException(); // It better not come here.
  }
  

}
