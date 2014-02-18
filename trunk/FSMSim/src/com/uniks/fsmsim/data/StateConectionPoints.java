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
	
	public List<PointF> getConnectionPoints() {
		return connectionPoints;
	}
	
	public StateConectionPoints(float radius, int count, PointF statePoint){
		occupied = new boolean[count];
		transitions = new Transition[count];
		
		double slice = 2 * Math.PI / count;
		for (int i = 0; i < count; i++) {
			double angle = slice * i;
			PointF p = new PointF();
			p.x = (float)(statePoint.x + radius * Math.cos(angle));
			p.y = (float)(statePoint.y + radius * Math.sin(angle));
			occupied[i] = false;
			connectionPoints.add(p);
		}
	}
	public int getFreeIndexNearTo(PointF p){
		int index = 0, nearIndex = -1;
		float xDist = 0, yDist = 0, sumDist = 0, nearSumDist = -1;
		for (PointF point : connectionPoints) {
			if(occupied[index])continue;
			xDist = Math.abs(point.x - p.x);
			yDist = Math.abs(point.y - p.y);
			sumDist = xDist + yDist;
			
			if(nearSumDist == -1){
				nearSumDist = sumDist;
				nearIndex = index;
			}
			else if(sumDist < nearSumDist){
				nearIndex = index;
				nearSumDist = sumDist;
			}
			index++;
		}
		//returns -1 if no free Point available
		return nearIndex;
	}
	
	public boolean occupyConnectionPoint(int index, Transition t){
		if(occupied.length <= index)return false;
		if(occupied[index]) return false;
		if(transitions.length <= index)return false;
		
		occupied[index] = true;
		transitions[index] = t;
		return true;
	}
	
	public PointF getPointfrom(Transition t){
		int index = 0;
		for(Transition transition : transitions){
			if(transition == null)continue;
			if(transition.getID() == t.getID()){
				return connectionPoints.get(index);
			}
			index++;
		}
		return null;
	}
	
	public void setPoint(Transition t){
		
	}

}
