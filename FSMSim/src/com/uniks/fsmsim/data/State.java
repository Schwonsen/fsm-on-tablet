package com.uniks.fsmsim.data;

import com.uniks.fsmsim.controller.MainController.fsmType;

public class State {
	private fsmType type;
	private int inputCount = 0;
	private int outputCount = 0;
	private String stateOutput = "";
	private String name = "";
	
	//drawInformations
	private float x = 0f;
	private float y = 0f;
	private float radius = 50f;
	
	boolean isStartState = false;
	boolean isEndState = false;
	
	//Getter Setter
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
		this.stateOutput = stateOutput;
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
	public float getRadius() {
		return radius;
	}
	public void setRadius(float radius) {
		this.radius = radius;
	}

	//Constructor
	public State(fsmType type, String name){
		this.type = type;
		this.name = name;
	}
}
