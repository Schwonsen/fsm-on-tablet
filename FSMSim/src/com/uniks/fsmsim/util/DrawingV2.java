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

	// ### Properties ###
	private GraphController graphController;
	private Context context;
	private GestureDetectorCompat detector;

	// ## Drawing scales and Objects ##
	private float state_radius;
	private float strokeWidth;
	private int textSize;
	private Paint paintCircle, paintText, paintSelectedCircle, paintArrow;
		
	//###	Init	###
	//##	Constructor	##
	public DrawingV2(Context context, GraphController controller) {
		super(context);
		// # init props #
		this.context = context;
		graphController = controller;
		detector = new GestureDetectorCompat(context, new Gesturelistener());

		// # calc drawing scales #
		state_radius = graphController.getDisplay_width() / 25;
		controller.setStateRadius(state_radius);
		strokeWidth = state_radius / 10;
		textSize = 24;

		// # setup Paintings #
		paintCircle = new Paint();
		paintCircle.setStyle(Paint.Style.STROKE);
		paintCircle.setStrokeWidth(strokeWidth);
		paintCircle.setColor(Color.BLACK);
		paintCircle.setAntiAlias(true);
		paintArrow = new Paint();
		paintArrow.setStyle(Paint.Style.FILL);
		paintArrow.setStrokeWidth(strokeWidth);
		paintArrow.setColor(Color.BLACK);
		paintArrow.setAntiAlias(true);
		paintSelectedCircle = new Paint();
		paintSelectedCircle.setStyle(Paint.Style.STROKE);
		paintSelectedCircle.setStrokeWidth(strokeWidth);
		paintSelectedCircle.setColor(Color.BLUE);
		paintSelectedCircle.setAntiAlias(true);
		paintText = new Paint();
		paintText.setTextSize(textSize);
		
		//Test
		controller.addState("s1", "01", true, false, 200.0f, 200.0f);
		controller.addState("s2", "10", false, false, 400.0f, 200.0f);
		controller.addState("s2", "10", false, true, 600.0f, 200.0f);
		controller.addTransition(controller.getStateList().get(0), controller.getStateList().get(1), "0", "1");
		controller.addTransition(controller.getStateList().get(1), controller.getStateList().get(2), "0", "1");
	}

	// ### objects to draw ###

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// ## States ##
		for (State state : graphController.getStateList()) {
			// draw State Circle
			canvas.drawCircle(state.getX(), state.getY(), state_radius,
					paintCircle);
			// draw Endstate circle
			if (state.isEndState())
				canvas.drawCircle(state.getX(), state.getY(), state_radius - 7,
						paintCircle);
			// draw Startingstate arrow
			if (state.isStartState())
				canvas.drawPath(getPathArrowOn(state), paintCircle);
			// draw State Content
			if (state.getType() == fsmType.Moore) {
				canvas.drawLine(state.getX() - state_radius, state.getY(),
						state.getX() + state_radius, state.getY(), paintCircle);

				canvas.drawText(state.getName(), state.getX() - 15,
						state.getY() - 10, paintText);
				canvas.drawText(state.getStateOutput(), state.getX() - 15,
						state.getY() + 30, paintText);
			} else {
				canvas.drawText(state.getName(), state.getX() - 15,
						state.getY() + 7, paintText);
			}
			// draw selection
			if (state.isSelected()) {
				canvas.drawCircle(state.getX(), state.getY(), state_radius + 4,
						paintSelectedCircle);
			}

		}
		// ## Transitions ##
		for (Transition t : graphController.getTransitionList()) {
			canvas.drawPath(getPathTransition(t), paintCircle);
			//transition notation
			PointF p = getTransitionNotationPosition(t);
			if(graphController.getCurrentType() == fsmType.Moore){
				canvas.drawText(t.getValue(),p.x,p.y, paintText);
			}else{
				canvas.drawText(t.getValue()+"/"+t.getTransitionOutput(), p.x, p.y, paintText);
			}
			canvas.drawPath(getPathArrowHead(t), paintArrow);
			if(t.isSelected())
				canvas.drawCircle(t.getDragPoint().x, t.getDragPoint().y, state_radius/3, paintSelectedCircle);
		}
	}

	// ## DrawFuntions ##
	private Path getPathArrowOn(State s) {
		Path path = new Path();
		if (s.getX() < graphController.getDisplay_width() / 2) {
			path.moveTo(s.getX() - state_radius * 2, s.getY());
			path.lineTo(s.getX() - state_radius - 3, s.getY());
			path.lineTo(s.getX() - state_radius - 6, s.getY() - 3);
			path.lineTo(s.getX() - state_radius - 6, s.getY() + 3);
			path.lineTo(s.getX() - state_radius - 3, s.getY());
		} else {
			path.moveTo(s.getX() + state_radius * 2, s.getY());
			path.lineTo(s.getX() + state_radius + 3, s.getY());
			path.lineTo(s.getX() + state_radius + 6, s.getY() + 3);
			path.lineTo(s.getX() + state_radius + 6, s.getY() - 3);
			path.lineTo(s.getX() + state_radius + 3, s.getY());
		}
		path.close();
		return path;
	}

	private Path getPathTransition(Transition t) {
		Path path = new Path();
		path.moveTo(t.getPointFrom().x,t.getPointFrom().y);
		path.quadTo(t.getDragPoint().x, t.getDragPoint().y, t.getPointTo().x, t.getPointTo().y);
		return path;
	}
	private PointF getTransitionNotationPosition(Transition t){
		PointF p = new PointF();
		PointF pFrom = t.getPointFrom(), pTo = t.getPointTo();
		
		if(pFrom.x < pTo.x){
			if(pFrom.y < pTo.y){
				//<<
				p.x = pFrom.x + (pTo.x-pFrom.x)/2;
				p.y = pFrom.y + (pTo.y-pFrom.y)/2 - textSize/2;
			}else{
				//<>
				p.x = pFrom.x + (pTo.x-pFrom.x)/2;
				p.y = pTo.y + (pFrom.y-pTo.y)/2 - textSize;
			}
		}else{
			if(pFrom.y < pTo.y){
				//>>
				p.x = pTo.x + (pFrom.x-pTo.x)/2;
				p.y = pTo.y + (pFrom.y-pTo.y)/2 - textSize/2;
			}else{
				//><
				p.x = pTo.x + (pFrom.x-pTo.x)/2;
				p.y = pFrom.y + (pTo.y-pFrom.y)/2 - textSize;
			}
		}
			
		return p;
	}
	
	private Path getPathArrowHead(Transition t){
		Path mPath = new Path();
		float fromx = t.getDragPoint().x, fromy = t.getDragPoint().y, tox = t.getPointTo().x, toy = t.getPointTo().y;
		float headlen = state_radius/2;   // length of head in pixels
		float angle = (float) Math.atan2((double)(toy-fromy),(double)(tox-fromx));
		mPath.moveTo(tox, toy);
		mPath.lineTo((float)(tox-headlen*Math.cos(angle-Math.PI/6)),(float)(toy-headlen*Math.sin(angle-Math.PI/6)));
	    mPath.lineTo((float)(tox-headlen*Math.cos(angle+Math.PI/6)),(float)(toy-headlen*Math.sin(angle+Math.PI/6)));
        mPath.close();
        return mPath;
	}
	
	
	//### TouchEvents ###
	int touchedStateIndex = -1, selectedStateIndex = -1, touchedTransitionIndex = -1;
	float touchedPoint_x, touchedPoint_y;


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		touchedPoint_x = event.getX();
		touchedPoint_y = event.getY();
		
		
		int i = 0;
		for (State s : graphController.getStateList()) {
			if (touchedPoint_x <= (s.getX() + state_radius)
					&& touchedPoint_x >= (s.getX() - state_radius)) {
				if (touchedPoint_y <= (s.getY() + state_radius)
						&& touchedPoint_y >= (s.getY() - state_radius)) {
					touchedStateIndex = i;
					break;
				}
			}
			touchedStateIndex = -1;
			i++;
		}
		i = 0;
		for(Transition t : graphController.getTransitionList()){
			if (touchedPoint_x <= (t.getDragPoint().x + state_radius)
					&& touchedPoint_x >= (t.getDragPoint().x - state_radius)) {
				if (touchedPoint_y <= (t.getDragPoint().y + state_radius)
						&& touchedPoint_y >= (t.getDragPoint().y - state_radius)) {
					touchedTransitionIndex = i;
					break;
				}
			}
			touchedTransitionIndex = -1;
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
				if(touchedStateIndex >= 0){
					graphController.getStateList().get(touchedStateIndex).moveState(new PointF(touchedPoint_x,touchedPoint_y));
				}
				if(touchedTransitionIndex >= 1){
					if(graphController.getTransitionList().get(touchedTransitionIndex).isSelected())
						graphController.getTransitionList().get(touchedTransitionIndex).
							moveDragPoint(new PointF(touchedPoint_x,touchedPoint_y));
					System.out.println("move transition");
				}
				invalidate();
				break;
				
			default: return false;
		}

		return true;
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

		if(touchedStateIndex != -1){
			index = touchedStateIndex;
			dialog.setTitle("Zustand bearbeiten");
			textBox_name.setText(graphController.getStateList().get(touchedStateIndex).getName());
			cB_start.setChecked(graphController.getStateList().get(touchedStateIndex).isStartState());
			cB_end.setChecked(graphController.getStateList().get(touchedStateIndex).isEndState());
			
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
			textBox_name.setText(graphController.getNextName());
			btnDelete.setVisibility(INVISIBLE);
			index = touchedStateIndex;
		}
		
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// create new state
				if (!textBox_name.getText().toString().equals("") && !(touchedStateIndex <= 0 &&graphController.getStatenames().
						contains(textBox_name.getText().toString()))) {
					if (index != -1) {
						graphController.getStateList().get(index)
								.setName(textBox_name.getText().toString());
						if (cB_start.isChecked())
							graphController.setSingleStartState(index);
						if (cB_end.isChecked())
							graphController.setSingleEndState(index);
					} else {
						// create new state
						graphController.addState(textBox_name.getText()
								.toString(), "test", cB_start.isChecked(),
								cB_end.isChecked(), touchedPoint_x, touchedPoint_y);
					}
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setMessage("Zustand braucht einen neuen Namen!")
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
				invalidate();
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	//in- output for transitions
	public void showIOTransitions(){
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.create_transition_popup);
		final int index;

		Button btnCreate = (Button) dialog.findViewById(R.id.btn_create);
		Button btnDelete = (Button) dialog.findViewById(R.id.btn_delete);
		final EditText textBox_input = (EditText) dialog.findViewById(R.id.eT_eingang);
		final EditText textBox_output = (EditText) dialog.findViewById(R.id.eT_ausgang);

		//TODO irgendeine andere if abfrage nutzen
		if(touchedStateIndex != -1){
			index = touchedStateIndex;
			dialog.setTitle("Transition bearbeiten");
			btnDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//delete state
					graphController.getTransitionList().remove(index);
					invalidate();
					dialog.dismiss();
				}
			});
			
		}
		else{
			dialog.setTitle("Transition erstellen");
			btnDelete.setVisibility(INVISIBLE);
			index = touchedStateIndex;
		}
		
		btnCreate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("on click " + touchedStateIndex);
				if (!textBox_input.getText().toString().equals("") && !textBox_output.getText().toString().equals("")) {
				// create new Transition
					System.out.println("selectedStateIndex:"+selectedStateIndex);
					System.out.println("touchedLoc:"+touchedStateIndex);
						graphController.addTransition(graphController.getStateList().get(selectedStateIndex), graphController
								.getStateList().get(touchedStateIndex), textBox_input.getText().toString(), textBox_output.getText().toString());
					
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setMessage("Transition braucht einen Wert bei Eingang und Ausgabe!")
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
				invalidate();
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	//###	Gesture Recognize	###
	class Gesturelistener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (touchedStateIndex != -1)
				System.out.println("Gesture:\tdoubleTap on state");
			else
				System.out.println("Gesture:\tdoubleTap");
			showState();
			return super.onDoubleTap(e);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			if (touchedStateIndex != -1) {
				graphController.deSelectAll();
				graphController.getStateList().get(touchedStateIndex)
						.setSelected(true);
				selectedStateIndex = touchedStateIndex;
				System.out.println("Gesture:\tLong press on state");
			}
			System.out.println("Gesture:\tLong press");
			invalidate();
			super.onLongPress(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {

			if (touchedStateIndex != -1) {
				// if not selected
				if (!graphController.getStateList().get(touchedStateIndex)
						.isSelected()){
					boolean isOneSelected = false;
					// search selected one
					for (State s : graphController.getStateList()) {
						if (s.isSelected()) {
							isOneSelected = true;
							showIOTransitions();
							break;
						}
					}
					
					if(!isOneSelected){
						for(Transition t : graphController.getStateList().get(touchedStateIndex).
								getScp().getTransitions()){
							if(t != null)
								t.setSelected(true);
						}
					}
				}

				System.out.println("Gesture:\tsingle tap on state");
			}
			else graphController.deSelectAll();
			
			System.out.println("Gesture:\tsingle tap " + touchedStateIndex);

			invalidate();
			return super.onSingleTapConfirmed(e);
		}
	}
}