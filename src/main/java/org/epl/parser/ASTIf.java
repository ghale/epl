package org.epl.parser;

/* Generated By:JJTree: Do not edit this line. ASTIf.java */

/**
 * Represents the If production in the EPL grammar.
 * 
 * <pre>
 * If ::= "if" {@link ASTExpr Expr} {@link ASTBlock Block}
 * </pre> 
 * 
 * 
 * @see ASTExpr
 * @see ASTBlock
 */
public class ASTIf extends SimpleNode {
  public ASTIf(int id) {
    super(id);
  }

  public ASTIf(EplParser p, int id) {
    super(p, id);
  }
  
  /**
   *  If the first child, {@link ASTExpr}, evaluates to true, then the {@link ASTBlock} is interpreted.
   */
  
  public void interpret() {
      super.interpret();
	  
      // If() contains an Expr() and a Block()
      // If the Expr() evaluates to true (isTriggered is true?), interpret the Block()
      if (((ASTExpr)jjtGetChild(0)).isTriggered()) {
        jjtGetChild(1).interpret();
      }
  }

}
