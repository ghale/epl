package org.epl.parser;
/* Generated By:JJTree: Do not edit this line. ASTExecute.java */

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Runtime;
import java.util.ArrayList;

import org.epl.core.Result;

/**
 * Represents the Execute production in the EPL grammar.
 *
 * <pre>
 * Execute ::= "execute" &lt;ID&gt; "as" &lt;STRING&gt; [ {@link ASTParams Params} 
 * </pre>
 * An example EPL execute statement:
 * <p>
 * <code>execute "/usr/bin/ls" "-l"</code>
 * @see ASTParams
 */
public class ASTExecute extends SimpleNode {
  String literalString;
  String returnVar;
  
  public ASTExecute(int id) {
    super(id);
  }

  public ASTExecute(EplParser p, int id) {
    super(p, id);
  }

  /**
   * This is the main interpretation method.  It evaluates the single &lt;STRING&gt; token, which is an 
   * operating system command name, and the single {@link ASTParams} child node.  The ASTParams child node returns 
   * an ArrayList of strings.  The command name and result of the Params are put into a single array 
   * of Strings and passed to java.lang.Runtime.exec() to execute on the operating system.
   */
  
  public void interpret() {
    super.interpret();		
		
    
    // Execute() contains the name of a command, and a number of parameters as children   
    // The first child is a <STRING>
    String commandName = literalString.substring(1, literalString.length() - 1);
    
    // Everything else comes from the single Params() child
    ArrayList<String> params = ((ASTParams)jjtGetChild(0)).interpretAsStrings();
    
    // Prepend the command name into the array list; this is how Runtime.exec() must be called
    params.add(0, commandName);  
    
    String[] args = new String[params.size()];
    for (int i=0; i<params.size(); i++) args[i] = params.get(i);

    // Execute the command on the operating system	
    Runtime rt = java.lang.Runtime.getRuntime();
    try {
    	// convert the ArrayList to an array of String
        //Process process = rt.exec((String[])params.toArray(new String[params.size()]));
    	Process process = rt.exec(args);
    	// Spawn threads to gather the stdout and stderr of the process
        StreamThread output = new StreamThread(process.getInputStream());
        StreamThread error = new StreamThread(process.getErrorStream());
        output.start();
        error.start();
        
        // Wait for the process and I/O threads to complete
        try {
			process.waitFor();
			output.join();
			error.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
		// Create a result object to contain the result data
		Result result = new Result();
		result.setSuccess((process.exitValue() == 0));
		result.setOutput(output.toString());
		result.setError(error.toString());
		
		// Place the result in the local variable specified in the execute statement 
		symtable.putLocal(returnVar, result);
    }
    catch (IOException e) {
    	System.out.println("Exception running commmand in response to execute " + commandName + " " + params);
    	e.printStackTrace();
    }
  }

  /**
   * StreamThread is a class for reading output streams from processes and gathering the
   * characters printed.  This is attached to the stdout and stderr of a process being
   * executed and captures the results.
   *
   */
private class StreamThread extends Thread {
	  StringBuffer s = new StringBuffer();
	  private BufferedInputStream stream;

	  public StreamThread(InputStream stream) {
		super();
		this.stream = new BufferedInputStream(stream);
	  }

	
	public void run() {
		byte[] buffer = new byte[1024];
		try {
			// As data arrives on the stream, store it in the string buffer
			for (int count = stream.read(buffer); count != -1; count = stream.read(buffer)) {
				s.append(new String(buffer, 0, count));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public String toString() {
		return s.toString();
	}
	   
  }
}
