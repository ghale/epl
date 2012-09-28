package org.epl.parser;
import org.epl.core.Action;
import org.epl.core.Condition;
import org.epl.core.Situation;

/* Generated By:JJTree: Do not edit this line. ASTSituation.java */

/**
 * Represents the Situation production in the EPL grammar.
 * <pre>
 * Situation ::= "when" &lt;ID&gt; "do" {@link ASTList List}
 * </pre>
 * @see ASTList
 */
public class ASTSituation extends SimpleNode {
	String condition;
	
  public ASTSituation(int id) {
    super(id);
  }

  public ASTSituation(EplParser p, int id) {
    super(p, id);
  }

	
	public void interpret() {
		super.interpret();
		// Make sure the condition is valid
		Condition c = (Condition) bus.conditions.get(condition);
		if (c == null) {
			throw new RuntimeException("Invalid condition name in situation statement: " + condition);
		}
		
		// Create a new situation based on the referenced condition
		Situation situation = new Situation(c);
		// Set up the action list
		Action list = (Action) jjtGetChild(0);
		jjtGetChild(0).interpret();
		situation.setList(list);
		// Register the situation with the bus
		bus.registerSituation(situation);
	}
}