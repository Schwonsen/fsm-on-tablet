package com.uniks.fsmsim.data;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.graphics.PointF;

public class StateConectionPoints {
	private float distanceAngle;
	private List<PointF> connectionPoints = new ArrayList<PointF>();
	private boolean[] occupied;
	private Transition[] transitions;
	
	public StateConectionPoints(float radius, int count){
		occupied = new boolean[count];
		transitions = new Transition[count];
		
		distanceAngle = (float)(2*Math.PI*count);
		
		for (int i = 0; i < count; i++) {
			PointF p = new PointF();
			p.x =(float)(Math.cos(i*distanceAngle)*radius);
			p.y =(float)(Math.sin(i*distanceAngle)*radius);
			
			occupied[i] = false;
			connectionPoints.add(p);
		}
	}
	
	public void setPoint(Transition t){
		
	}
	

}
