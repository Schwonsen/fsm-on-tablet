package com.uniks.fsmsim.util;

import com.uniks.fsmsim.R;
import com.uniks.fsmsim.controller.GraphController;
import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.data.State;
import com.uniks.fsmsim.data.Transition;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

@SuppressLint("ViewConstructor")
public class Drawing extends View {

	public Drawing(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
//	private GraphController graphController;
//	private GestureDetectorCompat detector;
//	private Context context;
//	private int curWidth, curHeight;
//	
//	//drawings
//	private float state_radius = 40f;
//	private float strokeWidth = 4f;
//	private int textSize = 24;
//
//	// drawing and canvas paint
//	private Paint paintCircle, paintText, paintSelectedCircle;
//
//	public Drawing(Context context, GraphController controller) {
//		super(context);
//		this.context = context;
//		graphController = controller;
//		setupDrawing();
//		detector = new GestureDetectorCompat(context, new Gesturelistener());
//	}
//
//	// setup drawing
//	private void setupDrawing() {
//		// prepare for drawing and setup paint stroke properties
//
//		paintCircle = new Paint();
//		paintCircle.setStyle(Paint.Style.STROKE);
//		paintCircle.setStrokeWidth(strokeWidth);
//		paintCircle.setColor(Color.BLACK);
//		
//		paintSelectedCircle = new Paint();
//		paintSelectedCircle.setStyle(Paint.Style.STROKE);
//		paintSelectedCircle.setStrokeWidth(strokeWidth);
//		paintSelectedCircle.setColor(Color.BLUE);
//		
//		paintText = new Paint();
//		paintText.setTextSize(textSize);
//	}
//
//	// size assigned to view
//	@Override
//	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//		System.out.println("scale! w:" + oldw + " to:" + w + " h:" + oldh
//				+ " to:" + h);
//		curWidth = w;
//		curHeight = h;
//	}
//
//	// draw the view - will be called after touch event
//	@SuppressLint("DrawAllocation")
//	@Override
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//		// Draw states
//		for (State state : graphController.getStateList()) {
//			
//			canvas.drawCircle(state.getX(), state.getY(), state_radius, paintCircle);
//			
//			if(state.isEndState())
//				canvas.drawCircle(state.getX(), state.getY(), state_radius-7, paintCircle);
//			
//			//draw arrow
//			if(state.isStartState()){
//				Path path = new Path();
//
//				if(state.getX() < canvas.getWidth()/2){
//					path.moveTo(state.getX() - state_radius*2, state.getY());
//					path.lineTo(state.getX() - state_radius - 3, state.getY());
//					path.lineTo(state.getX() - state_radius - 6, state.getY() - 3);
//					path.lineTo(state.getX() - state_radius - 6, state.getY() + 3);
//					path.lineTo(state.getX() - state_radius - 3, state.getY());
//				}else {
//					path.moveTo(state.getX() + state_radius*2, state.getY());
//					path.lineTo(state.getX() + state_radius + 3, state.getY());
//					path.lineTo(state.getX() + state_radius + 6, state.getY() + 3);
//					path.lineTo(state.getX() + state_radius + 6, state.getY() - 3);
//					path.lineTo(state.getX() + state_radius + 3, state.getY());
//				}
//				path.close();
//				canvas.drawPath(path, paintCircle);
//			}
//			
//			//draw circle and text
//			if(state.getType() == fsmType.Moore){
//				canvas.drawLine(state.getX() - state_radius, state.getY(), state.getX()
//						+ state_radius, state.getY(), paintCircle);
//				
//				canvas.drawText(state.getName(), state.getX()-15, state.getY()-10, paintText);
//				canvas.drawText(state.getStateOutput(), state.getX()-15, state.getY()+30, paintText);
//			}else{
//				canvas.drawText(state.getName(), state.getX()-15, state.getY()+7, paintText);
//			}
//			
//			//draw selection
//			if(state.isSelected()){
//				canvas.drawCircle(state.getX(), state.getY(), state_radius+4, paintSelectedCircle);
//			}	
//		}
//		
//		//draw transitions
//		for (Transition t : graphController.getTransitionList()) {
//			Path path = new Path();
//			float pointLX = 0;
//			float pointRX = 0;
//			float pointY = 0;
//			//from is on the left
//			if(t.getState_from().getX() < t.getState_to().getX()){
//				path.moveTo(t.getState_from().getX()+state_radius, t.getState_from().getY());
//				
//				pointLX = (t.getState_from().getX() + t.getState_to().getX()) / 2;
//				pointY = (t.getState_from().getY() + t.getState_to().getY()) - 50;
//				
//				path.cubicTo(t.getState_from().getX() + state_radius, t.getState_from().getY(), pointLX, pointY, t.getState_to().getX() - state_radius, t.getState_to().getY());
//				
////				path.lineTo(t.getState_to().getX()-state_radius, t.getState_to().getY());
//				path.lineTo(t.getState_to().getX()-state_radius - 6, t.getState_to().getY() - 3);
//				path.lineTo(t.getState_to().getX()-state_radius - 6, t.getState_to().getY() + 3);
//				path.lineTo(t.getState_to().getX()-state_radius - 3, t.getState_to().getY());
//			}else {
//				path.moveTo(t.getState_from().getX()-state_radius, t.getState_from().getY());
//				
//				pointRX = (t.getState_to().getX() + t.getState_from().getX()) / 2;
//				
//				path.cubicTo(t.getState_from().getX() - state_radius, t.getState_from().getY(), pointRX, pointY, t.getState_to().getX() + state_radius, t.getState_to().getY());
//				
//				path.lineTo(t.getState_to().getX()+state_radius, t.getState_to().getY());
//				path.lineTo(t.getState_to().getX()+state_radius + 6, t.getState_to().getY() + 3);
//				path.lineTo(t.getState_to().getX()+state_radius + 6, t.getState_to().getY() - 3);
//				path.lineTo(t.getState_to().getX()+state_radius + 3, t.getState_to().getY());
//			}
//			path.close();
//			canvas.drawPath(path, paintCircle);
//			
//			//transition notation
//			float x = Math.abs(t.getState_from().getX()-t.getState_to().getX());
//			float y = Math.abs(t.getState_from().getY()-t.getState_to().getY());
//			if(graphController.getCurrentType() == fsmType.Moore){
//				canvas.drawText(t.getValue(), t.getState_from().getX()+x/2, t.getState_from().getY()+y/2, paintText);
//			}else{
//				canvas.drawText(t.getValue()+"/"+t.getTransitionOutput(), t.getState_from().getX()+x/2, t.getState_from().getY()+y/2, paintText);
//			}
//		}
//	}
//	private int touchedLoc = -1;
//	float oldX = 0f, oldY = 0f;
//	float x, y;
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		x = event.getX();
//		y = event.getY();
//		
//		int i = 0;
//		for (State s : graphController.getStateList()) {
//			if(x <= (s.getX()+state_radius) && x >= (s.getX()-state_radius)){
//				if(y <= (s.getY()+state_radius) && y >= (s.getY()-state_radius)){
//					touchedLoc = i;
//					oldX = s.getX();
//					oldY = s.getY();
//					break;
//				}
//			}
//			touchedLoc = -1;
//			i++;
//		}
//		
//		this.detector.onTouchEvent(event);
//		
//		switch(event.getAction()){
//			case MotionEvent.ACTION_DOWN:
//				System.out.println("action down");
//				break;
//				
//			case MotionEvent.ACTION_UP:
//				System.out.println("action up");
//				break;
//				
//			case MotionEvent.ACTION_MOVE:
//				if(touchedLoc >= 0){
//					graphController.getStateList().get(touchedLoc).setX(x);
//					graphController.getStateList().get(touchedLoc).setY(y);
//				}
//				invalidate();
//				break;
//				
//			default: return false;
//		}
//
//		return true;
//	}
//	
//	//show popup
//	public void showState(){
//		final Dialog dialog = new Dialog(context);
//		dialog.setContentView(R.layout.edit_state_popup);
//		final int index;
//
//		Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
//		Button btnDelete = (Button) dialog.findViewById(R.id.btn_delete);
//		final CheckBox cB_start = (CheckBox) dialog.findViewById(R.id.checkBoxStart);
//		final CheckBox cB_end = (CheckBox) dialog.findViewById(R.id.checkBoxEnd);
//		final EditText textBox_name = (EditText) dialog.findViewById(R.id.input_statename);
//
//		cB_start.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				cB_start.isChecked();
//				cB_end.setChecked(false);
//			}
//		});
//
//		cB_end.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				cB_end.isChecked();
//				cB_start.setChecked(false);
//			}
//		});
//
//		if(touchedLoc != -1){
//			index = touchedLoc;
//			dialog.setTitle("Zustand bearbeiten");
//			textBox_name.setText(graphController.getStateList().get(touchedLoc).getName());
//			cB_start.setChecked(graphController.getStateList().get(touchedLoc).isStartState());
//			cB_end.setChecked(graphController.getStateList().get(touchedLoc).isEndState());
//			
//			btnDelete.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					//delete state
//					graphController.getStateList().remove(index);
//					invalidate();
//					dialog.dismiss();
//				}
//			});
//			
//		}
//		else{
//			dialog.setTitle("Zustand erstellen");
//			index = touchedLoc;
//		}
//		
//		btnOk.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// create new state
//				System.out.println("on click " +touchedLoc);
//				if (textBox_name.getText().toString().equals("")) {
//					AlertDialog.Builder builder = new AlertDialog.Builder(
//							context);
//					builder.setMessage("Zustand braucht einen Namen!")
//							.setCancelable(false)
//							.setPositiveButton("Ok",
//									new DialogInterface.OnClickListener() {
//										public void onClick(
//												DialogInterface dialog, int id) {
//											dialog.cancel();
//										}
//									});
//					AlertDialog alert = builder.create();
//					alert.show();
//				}
//				if(index != -1){
//					graphController.getStateList().get(index).setName(textBox_name.getText().toString());
//					if(cB_start.isChecked())
//					graphController.setSingleStartState(index);
//					if(cB_end.isChecked())
//					graphController.setSingleEndState(index);
//				}else{
//				//create new state
//				graphController.addState(textBox_name.getText().toString(), "test",
//						cB_start.isChecked(), cB_end.isChecked(), x, y);
//				}
//				invalidate();
//				dialog.dismiss();
//			}
//		});
//		dialog.show();
//
//	}
//
//	class Gesturelistener extends GestureDetector.SimpleOnGestureListener {
//		@Override
//		public boolean onDoubleTap(MotionEvent e) {
//			if(touchedLoc != -1)System.out.println("Gesture:\tdoubleTap on state");
//			else System.out.println("Gesture:\tdoubleTap");
//			showState();
//			return super.onDoubleTap(e);
//		}
//
//		@Override
//		public void onLongPress(MotionEvent e) {
//			if(touchedLoc != -1){
//				graphController.deSelectAll();
//				graphController.getStateList().get(touchedLoc).setSelected(true);
//				System.out.println("Gesture:\tLong press on state");
//			}
//			System.out.println("Gesture:\tLong press");
//			invalidate();
//			super.onLongPress(e);
//		}
//
//		@Override
//		public boolean onSingleTapConfirmed(MotionEvent e) {
//			if(touchedLoc != -1){
//				//graphController.getStateList().get(touchedLoc).setSelected(true);
//				
//				//if not selected
//				if(!graphController.getStateList().get(touchedLoc).isSelected())
//				//search selected one
//				for (State	s : graphController.getStateList()) {
//					if(s.isSelected()){
//						graphController.addTransition(s, 
//								graphController.getStateList().get(touchedLoc), "t", "t");
//						
//						break;
//					}
//				}
//				
//				System.out.println("Gesture:\tsingle tap on state");
//			}
//			graphController.deSelectAll();
//			System.out.println("Gesture:\tsingle tap " +touchedLoc);
//			
//
//			
//			invalidate();
//			return super.onSingleTapConfirmed(e);
//		}
//		
//	}

}
