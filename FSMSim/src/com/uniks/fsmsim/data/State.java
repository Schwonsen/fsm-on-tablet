package com.uniks.fsmsim.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Point;
import android.graphics.PointF;

import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.data.TransitionValue;

public class State {
	private int ID;
	private fsmType type;
	private int inputCount = 0;
	private int outputCount = 0;
	private String stateOutput = "";
	private String name = "";
	
	private StateConectionPoints scp;
	boolean isStartState = false;
	boolean isEndState = false;
	boolean isSelected = false;
	
	//drawInformations
	private float x = 0f;
	private float y = 0f;
	private boolean inSimulation = false;
	
	//Getter Setter
	public boolean isInSimulation() {
		return inSimulation;
	}
	public void setInSimulation(boolean inSimulation) {
		this.inSimulation = inSimulation;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public fsmType getType() {
		return type;
	}
	public void setType(fsmType type) {
		this.type = type;
	}
	public int getInputCount() {
		return inputCount;
	}
	public void setInputCount(int inputCount) {
		this.inputCount = inputCount;
	}
	public int getOutputCount() {
		return outputCount;
	}
	public void setOutputCount(int outputCount) {
		this.outputCount = outputCount;
	}
	public String getStateOutput() {
		return stateOutput;
	}
	public void setStateOutput(String stateOutput) {
		this.stateOutput = Transition.reziseToMax(stateOutput, outputCount);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public StateConectionPoints getScp() {
		return scp;
	}
	public void setScp(StateConectionPoints scp) {
		this.scp = scp;
	}
	public boolean isStartState() {
		return isStartState;
	}
	public void setStartState(boolean isStartState) {
		this.isStartState = isStartState;
	}
	public boolean isEndState() {
		return isEndState;
	}
	public void setEndState(boolean isEndState) {
		this.isEndState = isEndState;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public PointF getPoint(){
		return (new PointF(x,y));
	}
	
	public int getCurrInputCount(){
		int count = 0;
		for(Transition t : scp.getConnectedTransitions()){
			if(t != null)
			if(t.getState_to().getID() == ID){
				count += t.getValueList().size();
			}
			
		}
		return count;
	}
	
	public int getCurrOutputCount(){
		int count = 0;
		for(Transition t : scp.getConnectedTransitions()){
			if(t != null)
			if(t.getState_from().getID() == ID){
				count += t.getValueList().size();
			}
			
		}
		return count;
	}
	public void moveState(PointF toPoint){
		//moving vector
		PointF vecMove = new PointF(toPoint.x - this.x, toPoint.y - this.y);
		
		//move state
		this.x = toPoint.x;
		this.y = toPoint.y;
		this.scp.refreshTransitionConnections();
		
		//move dragpoints
		vecMove.x *= 0.5;
		vecMove.y *= 0.5;
		
		for(Transition t :scp.getConnectedTransitions()){
			if(!t.isBackConnection()){
				t.setDragPoint(new PointF(t.getDragPoint().x + vecMove.x ,t.getDragPoint().y + vecMove.y));
			}
		}
		
		//refresh neigbours 
		for(State s : getConnectedStates()){
			if(s == null)continue;
			s.getScp().refreshTransitionConnections();
		}
	}
	public List<State> getConnectedStates(){
		List<State> newStateList = new ArrayList<State>();
		for(Transition t : scp.getConnectedTransitions()){
			if(t == null)continue;
			if(t.getState_from().getID() != this.ID)
				newStateList.add(t.getState_from());
			if(t.getState_to().getID() != this.ID)
				newStateList.add(t.getState_to());
		}
		return newStateList;
	}
	
	//checks if state has a transition with given value
	public Transition getTransitionTo(String value){
		for(Transition t : scp.getConnectedTransitions()){
			//is outgoing
			if(t.getState_from().getID() == ID)
			for(TransitionValue tv : t.getValueList()){
				if (tv.getValue().equals(value)){
					return t;
				}
				if(tv.getValue().contains("-")){
					StringBuilder sb = new StringBuilder();
					sb.append(tv.getValue());
					for (int index = 0; index < sb.length(); index++) {
					    if (sb.charAt(index) == '-') {
					        sb.setCharAt(index, value.charAt(index));
					    }
					}
					System.out.println(tv.getValue() + " to "+sb);
					if (sb.toString().equals(value)){
						return t;
					}
					
				}
			}
		}
		return null;
	}
	
	//check if outgoing value is possible
	public boolean checkForOutgoingValue(String value){
		for(Transition t : scp.getConnectedTransitions()){
			//is outgoing
			if(t.getState_from().getID() == ID)
			for(TransitionValue tv : t.getValueList()){
				//Already there
				if (tv.getValue().equals(value)){
					return false;
				}
				if(tv.getValue().contains("-")){
					StringBuilder sb = new StringBuilder();
					sb.append(tv.getValue());
					for (int index = 0; index < sb.length(); index++) {
					    if (sb.charAt(index) == '-') {
					        sb.setCharAt(index, value.charAt(index));
					    }
					}
					System.out.println(tv.getValue() + " to "+sb);
					if (sb.toString().equals(value)){
						return false;
					}
				}
				if(value.contains("-")){
					StringBuilder sb = new StringBuilder();
					sb.append(value);
					for (int index = 0; index < sb.length(); index++) {
					    if (sb.charAt(index) == '-' && tv.getValue().charAt(index) != '-') {
					    	return false;

					    }
					}
				}
			}
		}
		return true;
	}
	//check if outgoing value is possible
	public boolean checkForOutgoingValue2(String value){
		for(Transition t : scp.getConnectedTransitions()){
			List<String> valueList = new ArrayList<String>();
			//is outgoing
			if(t.getState_from().getID() == ID)
			for(TransitionValue tv : t.getValueList()){
				//Already there
				if (tv.getValue().equals(value)){
					return false;
				}else{
					List<String> al = new ArrayList<String>();
					al.add(tv.getValue());
					valueList.addAll(getPossibleValues(al));
				}
			}
			
			List<String> al = new ArrayList<String>();
			al.add(value);
			for(String s : getPossibleValues(al)){
				if(valueList.contains(s))return false;
			}
			
		}
		return true;
	}
	
	public List<String> getPossibleValues(List<String> valueList){
		if(valueList == null || valueList.size() == 0)return valueList;
		for(int indexList = 0; indexList < valueList.size(); indexList++){	
			StringBuilder sb = new StringBuilder();
			sb.append(valueList.get(indexList));
			for (int index = 0; index < sb.length(); index++) {
			    if(sb.charAt(index) == '-'){
			    	sb.setCharAt(index, '0');
			    	valueList.set(indexList, sb.toString());
			    	sb.setCharAt(index, '1');
			    	valueList.add(sb.toString());
			    	break;
			    }
			}
		}
		if(valueList.get(0).contains("-"))return getPossibleValues(valueList);
		else return valueList;
	}

	//Constructor
	public State(fsmType type, String name, int ID){
		this.type = type;
		this.name = name;
		this.ID = ID;
	}
}
