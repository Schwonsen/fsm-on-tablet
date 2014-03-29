package com.uniks.fsmsim.data;

import java.io.Serializable;

public class TransitionValue implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String value;
	private String output;
	
	public String getValue() {
		return value;
	}

	public String getOutput() {
		return output;
	}
	
	public TransitionValue(String value, String output) {
		this.output = output;
		this.value = value;
	}
}