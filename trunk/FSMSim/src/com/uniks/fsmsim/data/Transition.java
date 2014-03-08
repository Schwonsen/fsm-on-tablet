package com.uniks.fsmsim.data;

import android.graphics.PointF;

public class Transition {
	private int ID;
	private State state_from, state_to;
	private String transitionOutput = "";
	private String value;
	private PointF pointFrom = null, pointTo = null;
	private PointF dragPoint;
	private boolean isBackConnection = false;
	private boolean isTwoSided = false;
	private Transition twoSidedWith = null;
	private boolean isSelected = false;
	private boolean isMarkedAsDeletion = false;
	

	public boolean isBackConnection() {
		return isBackConnection;
	}
	public void setBackConnection(boolean isBackConnection) {
		this.isBackConnection = isBackConnection;
	}
	public PointF getDragPoint() {
		return dragPoint;
	}
	public void setDragPoint(PointF dragPoint) {
		this.dragPoint = dragPoint;
	}
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
	public PointF getPointFrom() {
		return pointFrom;
	}
	public void setPointFrom(PointF pointFrom) {
		this.pointFrom = pointFrom;
	}
	public PointF getPointTo() {
		return pointTo;
	}
	public void setPointTo(PointF pointTo) {
		this.pointTo = pointTo;
	}
	public void setTwoSidedWith(Transition twoSidedWith) {
		this.twoSidedWith = twoSidedWith;
		if(twoSidedWith != null)
			isTwoSided = true;
		else isTwoSided = false;
	}

	public boolean isSelected() {
		return isSelected ;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public void moveDragPoint(PointF toPoint){
		dragPoint = toPoint;
	}

	public Transition(State from, State to, String value, String output, int ID){
		setState_from(from);
		setState_to(to);
		this.transitionOutput = output;
		this.value = value;
		this.ID = ID;
		float distX = (state_to.getX() - state_from.getX())/2;
		float distY = (state_to.getY() - state_from.getY())/2;
		if(!isBackConnection)
			dragPoint = new PointF(state_to.getX()-distX,state_to.getY()-distY);
	}
	public boolean isMarkedAsDeletion() {
		return isMarkedAsDeletion;
	}
	public void setMarkedAsDeletion(boolean isMarkedAsDeletion) {
		this.isMarkedAsDeletion = isMarkedAsDeletion;
	}
}
