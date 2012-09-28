package org.epl.parser;


/**
 * The main class for the EPL implementation.  This class reads and parses the EPL source file.  Invoke with:
 * <p>
 * <pre>
 * java EPL sourcefile.epl
 * </pre>
 * <p> 
 * where <code>sourcefile.epl</code> specifies the filename of the EPL source code.
 *
 */
public class EPL {

  public static void main(String args[]) {
    EplParser parser;
    if (args.length == 1) {
      System.out.println("Event Processing Language Interpreter Version 0.1:  Reading from file " + args[0] + " . . .");
      try {
        parser = new EplParser(new java.io.FileInputStream(args[0]));
      } catch (java.io.FileNotFoundException e) {
        System.out.println("Event Processing Language Interpreter Version 0.1:  File " + args[0] + " not found.");
        return;
      }
    } else {
      System.out.println("Event Processing Language Interpreter Version 0.1:  Usage :");
      System.out.println("         java EPL inputfile");
      return;
    }
    try {
      // Parse	
      parser.S();
      // Interpret
      parser.jjtree.rootNode().interpret();
      // Wait forever...
      while (true) Thread.sleep(10000);
    } catch (ParseException e) {
      System.out.println("Event Processing Language Interpreter Version 0.1:  Encountered errors during parse.");
      e.printStackTrace();
    } catch (Exception e1) {
      System.out.println("Event Processing Language Interpreter Version 0.1:  Encountered errors during interpretation/tree building.");
      e1.printStackTrace();
    }
  }
}
