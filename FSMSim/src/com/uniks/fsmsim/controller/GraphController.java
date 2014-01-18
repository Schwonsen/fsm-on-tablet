package com.uniks.fsmsim.controller;

import java.util.ArrayList;
import java.util.List;

import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.data.State;

public class GraphController {
	private fsmType curType;
	private int curInputCount;
	private int curOuputCount;
	
	private List<State> stateList = new ArrayList<State>();
	
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
	public GraphController(fsmType type, int inputs, int outputs){
		this.curType = type;
		this.curInputCount = inputs;
		this.curOuputCount = outputs;
	}
}
