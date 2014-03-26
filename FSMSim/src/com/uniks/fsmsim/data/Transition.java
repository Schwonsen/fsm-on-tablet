package com.uniks.fsmsim.data;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;

public class Transition {
	private int ID;
	private State state_from, state_to;
	private List<TransitionValue> valueList = new ArrayList<TransitionValue>();
	private PointF pointFrom = null, pointTo = null;
	private PointF dragPoint;
	private boolean isBackConnection = false;

	private boolean isSelected = false;
	private boolean isMarkedAsDeletion = false;
	private PointF notationPoint = null;
	
	private boolean inSimulation = false;
	private boolean possibleSimulation = false;
	

	//getter/setter
	public boolean isPossibleSimulation() {
		return possibleSimulation;
	}
	public void setPossibleSimulation(boolean possibleSimulation) {
		this.possibleSimulation = possibleSimulation;
	}
	public boolean isInSimulation() {
		return inSimulation;
	}
	public void setInSimulation(boolean inSimulation) {
		this.inSimulation = inSimulation;
	}
	public PointF getNotationPoint() {
		return notationPoint;
	}
	public void setNotationPoint(PointF notationPoint) {
		this.notationPoint = notationPoint;
	}
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
	public List<TransitionValue> getValueList() {
		return valueList;
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
		output = reziseToMax(output, state_from.getOutputCount());
		value = reziseToMax(value, state_from.getInputCount());
		valueList.add(new TransitionValue(value, output));
		this.ID = ID;
		float distX = (state_to.getX() - state_from.getX())/2;
		float distY = (state_to.getY() - state_from.getY())/2;
		if(!isBackConnection)
			dragPoint = new PointF(state_to.getX()-distX,state_to.getY()-distY);
	}
	
	public void addValueOutput(String value, String output){
		output = reziseToMax(output, state_from.getOutputCount());
		value = reziseToMax(value, state_from.getInputCount());
		valueList.add(new TransitionValue(value, output));
	}
	
	public String getMealyNotification(){
		String text = "";
		boolean gotFirst = false;
		for(TransitionValue tv : valueList){
			if(gotFirst){
				text += " , " + tv.getValue() + "/" + tv.getOutput();
			}else{
				gotFirst = true;
				text = tv.value + "/" + tv.getOutput();
			}
		}
		return text;
	}
	
	public String getMooreNotification(){
		String text = "";
		boolean gotFirst = false;
		for(TransitionValue tv : valueList){
			if(gotFirst){
				text += " , " + tv.getValue();
			}else{
				gotFirst = true;
				text = tv.value;
			}
		}
		return text;
	}
	
	public boolean isMarkedAsDeletion() {
		return isMarkedAsDeletion;
	}
	public void setMarkedAsDeletion(boolean isMarkedAsDeletion) {
		this.isMarkedAsDeletion = isMarkedAsDeletion;
	}
	
	public static String reziseToMax(String input, int max){
		if(input == null || input.equals(""))return "";
		String result = input;
		if(result.length() < max){
			while(result.length() < max){
				result = "0"+result;
			}
		}
		if(result.length() > max)
			result = result.substring(result.length()-max,result.length());
		
		return result;
	}
	
	public String getOutputFromValue(String value){
		for(TransitionValue tv : valueList){
			if(tv.value.equals(value)){
				return tv.output;
			}
		}
		return "";
	}
	
	public class TransitionValue{
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
	
	public void moveDragPointOnNotification(PointF toPoint){
		//moving vector
		PointF vecMove = new PointF(toPoint.x - dragPoint.x, toPoint.y - dragPoint.y);
		
		//move dragpoints
		vecMove.x *= 0.5;
		vecMove.y *= 0.5;
		
		dragPoint.x += vecMove.x;
		dragPoint.y += vecMove.y;
	}
}
