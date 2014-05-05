package com.uniks.fsmsim.data;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.graphics.PointF;

public class StateConectionPoints {
	private State attachedState;
	private float radius;
	private int count;
	private int distanceBetweenPoints;
	private List<ConnectionPoint> connectionPoints = new ArrayList<ConnectionPoint>();
	
	public int getCount() {
		return count;
	}
	
	public List<ConnectionPoint> getConnectionPoints() {
		return connectionPoints;
	}

	public StateConectionPoints(float radius, int count, State state){
		this.attachedState = state;
		this.radius = radius;
		this.count = count;
		distanceBetweenPoints = (int)(count/10);
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
			ConnectionPoint cp = new ConnectionPoint(p);
			connectionPoints.add(cp);
		}
	}
	public List<Transition> getConnectedTransitions(){
		List<Transition> list = new ArrayList<Transition>();
		for(ConnectionPoint cp : connectionPoints){
			if(cp.connectedTransition!=null)
				list.add(cp.connectedTransition);
		}
		return list;
	}
	
	public List<Transition> getOutgoingTransitions(){
		List<Transition> list = new ArrayList<Transition>();
		for(ConnectionPoint cp : connectionPoints){
			if(cp.connectedTransition!=null)
				if(cp.connectedTransition.getState_from().getID() == attachedState.getID())
					if(!list.contains(cp.connectedTransition))
						list.add(cp.connectedTransition);
		}
		return list;
	}
	
	public boolean containsTransition(Transition t){
		for(ConnectionPoint cp : connectionPoints){
			if(cp.isOccupied)
			if(cp.getConnectedTransition().getID() == t.getID()){
				return true;
			}
		}
		return false;
	}
	
	public void refreshTransitionConnections(){
		List<ConnectionPoint> backupCP = new ArrayList<ConnectionPoint>();
		
		for(ConnectionPoint cp : connectionPoints){
			if(cp.isOccupied)
				backupCP.add(cp);
		}
		
		//clear and reInit cp's
		calcConnectionPoints(attachedState.getPoint());
		
		for(ConnectionPoint bcp : backupCP){
			//check 
			if(bcp.isBackTransition){
				if(!containsTransition(bcp.getConnectedTransition())){
					//get 2x index
					Point p = bcp.getConnectedTransition().getState_to().getScp().getPointWithNearDistance();
					bcp.getConnectedTransition().setPointFrom(null);
					bcp.getConnectedTransition().setPointTo(null);
					connectionPoints.get(p.x).setConnectedTransition(bcp.getConnectedTransition(),true);
					connectionPoints.get(p.y).setConnectedTransition(bcp.getConnectedTransition(),true);
				}
				
			}else{
				int index;
				if(bcp.getConnectedTransition().getState_from().getID() == attachedState.getID()){
					index = getFreeIndexNearTo(bcp.getConnectedTransition().getPointTo());
				}else{
					index = getFreeIndexNearTo(bcp.getConnectedTransition().getPointFrom());
				}
				
				index = getIndexWithoutNears(index);
				connectionPoints.get(index).setConnectedTransition(bcp.getConnectedTransition(), false);
			}
			

		}
	}
	
	public int getNextFreeIndex(){
		int index = 0;
		for (ConnectionPoint cp : connectionPoints) {
			if(!cp.isOccupied)
				return index;
		}
		System.out.println("!Error: StateConnectionPoints getNextFreeIndex has not found a free point");
		return -1;
	}
	
	public int getFreeIndexNearTo(PointF p){
		int index = 0, nearIndex = -1;
		float xDist = 0, yDist = 0, sumDist = 0, nearSumDist = -1;
		for (ConnectionPoint cp : connectionPoints) {
			PointF point = cp.getPoint();
			if(!cp.isOccupied){
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
		if(nearIndex == -1)
			System.out.println("!Error: StateConnectionPoints getFreeIndexNearTo has not found a free point");
		return nearIndex;
	}
	
	private int getIndexShift(int start, int shift){
		int length = connectionPoints.size();
		if(start+shift >= length){
			return (start+shift) - length;
		}
		if(start+shift < 0){
			return length + (start+shift);
		}
		return start + shift;
	}
	
	private int getCountBetweenPoints(int start, int end){
		int length = connectionPoints.size();
		
		if(start < end){
			return end - start;
		}else{
			return length - start + end;
		}
	}
	
	public int getNeigbourIndex(int index){
		int result = -1;
		for(int i = 1; i < distanceBetweenPoints; i++){
			if(getIndexShift(index,i)<0 || getIndexShift(index,i)>50)
				getIndexShift(index,i);
			if(connectionPoints.get(getIndexShift(index,i)).isOccupied)
				return getIndexShift(index,i);
			if(connectionPoints.get(getIndexShift(index,-i)).isOccupied)
				return getIndexShift(index,-i);
		}
		return result;
	}
	
	//untested
	public int getIndexWithoutNears(int index){
		int nei = getNeigbourIndex(index);
		int dist = distanceBetweenPoints;
		if(nei != -1){
			while(connectionPoints.get(getIndexShift(nei,dist)).isOccupied && dist > 1){
				dist--;
			}
			return getIndexShift(nei,dist);
		}else{
			return index;
		}
	}
	
	public int getFreeIndexWithDistanceTo(int index){
		int distance = distanceBetweenPoints;
		boolean plus = true, minus = true;
		while(distance != 0){
			for(int i = 1; i < distance;i++){
				if(connectionPoints.get(getIndexShift(index,i)).isOccupied){
					plus = false;
					break;
				}
			}
			if(plus)return getIndexShift(index,distance);
			for(int i = 1; i < distance;i++){
				if(connectionPoints.get(getIndexShift(index,-i)).isOccupied){
					minus = false;
					break;
				}
			}
			if(minus)return getIndexShift(index,-distance);
			distance--;
		}
		return index;
	}
	

	
	public boolean occupyConnectionPoint(int index, Transition t){
		if(connectionPoints.size() > index){
			connectionPoints.get(index).occupieCP(t);
		}else{
			System.out.println("!Error: StateConnectionPoints occupyConnectionPoint got wrong index");
			return false;
		}
		return true;
	}

	public void freeConnectionPoint(Transition t){
		for(ConnectionPoint cp : connectionPoints){
			if(cp.getConnectedTransition() != null)
				if(cp.getConnectedTransition().getID() == t.getID()){
					cp.freeCP();
				}
		}
	}



	public Point getPointWithNearDistance(){
		//get first transition
		int startpoint = 0;
		for(int a = 0; a < connectionPoints.size(); a++){
			if(connectionPoints.get(a).isOccupied){
				startpoint = a;
				break;
			}
		}
		
		int etappenStart = startpoint;
		int counter = 0;
		int maxCounter = 0;
		Point maxPoint = new Point(startpoint,-1);
		
		for (int b = 0; b < connectionPoints.size(); b++) {
			
			//frei
			if(!(etappenStart+b+1 < connectionPoints.size()))break;
			if(!connectionPoints.get(etappenStart+b+1).isOccupied()){
				counter++;
				
			//BESETZT
			}else{
				
				//groesser als letzter
				if(maxCounter < counter){
					maxPoint.x = etappenStart;
					maxPoint.y = b+etappenStart;
					maxCounter = counter;
				}
				counter = 0;
			}
		}
		
		//bereich auswaehlen
		int neededDistance = (int)(count / 5);
		return new Point(maxPoint.x+neededDistance/2,maxPoint.x+neededDistance/2 + neededDistance);
		
		
//		
//		int neededDistance = (int)(count / 5);
//		int newNeededDist = neededDistance;
//		int firstFound = -1;
//		int distCouter = 0;
//		for(int i = 0; i<neededDistance;i++){
//			int index = 0;
//			for(ConnectionPoint cp : connectionPoints){
//				if(firstFound == -1){
//					if(!cp.isOccupied){
//						firstFound = index;
//					}
//				}else{
//					//reached required distance
//					if(distCouter == newNeededDist){
//						return new Point(firstFound, index);
//					}
//					//free space
//					else if(!cp.isOccupied){
//						distCouter++;
//					}
//					//found occupied
//					else{
//						distCouter = 0;
//						firstFound = -1;
//					}
//				}
//				index++;
//			}
//			newNeededDist--;
//		}
//		System.out.println("!Error: StateConnectionPoints getPointWithNearDistance found no point");
//		return new Point(-1,-1);
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
			if(!connectionPoints.get(indexToReturn).isOccupied)
				return indexToReturn;
			indexToReturn++;
		}
		return -1;
	}
	
	private int getDistance(int step){
		int minDistance = (int)(count / 5);
		minDistance -= step;
		if(minDistance < 0)return 0;
		return minDistance;
	}
	
	public int getPointWithNearDistanceV2(int index){
		int indexToReturn = -1;
		//decide direction
		
		
		return indexToReturn;
	}
	
	public class ConnectionPoint{
		ConnectionPoint secondBackTransitionPoint;
		PointF point;
		boolean isOccupied = false;
		Transition connectedTransition = null;
		boolean isBackTransition = false;
		
		public ConnectionPoint(PointF p){
			this.point = p;
		}
		public ConnectionPoint(PointF p, ConnectionPoint cp){
			this.point = p;
			this.secondBackTransitionPoint = cp;
		}
		
		public void occupieCP(Transition t){
			connectedTransition = t;
			isOccupied = true;
		}
		public void freeCP(){
			connectedTransition = null;
			isOccupied = false;
		}
		
		//getter setter
		public PointF getPoint() {
			return point;
		}
		public boolean isOccupied() {
			return isOccupied;
		}
		public void setOccupied(boolean isOccupied) {
			this.isOccupied = isOccupied;
		}
		public Transition getConnectedTransition() {
			return connectedTransition;
		}
		public void setConnectedTransition(Transition connectedTransition, boolean isBackTransition) {
			if(connectedTransition == null){
				this.isOccupied = false;
				this.isBackTransition = false;
				this.connectedTransition = null;
			}else{
				this.isOccupied = true;
				this.isBackTransition = isBackTransition;
				this.connectedTransition = connectedTransition;
			}
			if(isBackTransition){
				if(connectedTransition.getPointFrom() == null)
					connectedTransition.setPointFrom(point);
				else connectedTransition.setPointTo(point);
			}
			else{
				if(connectedTransition.getState_to().getID() == attachedState.getID()){
					connectedTransition.setPointTo(point);
				}
				if(connectedTransition.getState_from().getID() == attachedState.getID()){
					connectedTransition.setPointFrom(point);
				}
			}
		}
		public boolean isBackTransition() {
			return isBackTransition;
		}
		public void setBackTransition(boolean isBackTransition) {
			this.isBackTransition = isBackTransition;
		}
	}

}
