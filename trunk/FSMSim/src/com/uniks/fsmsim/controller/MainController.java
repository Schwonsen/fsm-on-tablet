package com.uniks.fsmsim.controller;



public class MainController {
	public enum fsmType{Mealy,Moore}
	
	private fsmType curType = fsmType.Mealy;
	private int curInputCount = 0;
	private int curOuputCount = 0;
	
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
	

	
	public MainController(){
		
	
	}
	
}
