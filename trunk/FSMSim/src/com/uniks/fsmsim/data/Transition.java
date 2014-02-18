package com.uniks.fsmsim.data;

public class Transition {
	private int ID;
	private State state_from, state_to;
	private String transitionOutput = "";
	private String value;
	
	private boolean isTwoSided = false;
	private Transition twoSidedWith = null;
	
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
	
	public Transition getTwoSidedWith() {
		return twoSidedWith;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public void setTwoSidedWith(Transition twoSidedWith) {
		this.twoSidedWith = twoSidedWith;
		if(twoSidedWith != null)
			isTwoSided = true;
		else isTwoSided = false;
	}
	public Transition()
	{
		
	}

	public Transition(State from, State to, String output, String value, int ID){
		this.state_from = from;
		this.state_to = to;
		this.transitionOutput = output;
		this.value = value;
		this.ID = ID;
	}
}
