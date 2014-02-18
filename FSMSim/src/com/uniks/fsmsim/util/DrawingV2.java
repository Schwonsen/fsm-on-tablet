package com.uniks.fsmsim.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.uniks.fsmsim.R;
import com.uniks.fsmsim.controller.GraphController;
import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.data.State;
import com.uniks.fsmsim.data.Transition;

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
	
	//### TouchEvents ###
	float oldX = 0f, oldY = 0f;
	float x, y;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		x = event.getX();
		y = event.getY();
		
		int i = 0;
		for (State s : graphController.getStateList()) {
			if(x <= (s.getX()+state_radius) && x >= (s.getX()-state_radius)){
				if(y <= (s.getY()+state_radius) && y >= (s.getY()-state_radius)){
					touchedLoc = i;
					oldX = s.getX();
					oldY = s.getY();
					break;
				}
			}
			touchedLoc = -1;
			i++;
		}
		
		this.detector.onTouchEvent(event);
		
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				System.out.println("action down");
				break;
				
			case MotionEvent.ACTION_UP:
				System.out.println("action up");
				break;
				
			case MotionEvent.ACTION_MOVE:
				if(touchedLoc >= 0){
					graphController.getStateList().get(touchedLoc).setX(x);
					graphController.getStateList().get(touchedLoc).setY(y);
				}
				invalidate();
				break;
				
			default: return false;
		}

		return true;
	}	
	
	//###	Gesture Recognize	###
	class Gesturelistener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if(touchedLoc != -1)System.out.println("Gesture:\tdoubleTap on state");
			else System.out.println("Gesture:\tdoubleTap");
			showState();
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

			if(touchedLoc != -1){
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
		
		//show popup
		public void showState(){
			final Dialog dialog = new Dialog(context);
			dialog.setContentView(R.layout.edit_state_popup);
			final int index;

			Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
			Button btnDelete = (Button) dialog.findViewById(R.id.btn_delete);
			final CheckBox cB_start = (CheckBox) dialog.findViewById(R.id.checkBoxStart);
			final CheckBox cB_end = (CheckBox) dialog.findViewById(R.id.checkBoxEnd);
			final EditText textBox_name = (EditText) dialog.findViewById(R.id.input_statename);

			cB_start.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					cB_start.isChecked();
					cB_end.setChecked(false);
				}
			});

			cB_end.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					cB_end.isChecked();
					cB_start.setChecked(false);
				}
			});

			if(touchedLoc != -1){
				index = touchedLoc;
				dialog.setTitle("Zustand bearbeiten");
				textBox_name.setText(graphController.getStateList().get(touchedLoc).getName());
				cB_start.setChecked(graphController.getStateList().get(touchedLoc).isStartState());
				cB_end.setChecked(graphController.getStateList().get(touchedLoc).isEndState());
				
				btnDelete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//delete state
						graphController.getStateList().remove(index);
						invalidate();
						dialog.dismiss();
					}
				});
				
			}
			else{
				dialog.setTitle("Zustand erstellen");
				index = touchedLoc;
			}
			
			btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// create new state
					System.out.println("on click " +touchedLoc);
					if (textBox_name.getText().toString().equals("")) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						builder.setMessage("Zustand braucht einen Namen!")
								.setCancelable(false)
								.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog, int id) {
												dialog.cancel();
											}
										});
						AlertDialog alert = builder.create();
						alert.show();
					}
					if(index != -1){
						graphController.getStateList().get(index).setName(textBox_name.getText().toString());
						if(cB_start.isChecked())
						graphController.setSingleStartState(index);
						if(cB_end.isChecked())
						graphController.setSingleEndState(index);
					}else{
					//create new state
					graphController.addState(textBox_name.getText().toString(), "test",
							cB_start.isChecked(), cB_end.isChecked(), x, y);
					}
					invalidate();
					dialog.dismiss();
				}
			});
			dialog.show();

		}
		
	}
}
