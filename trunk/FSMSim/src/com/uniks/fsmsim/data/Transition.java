package com.uniks.fsmsim.data;

public class Transition {
	private State state_from, state_to;
	private String transitionOutput = "";
	private String value;
	
	public State getState_from() {
		return state_from;
	}
	public void setState_from(State state_from) {
		this.state_from = state_from;
	}
	public State getState_to() {
		return state_to;
	}
	public void setState_to(State state_to) {
		this.state_to = state_to;
	}
	public String getTransitionOutput() {
		return transitionOutput;
	}
	public void setTransitionOutput(String transitionOutput) {
		this.transitionOutput = transitionOutput;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public Transition(State from, State to, String output, String value){
		this.state_from = from;
		this.state_to = to;
		this.transitionOutput = output;
		this.value = value;
	}
}
