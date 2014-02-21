package com.uniks.fsmsim.data;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.graphics.PointF;

public class StateConectionPoints {
	private State attachedState;
	private float distanceAngle;
	private List<PointF> connectionPoints = new ArrayList<PointF>();
	private boolean[] occupied;
	private Transition[] transitions;
	


	private float radius;
	private int count;
	
	public int getCount() {
		return count;
	}

	public List<PointF> getConnectionPoints() {
		return connectionPoints;
	}
	
	public StateConectionPoints(float radius, int count, State state){
		this.attachedState = state;
		occupied = new boolean[count];
		transitions = new Transition[count];
		this.radius = radius;
		this.count = count;
		calcConnectionPoints(state.getPoint());
	}
	public void calcConnectionPoints(PointF statePoint){
		connectionPoints.clear();
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
	public void refreshTransitionConnections(){
		List<Transition> backupTransitions = new ArrayList<Transition>();
		for(Transition t1: transitions){
//			if(!backupTransitions.contains(t1))
				backupTransitions.add(t1);
		}
		
		this.occupied = new boolean[count]; 
		this.transitions = new Transition[count];
		
		List<State> connectedStates = new ArrayList<State>();
		
		for(Transition t1: backupTransitions){
			if(t1 != null){
				if(t1.isBackConnection()){
					Point p = t1.getState_to().getScp().getPointWithNearDistance();
					occupyConnectionPoint(p.x, t1);
					occupyConnectionPoint(p.y, t1);
					t1.setPointFrom(connectionPoints.get(p.x));
					t1.setPointTo(connectionPoints.get(p.y));
				}else{
					if(t1.getState_from().getID() == attachedState.getID()){
						int i= getFreeIndexNearTo(t1.getPointTo());
						if(connectedStates.contains(t1.getState_to())){
							i = getPointWithNearDistance(i);
						}else{
							connectedStates.add(t1.getState_to());
							occupyConnectionPoint(i,t1);
						}
						t1.setPointFrom(connectionPoints.get(i));
					}
					if(t1.getState_to().getID() == attachedState.getID()){
						
						int i = getFreeIndexNearTo(t1.getPointFrom());
						
						if(connectedStates.contains(t1.getState_to())){
							i = getPointWithNearDistance(i);
						}else{
							connectedStates.add(t1.getState_from());
							occupyConnectionPoint(i,t1);
						}
						t1.setPointTo(connectionPoints.get(i));
					}
				}
			}
		}
	}

	
	public void refreshTransitionConnections2(){
		refreshTransitionConnections();
		for(Transition t1: transitions){
			if(t1 == null)continue;
			if(t1.getState_from().getID() != attachedState.getID()){
				t1.getState_from().getScp().refreshTransitionConnections();
			}
			if(t1.getState_to().getID() != attachedState.getID()){
				t1.getState_to().getScp().refreshTransitionConnections();
			}
		}
	}
	
	public int getFreeIndexNearTo(PointF p){
		int index = 0, nearIndex = -1;
		float xDist = 0, yDist = 0, sumDist = 0, nearSumDist = -1;
		for (PointF point : connectionPoints) {
			if(!occupied[index]){
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
			}
			index++;
		}
		//returns -1 if no free Point available
		return nearIndex;
	}
	
	public boolean occupyConnectionPoint(int index, Transition t){
		if(occupied.length <= index || index < 0)return false;
		if(occupied[index]) return false;
		if(transitions.length <= index) return false;
		
		occupied[index] = true;
		transitions[index] = t;
		return true;
	}

	public void freeConnectionPoint(Transition t){
		int index = 0;
		for (Transition t1 : transitions) {
			if (t1 != null) {
				if (t1.getID() == t.getID()) {
					occupied[index] = false;
					transitions[index] = null;
				}
			}
		}
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
	public Transition[] getTransitions() {
		return transitions;
	}
	

	public Point getPointWithNearDistance(){
		int neededDistance = (int)(count / 5);
		int newNeededDist = neededDistance;
		int firstFound = -1;
		int distCouter = 0;
		for(int i = 0; i<neededDistance;i++){
			for(int index = 0; index < occupied.length; index++){
				if(firstFound == -1){
					if(!occupied[index]){
						firstFound = index;
					}
				}else{
					//reached required distance
					if(distCouter == newNeededDist){
						return new Point(firstFound, index);
					}
					//free space
					else if(!occupied[index]){
						distCouter++;
					}
					//found occupied
					else{
						distCouter = 0;
						firstFound = -1;
					}
				}
			}	
			newNeededDist--;
		}
		return new Point(-1,-1);
	}
	public int getPointWithNearDistance(int index){
		int neededDistance = (int)(count / 5);
		int indexToReturn;
		if(index+neededDistance < (count)){
			indexToReturn = index+neededDistance;
		}else{
			indexToReturn = (index+neededDistance) - count;
		}
		for(int i = 0; i < count-indexToReturn; i++){
			if(!occupied[indexToReturn])
				return indexToReturn;
			indexToReturn++;
		}
		return -1;
	}

}
