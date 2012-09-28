package org.epl.core;
import java.util.Hashtable;
import java.util.Stack;


/**
 * The Symtable class represents a symbol table in the language with support for 
 * nested scopes.  There is a global variable space that can be seen from any scope, a
 * local variable space that is only accessible from the current block, and a stack of 
 * scopes that provide access to variables defined in parent scopes.  Nested scopes can 
 * see out (ie into scopes in outer blocks) but outer blocks cannot see into nested 
 * scopes.
 *
 */
public class Symtable {
	// The local scope is ThreadLocal so that concurrently running actions don't step on each other
	private ThreadLocal<Hashtable<String, Object>> local = new ThreadLocal<Hashtable<String, Object>>() {
		
		protected Hashtable<String, Object> initialValue() {
			return new Hashtable<String, Object>();
		} 
	};
	// Scopes from outer blocks are also ThreadLocal so that there is no cross-block corruption 
	private ThreadLocal<Stack<Hashtable<String, Object>>> scopes = new ThreadLocal<Stack<Hashtable<String, Object>>>() {
		
		protected Stack<Hashtable<String, Object>> initialValue() {
			return new Stack<Hashtable<String, Object>>();
		} 
	};
	// Global variable space is visible everywhere
	private Hashtable<String, Object> global = new Hashtable<String, Object>();
	
	public Symtable() {
		super();
	}

	// Push the local scope onto the stack and create a new local scope.
	// Used when entering a new block.
	public void pushScope() {
		Stack<Hashtable<String, Object>> s = scopes.get();
		Hashtable<String, Object> l = local.get();
		s.push(l);
		local.set(new Hashtable<String, Object>());
	}
	
	// Discard the local scope and pop the scope from the outer block back as the local scope.
	// Used when leaving a nested block.
	public void popScope() {
		Stack<Hashtable<String, Object>> s = scopes.get();
		Hashtable<String, Object> l = s.pop();
		local.set(l);
	}

	// Get a variable from the most appropriate scope.  Start by looking in the local scope,
	// check all nested scopes and finally check the global scope.
	public synchronized Object get(Object key) {
		Hashtable<String, Object> l = local.get();
		if (l.containsKey(key)) return l.get(key);
		Stack<Hashtable<String, Object>> s = scopes.get();
		// Last element on the stack is the most recent outer block.
		for (int i=s.size()-1; i>-1; i--) {
			Hashtable<String, Object> next = s.get(i);
			if (next.containsKey(key)) return next.get(key);
		}
		return global.get(key);
	}
	
	// Check to see if it's a local variable
	public boolean containsKeyLocal(String key) {
		Hashtable<String, Object> l = local.get();
		return l.containsKey(key);
	}
	
	// Check to see if the variable is defined in any scope.
	public boolean containsKey(String key) {
		Hashtable<String, Object> l = local.get();
		if (! (l.containsKey(key) || global.containsKey(key))) {
			Stack<Hashtable<String, Object>> s = scopes.get();
			for (int i=s.size()-1; i>-1; i--) {
				Hashtable<String, Object> next = s.get(i);
				if (next.containsKey(key)) return true;
			}
		} else {
			return true;
		}
		
		return false;
	}
	
	// Put the variable in the global scope.
	public synchronized void put(String key, Object value) {
		global.put(key, value);
	}
	
	// Put the variable in the local scope.
	public synchronized void putLocal(String key, Object value) {
		Hashtable<String, Object> l = local.get();
		l.put(key, value);
	}

	// Remove the variable from the local scope
	public void removeLocal(String key) {
		Hashtable<String, Object> l = local.get();
		l.remove(key);
	}
	
}
