package com.uniks.fsmsim.controller;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;

import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.data.State;
import com.uniks.fsmsim.data.StateConectionPoints;
import com.uniks.fsmsim.data.Transition;

public class GraphController {
	private fsmType curType;
	private int curInputCount;
	private int curOuputCount;
	
	private List<State> stateList = new ArrayList<State>();
	private List<Transition> transitionList = new ArrayList<Transition>();
	
	int display_height;
	int display_width;
	int stateIndex = 0;
	int transitionIndex = 0;
	
	
	//Getter Setter
	public List<State> getStateList() {
		return stateList;
	}
	public void setStateList(List<State> stateList) {
		this.stateList = stateList;
	}
	public fsmType getCurrentType() {
		return curType;
	}
	public void setCurrentType(fsmType currentType) {
		this.curType = currentType;
	}
	public int getInputCount() {
		return curInputCount;
	}
	public void setInputCount(int inputCount) {
		this.curInputCount = inputCount;
	}
	public int getOuputCount() {
		return curOuputCount;
	}
	public void setOuputCount(int ouputCount) {
		this.curOuputCount = ouputCount;
	}
	public List<Transition> getTransitionList() {
		return transitionList;
	}
	public void setTransitionList(List<Transition> transitionList) {
		this.transitionList = transitionList;
	}
	//Constructor
	public GraphController(fsmType type, int inputs, int outputs){
		this.curType = type;
		this.curInputCount = inputs;
		this.curOuputCount = outputs;
	}
	
	//add a new state
	public void addState(String name, String stateOutput, boolean isStart, boolean isEnd, float x, float y){
		State state = new State(curType, name, stateIndex);
		state.setInputCount(curInputCount);
		state.setOutputCount(curOuputCount);
		state.setStateOutput(stateOutput);
		state.setType(curType);
		state.setX(x);
		state.setY(y);
		state.setScp(new StateConectionPoints(40f, curInputCount+curOuputCount));//TODO
		stateList.add(state);
		stateIndex++;
	}
	
	//add a new Transition
	//returns true if successful
	public boolean addTransition(State from, State to, String output, String value){
		Transition t = new Transition(from,to,output,value,transitionIndex);
		
		for(Transition t1 : transitionList){
			if(t1.getState_from().getID() == t.getState_from().getID()
					&& t1.getState_to().getID() == t.getState_to().getID()){
				transitionList.remove(t1);
			System.out.println("App:\tFound duplicate transition, delete old");
			}
			if(t1.getState_from().getID() == t.getState_to().getID()
				&& t1.getState_to().getID() == t.getState_from().getID()){
					t.setTwoSidedWith(t1);
					t1.setTwoSidedWith(t);
				}
		}
		int scp_index = t.getState_from().getScp().getFreeIndexNearTo(
				new PointF(t.getState_to().getX(), t.getState_to().getY()));
		if(!from.getScp().occupyConnectionPoint(scp_index, t))
			return false;
		scp_index = t.getState_to().getScp().getFreeIndexNearTo(
				new PointF(t.getState_from().getX(), t.getState_from().getY()));
		if(!to.getScp().occupyConnectionPoint(scp_index, t))
			return false;
		
		transitionList.add(t);
		transitionIndex++;
		return true;
	}
	
	public void setSingleStartState(int index){
		if(stateList.size()-1 < index) return;
		for (State  s : stateList) {
			s.setStartState(false);
		}
		stateList.get(index).setStartState(true);
	}
	
	public void setSingleEndState(int index){
		if(stateList.size()-1 < index) return;
		for (State  s : stateList) {
			s.setEndState(false);
		}
		stateList.get(index).setEndState(true);
	}
	
	public int getDisplay_height() {
		return display_height;
	}
	public void setDisplay_height(int display_height) {
		this.display_height = display_height;
	}
	public int getDisplay_width() {
		return display_width;
	}
	public void setDisplay_width(int display_width) {
		this.display_width = display_width;
	}
	public void deSelectAll(){
		for (State  s : stateList) {
			s.setSelected(false);
		}
	}
	
	public State getSelected(){
		for (State  s : stateList) {
			if(s.isSelected())
				return s;
		}
		return null;
	}
}
