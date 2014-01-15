package com.uniks.fsmsim.controller;

public class MainController {
	
	public enum fsmType{
			Mealy(0),Moore(1);
			
			private int value;
			
			private fsmType(int value) {
				this.value = value;
				System.out.println("value: " + value);
			}
			
			public int getValue() {
				return value;
			}
			
			public static fsmType getEnumByValue(int value) {
				   if(value == 0)return Mealy;
				   else return Moore;
				}
		}
	
	private fsmType curType;
	private int curInputCount;
	private int curOuputCount;
	
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
	
	//Constructor
	public MainController(){
		
	
	}
	
}
