package com.uniks.fsmsim.util;

import com.uniks.fsmsim.controller.GraphController;
import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.data.State;
import com.uniks.fsmsim.data.Transition;
import com.uniks.fsmsim.util.Drawing.Gesturelistener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

@SuppressLint("ViewConstructor")
public class DrawingV2 extends View {
	
	//###	Properties	###
	private GraphController graphController;
	private Context context;
	private GestureDetectorCompat detector;

	//##	Drawing scales and Objects	##
	private float state_radius;
	private float strokeWidth;
	private int textSize;
	private Paint paintCircle, paintText, paintSelectedCircle;

	/*test*/int touchedLoc = 0;
	
	
	//###	Init	###
	//##	Constructor	##
	public DrawingV2(Context context, GraphController controller) {
		super(context);
		//#	init props	#
		this.context = context;
		graphController = controller;
		detector = new GestureDetectorCompat(context, new Gesturelistener());
		
		//# calc drawing scales	#
		state_radius = graphController.getDisplay_width() / 25;
		strokeWidth = state_radius / 10;
		textSize = 24;
		
		//# setup Paintings	#
		paintCircle = new Paint();
		paintCircle.setStyle(Paint.Style.STROKE);
		paintCircle.setStrokeWidth(strokeWidth);
		paintCircle.setColor(Color.BLACK);
		paintSelectedCircle = new Paint();
		paintSelectedCircle.setStyle(Paint.Style.STROKE);
		paintSelectedCircle.setStrokeWidth(strokeWidth);
		paintSelectedCircle.setColor(Color.BLUE);
		paintText = new Paint();
		paintText.setTextSize(textSize);
	}
	


	//###	objects to draw	###
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	
		//##	States	##
		for (State state : graphController.getStateList()) {
			//draw State Circle
			canvas.drawCircle(state.getX(), state.getY(), state_radius, paintCircle);
			//draw Endstate circle
			if(state.isEndState())
				canvas.drawCircle(state.getX(), state.getY(), state_radius-7, paintCircle);
			//draw Startingstate arrow
			if(state.isStartState())
				canvas.drawPath(getPathArrowOn(state), paintCircle);
			//draw State Content
			if(state.getType() == fsmType.Moore){
				canvas.drawLine(state.getX() - state_radius, state.getY(), state.getX()
						+ state_radius, state.getY(), paintCircle);
				
				canvas.drawText(state.getName(), state.getX()-15, state.getY()-10, paintText);
				canvas.drawText(state.getStateOutput(), state.getX()-15, state.getY()+30, paintText);
			}else{
				canvas.drawText(state.getName(), state.getX()-15, state.getY()+7, paintText);
			}
			//draw selection
			if(state.isSelected()){
				canvas.drawCircle(state.getX(), state.getY(), state_radius+4, paintSelectedCircle);
			}
		
		}
		//##	Transitions	##
		for (Transition t : graphController.getTransitionList()) {
			canvas.drawPath(getPathTransition(t), paintCircle);
			//transition notation
			float x = Math.abs(t.getState_from().getX()-t.getState_to().getX());
			float y = Math.abs(t.getState_from().getY()-t.getState_to().getY());
			if(graphController.getCurrentType() == fsmType.Moore){
				canvas.drawText(t.getValue(), t.getState_from().getX()+x/2, t.getState_from().getY()+y/2, paintText);
			}else{
				canvas.drawText(t.getValue()+"/"+t.getTransitionOutput(), t.getState_from().getX()+x/2, t.getState_from().getY()+y/2, paintText);
			}
		}
	}
	//##	DrawFuntions	##
	private Path getPathArrowOn(State s){
		Path path = new Path();
		if(s.getX() < graphController.getDisplay_width()/2){
			path.moveTo(s.getX() - state_radius*2, s.getY());
			path.lineTo(s.getX() - state_radius - 3, s.getY());
			path.lineTo(s.getX() - state_radius - 6, s.getY() - 3);
			path.lineTo(s.getX() - state_radius - 6, s.getY() + 3);
			path.lineTo(s.getX() - state_radius - 3, s.getY());
		}else {
			path.moveTo(s.getX() + state_radius*2, s.getY());
			path.lineTo(s.getX() + state_radius + 3, s.getY());
			path.lineTo(s.getX() + state_radius + 6, s.getY() + 3);
			path.lineTo(s.getX() + state_radius + 6, s.getY() - 3);
			path.lineTo(s.getX() + state_radius + 3, s.getY());
		}
		path.close();
		return path;
	}
	private Path getPathTransition(Transition t){
		Path path = new Path();
		PointF p = t.getState_from().getScp().getPointfrom(t);
		path.moveTo(p.x,p.y);
		p = t.getState_to().getScp().getPointfrom(t);
		path.lineTo(p.x,p.y);
		path.close();
		return path;
	}
	
	//###	Gesture Recognize	###
	class Gesturelistener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if(touchedLoc != -1)System.out.println("Gesture:\tdoubleTap on state");
			else System.out.println("Gesture:\tdoubleTap");
			//showState();
			return super.onDoubleTap(e);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			if(touchedLoc != -1){
				graphController.deSelectAll();
				graphController.getStateList().get(touchedLoc).setSelected(true);
				System.out.println("Gesture:\tLong press on state");
			}
			System.out.println("Gesture:\tLong press");
			invalidate();
			super.onLongPress(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {

			if(touchedLoc  != -1){
				//graphController.getStateList().get(touchedLoc).setSelected(true);
				
				//if not selected
				if(!graphController.getStateList().get(touchedLoc).isSelected())
				//search selected one
				for (State	s : graphController.getStateList()) {
					if(s.isSelected()){
						graphController.addTransition(s, 
								graphController.getStateList().get(touchedLoc), "t", "t");
						
						break;
					}
				}
				
				System.out.println("Gesture:\tsingle tap on state");
			}
			graphController.deSelectAll();
			System.out.println("Gesture:\tsingle tap " +touchedLoc);

			invalidate();
			return super.onSingleTapConfirmed(e);
		}
		
	}
}
