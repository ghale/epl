package org.epl.core;
import org.epl.parser.ASTAction;
import org.epl.parser.ASTCondition;
import org.epl.parser.ASTList;
import org.epl.parser.ASTSituation;


/**
 * Represents a <em>situation</em> in EPL.  A situation is defined with syntax like:
 * <p>
 * <code>when somecondition do someaction, someotheraction;</code> 
 * <p>

 * <em>Situations</em> execute actions in response to a particular condition being met.
 * 
 * @see ASTSituation
 * @see ASTCondition
 * @see ASTList
 * @see ASTAction
 */
public class Situation {
	private Condition condition;
	private Action list;

	public Situation(Condition condition) {
		super();
		this.condition = condition;
	}
	
	/**
	 * This method is called when a situation is <em>triggered</em>, that is,
	 * when the {@link Condition} associated with the system has determined
	 * that the condition has been met (through its {@link Condition#isTriggered()} method)
	 */
	public void trigger() {
		list.execute();
	}

	/**
	 * Each Situation has a condition associated with it.  Once this condition evaluates to
	 * True, the Situation is triggered.  This method gets the Condition.
	 * @return the Condition associated with this situation
	 */
	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public Action getList() {
		return list;
	}

	public void setList(Action list) {
		this.list = list;
	}
	
}
