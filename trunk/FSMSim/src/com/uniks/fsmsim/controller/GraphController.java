package com.uniks.fsmsim.controller;

import java.util.ArrayList;
import java.util.List;

import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.data.State;
import com.uniks.fsmsim.data.Transition;

public class GraphController {
	private fsmType curType;
	private int curInputCount;
	private int curOuputCount;
	
	private List<State> stateList = new ArrayList<State>();
	private List<Transition> transitionList = new ArrayList<Transition>();
	
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
		State state = new State(curType, name);
		state.setInputCount(curInputCount);
		state.setOutputCount(curOuputCount);
		state.setStateOutput(stateOutput);
		state.setType(curType);
		state.setX(x);
		state.setY(y);
		stateList.add(state);
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
