package com.uniks.fsmsim.controller;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.graphics.PointF;

import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.data.State;
import com.uniks.fsmsim.data.StateConectionPoints;
import com.uniks.fsmsim.data.Transition;
import com.uniks.fsmsim.data.TransitionValue;

public class GraphController {
	private fsmType curType;
	private int curInputCount;
	private int curOuputCount;
	
	private List<State> stateList = new ArrayList<State>();
	private List<Transition> transitionList = new ArrayList<Transition>();
	
	int display_height;
	int display_width;
	int display_TopBarSize = 0;
	int display_BotBarSize = 0;
	int stateIndex = 0;
	int transitionIndex = 0;
	float stateRadius = 40f;
	
	private List<String>Statenames = new ArrayList<String>();
	
	//Getter Setter
	public List<String> getStatenames() {
		return Statenames;
	}
	public void setStatenames(List<String> statenames) {
		Statenames = statenames;
	}
	public float getStateRadius() {
		return stateRadius;
	}
	public void setStateRadius(float stateRadius) {
		this.stateRadius = stateRadius;
	}
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
	public int getDisplay_TopBarSize() {
		return display_TopBarSize;
	}
	public void setDisplay_TopBarSize(int display_barSize) {
		this.display_TopBarSize = display_barSize;
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
	public int getDisplay_BotBarSize() {
		return display_BotBarSize;
	}
	public void setDisplay_BotBarSize(int display_BotBarSize) {
		this.display_BotBarSize = display_BotBarSize;
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
		state.setEndState(isEnd);
		state.setStartState(isStart);
		state.setScp(new StateConectionPoints(stateRadius, 50, state));
		state.getScp().refreshTransitionConnections();
		stateList.add(state);
		stateIndex++;
		Statenames.add(name);
	}
	public void removeState(int index){
		for(Transition t : stateList.get(index).getScp().getConnectedTransitions()){
			removeTransition(t);
		}
		Statenames.remove(stateList.get(index).getName());
		stateList.remove(index);
	}
	//add a new Transition
	//returns true if successful
	public boolean addTransition(State from, State to, String output, String value){
		Transition t = new Transition(from,to,output,value,transitionIndex);
		
		//check for backconnection
		if(from.getID() == to.getID()){
			t.setBackConnection(true);
			//remove old before
			for(Transition t1 : to.getScp().getConnectedTransitions()){
				if(t1 != null)
				if(t1.isBackConnection()){
					removeTransition(t1);
				}
			}
		}

		from.getScp().getConnectionPoints().get(from.getScp().getNextFreeIndex()).setConnectedTransition(t, t.isBackConnection());
		to.getScp().getConnectionPoints().get(to.getScp().getNextFreeIndex()).setConnectedTransition(t, t.isBackConnection());
		from.getScp().refreshTransitionConnections();
		to.getScp().refreshTransitionConnections();
		transitionList.add(t);
		transitionIndex++;
		return true;
	}
	
	public boolean addTransition(State from, State to, List<TransitionValue> valueList, PointF dragPoint){
		Transition t = new Transition(from,to,"","",transitionIndex);
		t.setValueList(valueList);
		t.setDragPoint(dragPoint);
		
		//check for backconnection
		if(from.getID() == to.getID()){
			t.setBackConnection(true);
			//remove old before
			for(Transition t1 : to.getScp().getConnectedTransitions()){
				if(t1 != null)
				if(t1.isBackConnection()){
					removeTransition(t1);
				}
			}
		}

		from.getScp().getConnectionPoints().get(from.getScp().getNextFreeIndex()).setConnectedTransition(t, t.isBackConnection());
		to.getScp().getConnectionPoints().get(to.getScp().getNextFreeIndex()).setConnectedTransition(t, t.isBackConnection());
		from.getScp().refreshTransitionConnections();
		to.getScp().refreshTransitionConnections();
		transitionList.add(t);
		transitionIndex++;
		return true;
	}
	
	public void removeTransition(Transition t){
		if(t == null)return;
		t.getState_from().getScp().freeConnectionPoint(t);
		t.getState_to().getScp().freeConnectionPoint(t);
		transitionList.remove(t);
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
		for (Transition t : transitionList) {
			t.setSelected(false);
		}
	}
	public void unmarkDeletion(){
		for (Transition t : transitionList) {
			t.setMarkedAsDeletion(false);
		}
	}
	
	public State getSelectedState(){
		for (State  s : stateList) {
			if(s.isSelected())
				return s;
		}
		return null;
	}
	
	public boolean haveSelectedState(){
		for(State s : stateList){
			if(s.isSelected()){
				return true;
			}
		}
		return false;
	}
	
	public String getNextName(){
		String name = "s0";
		for(int i = 0; i < 999;i++){
			name = "s"+i;
			if(!Statenames.contains(name)){
				break;
			}
		}
		return name;
	}
	
	public void clear(){
		stateList = new ArrayList<State>();
		transitionList = new ArrayList<Transition>();
		stateIndex = 0;
		transitionIndex = 0;
		Statenames = new ArrayList<String>();
	}
	
	public State getStartState(){
		for(State s : stateList){
			if(s.isStartState())
				return s;
		}
		return null;
	}
	
	public void removePossibleSimulations(){
		for(Transition t : transitionList){
			t.setPossibleSimulation(false);
		}
		for(State s: stateList){
			s.setInSimulationEnd(false);
		}
	}
	
	public State getStateByName(String name){
		for(State s : stateList){
			if(s.getName().equals(name))
				return s;
		}
		return null;
	}
	
	public void checkConnections(){
		//all states
		for(State s : stateList){
			//all connected transitions
			for(Transition t : s.getScp().getConnectedTransitions()){
				//all connected transitions outgoing
				boolean isThere = false;
				if(t.getState_from().getID() == s.getID())
					//all connected transitions outgoings target
					isThere = false;
					for(Transition t2 : t.getState_to().getScp().getConnectedTransitions()){
						if(t.getID() == t2.getID())isThere = true;
					}
					if(!isThere){
						t.getState_to().getScp().occupyConnectionPoint(t.getState_to().getScp().getNextFreeIndex(), t);
					}
			}
		}
	}
}
