package org.epl.core;

/**
 * This simple class represents a result from an "execute" statement.
 *
 */
public class Result {
	private Boolean success = Boolean.FALSE;
	private String output;
	private String error;
	
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
}
