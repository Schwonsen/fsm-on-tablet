package com.uniks.fsmsim.util;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.uniks.fsmsim.R;
import com.uniks.fsmsim.controller.GraphController;
import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.data.State;
import com.uniks.fsmsim.data.TransitionValue;
import com.uniks.fsmsim.data.TransitionListAdapter;
import com.uniks.fsmsim.data.Transition;

@SuppressLint("ViewConstructor")
public class DrawingV2 extends View {

	//###	Properties	###
	private GraphController graphController;
	private Context context;
	private GestureDetectorCompat detector;
	
	//###	TouchEvents	###
	int touchedStateIndex = -1, selectedStateIndex = -1,
			touchedDragPointIndex = -1, touchedNotationIndex = -1;
	float touchedPoint_x = 0, touchedPoint_y = 0;
	

	//##	Drawing scales and Objects	##
	private float state_radius;
	private float strokeWidth;
	private int textSizeState;
	private int textSizeTransition;
	private Paint paintCircle, paintText, paintSelectedCircle, paintArrow,
			paintCross, paintPath;
	
	private int arrowColor = Color.rgb(60, 60, 60);

	private String input, output;

	private ArrayList<String> transi_id = new ArrayList<String>();
	private ArrayList<String> transi_input = new ArrayList<String>();
	private ArrayList<String> transi_output = new ArrayList<String>();
	private AlertDialog.Builder build;
	
	boolean isMoved = false;
	int moveIndex = -1;
	
	//##	Background lines	##
	boolean drawLines = true;
	int lineDistance = 200;
	Paint paintBgLine;
	public boolean isDrawLines() {
		return drawLines;
	}
	public void setDrawLines(boolean drawLines) {
		this.drawLines = drawLines;
	}

	// ### Init ###
	
	// ## Constructor ##
	public DrawingV2(Context context, GraphController controller) {
		super(context);
		// # init props #
		this.context = context;
		graphController = controller;
		detector = new GestureDetectorCompat(context, new Gesturelistener());

		// # calc drawing scales #
		state_radius = graphController.getDisplay_width() / 24;
		controller.setStateRadius(state_radius);
		strokeWidth = state_radius / 14;
		textSizeState = 30;
		textSizeTransition = 20;

		// # setup Paintings #
		paintCircle = new Paint();
		paintCircle.setStyle(Paint.Style.STROKE);
		paintCircle.setStrokeWidth(strokeWidth);
		paintCircle.setColor(Color.BLACK);
		paintCircle.setAntiAlias(true);
		paintPath = new Paint();
		paintPath.setStyle(Paint.Style.STROKE);
		paintPath.setStrokeWidth(strokeWidth);
		paintPath.setColor(Color.BLACK);
		paintPath.setAntiAlias(true);
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
		paintText.setTextSize(textSizeState);
		paintText.setTextAlign(Align.CENTER);
		paintCross = new Paint();
		paintCross.setStyle(Paint.Style.STROKE);
		paintCross.setStrokeWidth(strokeWidth);
		paintCross.setColor(Color.RED);
		paintCross.setAntiAlias(true);
		
		//Background lines
		paintBgLine = new Paint();
		paintBgLine.setStyle(Paint.Style.STROKE);
		paintBgLine.setColor(Color.rgb(117, 186, 255));
		paintBgLine.setAntiAlias(true);

		// TODO remove test values
		controller.addState("s0", "01", true, false, 200.0f, 200.0f);
		controller.addState("s1", "10", false, false, 400.0f, 200.0f);
		controller.addState("s2", "10", false, true, 600.0f, 200.0f);
		controller.addTransition(controller.getStateList().get(0), controller.getStateList().get(1), "0", "1");
		controller.addTransition(controller.getStateList().get(1), controller.getStateList().get(2), "1", "1");

	}

	// ### objects to draw ###

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//draw background lines
		if(drawLines){
			
		float lineStrokeWidth = Utils.SetTextSize("text", (int) (graphController.getDisplay_width() * 0.003), (int) (graphController.getDisplay_height() * 0.003));
		//small
			paintBgLine.setStrokeWidth(lineStrokeWidth/2);
			//Vertical
			for(int i = lineDistance/10; i < graphController.getDisplay_width(); i+=lineDistance/10){
				canvas.drawLine(i, 0, i, graphController.getDisplay_height(), paintBgLine);
			}
			//horizontal
			for(int i = lineDistance/10; i < graphController.getDisplay_height(); i+=lineDistance/10){
				canvas.drawLine(0, i, graphController.getDisplay_width(), i, paintBgLine);
			}
		//big
			paintBgLine.setStrokeWidth(lineStrokeWidth);
			//Vertical
			for(int i = lineDistance; i < graphController.getDisplay_width(); i+=lineDistance){
				canvas.drawLine(i, 0, i, graphController.getDisplay_height(), paintBgLine);
			}
			//horizontal
			for(int i = lineDistance; i < graphController.getDisplay_height(); i+=lineDistance){
				canvas.drawLine(0, i, graphController.getDisplay_width(), i, paintBgLine);
			}

		}
		
		// ## Transitions ##
		paintText.setTextSize(textSizeTransition);
		for (Transition t : graphController.getTransitionList()) {
			//#	set simulation color	#
			if(t.isInSimulation()){
				paintArrow.setColor(Color.RED);
				paintPath.setColor(Color.RED);
			}else if(t.isPossibleSimulation()){
				paintArrow.setColor(Color.GREEN);
				paintPath.setColor(Color.GREEN);
			}
			else{
				paintArrow.setColor(arrowColor);
				paintPath.setColor(arrowColor);
			}
			//#	draw line and arrow	#
			if (t.isBackConnection()) {
				canvas.drawPath(getPathTransitionBackCon(t), paintPath);
				canvas.drawPath(getPathArrowHead(t), paintArrow);
			} else {
				canvas.drawPath(getPathTransition(t), paintPath);
				canvas.drawPath(getPathArrowHead(t), paintArrow);
			}
			//#	transition notation	#
			t.setNotationPoint(getTransitionNotationPosition(t));
			PointF p_note = t.getNotationPoint();
			if (graphController.getCurrentType() == fsmType.Moore) {
				canvas.drawText(t.getMooreNotification(), p_note.x, p_note.y, paintText);
			} else {
				canvas.drawText(t.getMealyNotification(), p_note.x, p_note.y, paintText);
			}
			//#	Draw moveModus	#
			if(isMoved && graphController.getTransitionList().get(moveIndex).getID() == t.getID()){
				canvas.drawPath(getPathPointet(p_note, new PointF(touchedPoint_x,touchedPoint_y)), paintSelectedCircle);
				paintSelectedCircle.setStyle(Paint.Style.FILL);
				canvas.drawCircle(touchedPoint_x, touchedPoint_y, state_radius/2, paintSelectedCircle);
				paintSelectedCircle.setStyle(Paint.Style.STROKE);
			}	
			//#	draw deletion cross	#
			if (t.isSelected()) {
			canvas.drawCircle(p_note.x, p_note.y, state_radius / 3, paintSelectedCircle);
			canvas.drawPath(drawRedCross(p_note), paintCross);
			}
		}

		// ## States ##
		for (State state : graphController.getStateList()) {
			//#	set simulation color	#
			if(state.isInSimulation()){
				paintCircle.setColor(Color.RED);
			}else{
				paintCircle.setColor(Color.BLACK);
			}
			// # draw selection #
			if (state.isSelected()) {
				canvas.drawCircle(state.getX(), state.getY(), state_radius + 4, paintSelectedCircle);
			}
			// # draw State Circle #
			canvas.drawCircle(state.getX(), state.getY(), state_radius, paintCircle);
			
			// # draw Endstate circle #
			if (state.isEndState()){
				canvas.drawCircle(state.getX(), state.getY(), state_radius - 7, paintCircle);
			}
			// # draw Startingstate arrow #
			if (state.isStartState()){
				canvas.drawPath(getPathArrowOn(state), paintCircle);
			}
			// # draw State Content #
			float outputX = state.getX();
			float outputY = state.getY() + state_radius/2;
			outputY -= (paintText.descent() / 2);
			float inputY = state.getY() - state_radius/2;
			inputY -= ((paintText.descent() + paintText.ascent()) / 2);
			
			if (state.getType() == fsmType.Moore) {
				paintText.setTextSize(textSizeTransition);
				canvas.drawLine(state.getX() - state_radius, state.getY(),state.getX() + state_radius, 
						state.getY(), paintCircle);

				canvas.drawText(state.getName(), outputX, inputY, paintText);
				canvas.drawText(state.getStateOutput(),outputX, outputY, paintText);
			} else {
				paintText.setTextSize(textSizeState);
				canvas.drawText(state.getName(), state.getX(), state.getY()-((paintText.descent() + 
						paintText.ascent()) / 2), paintText);
			}

		}
	}

	// ## DrawFuntions ##
	
	// # draw an red cross
	private Path drawRedCross(PointF point) {
		Path path = new Path();
		float radius = state_radius / 3;
		path.moveTo(point.x + radius, point.y - radius);
		path.lineTo(point.x - radius, point.y + radius);
		path.moveTo(point.x - radius, point.y - radius);
		path.lineTo(point.x + radius, point.y + radius);
		return path;
	}

	// # get Arrow for Startstate as Path #
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

	// # get line from and to a state as Path #
	private Path getPathTransition(Transition t) {
		Path path = new Path();
		path.moveTo(t.getPointFrom().x, t.getPointFrom().y);
		path.quadTo(t.getDragPoint().x, t.getDragPoint().y, t.getPointTo().x, t.getPointTo().y);
		return path;
	}
	
	private Path getPathPointet(PointF pf, PointF pt) {
		Path path = new Path();
		path.moveTo(pf.x, pf.y);
		path.lineTo(pt.x, pt.y);
//		PointF vec = new PointF(pt.x - pf.x, pt.y - pf.y);
//		float value = (float) Math.sqrt(vec.x*vec.x + vec.y * vec.y);
//		vec.x /= value;
//		vec.y /= value;
//		
//		for(int i = 0;(pf.x + vec.x * i) < pt.x && (pf.y + vec.y * i) < pt.y; i+=2){
//			path.moveTo(pf.x + vec.x * i, pf.y + vec.y * i);
//			path.lineTo(pf.x + vec.x * i+1, pf.y + vec.y * i+1);
//			path.close();
//		}
		return path;
	}

	// # get line from and to same state as Path #
	private Path getPathTransitionBackCon(Transition t) {
		Path path = new Path();
		PointF p1, p2;
		p1 = new PointF(t.getPointFrom().x - t.getState_from().getX(), t.getPointFrom().y - t.getState_from().getY());
		p2 = new PointF(t.getPointTo().x - t.getState_from().getX(), t.getPointTo().y - t.getState_from().getY());

		p1.x *= 2;
		p1.y *= 2;
		p2.x *= 2;
		p2.y *= 2;

		p1.x = t.getPointFrom().x + p1.x;
		p1.y = t.getPointFrom().y + p1.y;
		p2.x = t.getPointFrom().x + p2.x;
		p2.y = t.getPointFrom().y + p2.y;

		path.moveTo(t.getPointFrom().x, t.getPointFrom().y);
		path.cubicTo(p1.x, p1.y, p2.x, p2.y, t.getPointTo().x, t.getPointTo().y);
		return path;
	}

	// # calculate point to write the transition notification #
	private PointF getTransitionNotationPosition(Transition t) {
		PointF pFrom = t.getPointFrom(), pTo = t.getPointTo();
		PointF between = new PointF((pTo.x - pFrom.x) / 2 + pFrom.x, (pTo.y - pFrom.y) / 2 + pFrom.y);

		if (t.isBackConnection()) {
			PointF vektor = new PointF(between.x - t.getState_from().getPoint().x, between.x
					- t.getState_from().getPoint().x);
			vektor.x *= 3;
			vektor.y *= 3;
			vektor.x += t.getState_from().getPoint().x;
			vektor.y += t.getState_from().getPoint().y;
			return vektor;
		} else {
			PointF betweenDragMid = new PointF((t.getDragPoint().x - between.x)
					/ 2 + between.x, (t.getDragPoint().y - between.y) / 2 + between.y);
			betweenDragMid.y -= state_radius / 3;
			if(Math.abs(t.getState_from().getX()-t.getState_to().getX()) < 230){
				betweenDragMid.x+= 20;
			}
			return betweenDragMid;
		}
		
		
	}

	// # get Arrowhead as Path #
	private Path getPathArrowHead(Transition t) {
		Path mPath = new Path();
		float fromx, fromy, tox, toy;
		if (t.isBackConnection()) {
			fromx = t.getPointTo().x - t.getState_from().getX();
			fromy = t.getPointTo().y - t.getState_from().getY();
			fromx *= 2;
			fromy *= 2;
			fromx = t.getPointFrom().x + fromx;
			fromy = t.getPointFrom().y + fromy;
		} else {
			fromx = t.getDragPoint().x;
			fromy = t.getDragPoint().y;
		}
		tox = t.getPointTo().x;
		toy = t.getPointTo().y;
		float headlen = state_radius / 2; // length of head in pixels
		float angle = (float) Math.atan2((double) (toy - fromy), (double) (tox - fromx));
		mPath.moveTo(tox, toy);
		mPath.lineTo((float) (tox - headlen * Math.cos(angle - Math.PI / 6)),
				(float) (toy - headlen * Math.sin(angle - Math.PI / 6)));
		mPath.lineTo((float) (tox - headlen * Math.cos(angle + Math.PI / 6)),
				(float) (toy - headlen * Math.sin(angle + Math.PI / 6)));
		mPath.close();
		return mPath;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		touchedPoint_x = event.getX();
		touchedPoint_y = event.getY();

		// ## check if touching a state ##
		if (touchedDragPointIndex == -1) {

			int i = 0;
			if (graphController.getStateList().size() < 1)
				touchedStateIndex = -1;

			touchedStateIndex = -1;
			for (State s : graphController.getStateList()) {
				if (touchedPoint_x <= (s.getX() + state_radius) && touchedPoint_x >= (s.getX() - state_radius)) {
					if (touchedPoint_y <= (s.getY() + state_radius) && touchedPoint_y >= (s.getY() - state_radius)) {
						touchedStateIndex = i;
						break;
					}
				}
				i++;
			}
		} else
			touchedDragPointIndex = -1;

		// ## check if touching a Transition ##
		if (touchedStateIndex == -1) {
			int i = 0;
			touchedDragPointIndex = -1;
			touchedNotationIndex = -1;
			for (Transition t : graphController.getTransitionList()) {
				if (!t.isBackConnection() && t.getDragPoint() != null 
						&& t.getNotationPoint() != null) {
					// # check if touching a dragpoint #
//					if (touchedPoint_x <= (t.getDragPoint().x + state_radius)
//							&& touchedPoint_x >= (t.getDragPoint().x - state_radius)) {
//						if (touchedPoint_y <= (t.getDragPoint().y + state_radius)
//								&& touchedPoint_y >= (t.getDragPoint().y - state_radius)) {
//							touchedDragPointIndex = i;
//							break;
//						}
//					}
					// # check if touching a notification #
					if (touchedPoint_x <= (t.getNotationPoint().x + state_radius)
							&& touchedPoint_x >= (t.getNotationPoint().x - state_radius)) {
						if (touchedPoint_y <= (t.getNotationPoint().y + state_radius)
								&& touchedPoint_y >= (t.getNotationPoint().y - state_radius)) {
							touchedNotationIndex = i;
							System.out.println("touchedNotationIndex: " + i);
							break;
						}
					}
				}
				i++;
			}
		} else {
			touchedDragPointIndex = -1;
			touchedNotationIndex = -1;
		}

		this.detector.onTouchEvent(event);

		// ## check for kind of movement ##
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			System.out.println("action down");
			if(touchedNotationIndex >= 0){
				isMoved = true;
				moveIndex = touchedNotationIndex;
			}
			break;
			
		case MotionEvent.ACTION_UP:
			isMoved = false;
			moveIndex = -1;
			invalidate();
			break;

//		case MotionEvent.ACTION_UP:
////			// if(!isMoved){
////			// System.out.println("isMoved");
////			// }else{
////			// # Single tap #
////
//			// ## single tap on state
//			if (touchedStateIndex != -1
//					&& graphController.getStateList().size() > 0) {
//				// # if one is selected create transition, else select it #
//				if (graphController.haveSelectedState()) {
//					System.out.println("fromOutput"+graphController.getSelectedState().getCurrOutputCount() +" MaxOutput:"+ graphController.getOuputCount() +" toInput:"+
//							graphController.getStateList().get(touchedStateIndex).getCurrInputCount() +" MaxInput:"+ graphController.getInputCount());
//					showIOTransitions();
//				} else {
//					graphController.deSelectAll();
//					graphController.getStateList().get(touchedStateIndex).setSelected(true);
//					selectedStateIndex = touchedStateIndex;
//				}
//			}
//
//			// ## single tap on transition notification ##
//			if (touchedNotationIndex != -1
//					&& graphController.getTransitionList().size() > 0) {
//				graphController.getTransitionList().get(touchedNotationIndex).setSelected(true);
//			}
//
//			// ## single tap on drag point ##
//			if (touchedDragPointIndex != -1
//					&& graphController.getTransitionList().size() > 0) {
//				graphController.getTransitionList().get(touchedDragPointIndex).setSelected(true);
//				if (graphController.getTransitionList().get(touchedDragPointIndex) != null) {
//					if (graphController.getTransitionList().get(touchedDragPointIndex).isMarkedAsDeletion()) {
//						graphController.removeTransition(graphController.getTransitionList().get(touchedDragPointIndex));
//						invalidate();
//					} else
//						graphController.getTransitionList().get(touchedDragPointIndex).setMarkedAsDeletion(true);
//				}
//			}
//			
//			// ## single into the white ##
//			if (touchedNotationIndex == -1 && touchedDragPointIndex == -1 && touchedStateIndex == -1) {
//				graphController.deSelectAll();
//				graphController.unmarkDeletion();
//			}
//
//			invalidate();
//			// }
//			System.out.println("action up");
//			break;

		case MotionEvent.ACTION_MOVE:
			// # set restricted moveArea #
			if (touchedPoint_x < state_radius)
				touchedPoint_x = state_radius;

			if (touchedPoint_x > graphController.getDisplay_width())
				touchedPoint_x = graphController.getDisplay_width() - state_radius;

			if (touchedPoint_y > graphController.getDisplay_height() - graphController.getDisplay_BotBarSize() - state_radius)
				touchedPoint_y = graphController.getDisplay_height() - graphController.getDisplay_BotBarSize() - state_radius;

			if (touchedPoint_y < graphController.getDisplay_TopBarSize())
				touchedPoint_y = graphController.getDisplay_TopBarSize();

			// # Move state #
			if (touchedStateIndex >= 0) {
				graphController.getStateList().get(touchedStateIndex)
						.moveState(new PointF(touchedPoint_x, touchedPoint_y));
			}
			// # Move Transition #
			if (touchedDragPointIndex >= 0) {
				// # move selected transition drag point #
				if (graphController.getTransitionList()
						.get(touchedDragPointIndex).isSelected()) {
					graphController
							.getTransitionList()
							.get(touchedDragPointIndex)
							.moveDragPoint(
									new PointF(touchedPoint_x, touchedPoint_y));
				}
			}
			if (isMoved) {
				// # move selected transition drag point #
					graphController.getTransitionList().get(moveIndex)
							.moveDragPointOnNotification(new PointF(touchedPoint_x, touchedPoint_y));
			}
			// redraw
			invalidate();
			break;
			
		default:
			return false;
		}
		
		return true;
	}

	// ### show popup Edit State ###
	public void showState() {
		final Dialog dialog = new Dialog(context);
		float textsize = Utils.SetTextSize("text", (int) (graphController.getDisplay_width() * 0.035), (int) (graphController.getDisplay_height() * 0.035));
		
		if (graphController.getCurrentType() == fsmType.Mealy) {
			dialog.setContentView(R.layout.edit_state_popup);
		} else if(graphController.getCurrentType() == fsmType.Moore) {
			dialog.setContentView(R.layout.edit_state_popup_moore);
		}
		
		final int index;

		Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
		Button btnDelete = (Button) dialog.findViewById(R.id.btn_delete);
		final CheckBox cB_start = (CheckBox) dialog.findViewById(R.id.checkBoxStart);
		final CheckBox cB_end = (CheckBox) dialog.findViewById(R.id.checkBoxEnd);
		final EditText textBox_name = (EditText) dialog.findViewById(R.id.input_statename);
//		final EditText outputMoore = (EditText) dialog.findViewById(R.id.et_ausgaengeMoore);
		final RelativeLayout outputTable = (RelativeLayout) dialog.findViewById(R.id.relativeTable);
		final BinPicker inputPicker = new BinPicker(context, graphController.getOuputCount(), 2, textsize);
		
		if(graphController.getCurrentType() == fsmType.Moore) {
			outputTable.addView(inputPicker, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		
		cB_start.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				cB_start.isChecked();
				cB_end.setChecked(false);
			}
		});

		cB_end.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				cB_end.isChecked();
				cB_start.setChecked(false);
			}
		});

		if (touchedStateIndex >= 0) {
			index = touchedStateIndex;
			dialog.setTitle("Zustand bearbeiten");
			btnOk.setText("Speichern");
			textBox_name.setText(graphController.getStateList().get(touchedStateIndex).getName());
			cB_start.setChecked(graphController.getStateList().get(touchedStateIndex).isStartState());
			cB_end.setChecked(graphController.getStateList().get(touchedStateIndex).isEndState());

			btnDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// delete state
					graphController.removeState(index);
					invalidate();
					dialog.dismiss();
				}
			});

		} else {
			dialog.setTitle("Zustand erstellen");
			textBox_name.setText(graphController.getNextName());
			btnDelete.setVisibility(INVISIBLE);
			index = touchedStateIndex;
		}

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// create new state
				if (!textBox_name.getText().toString().equals("")
						&& !(touchedStateIndex < 0 && graphController.getStatenames().contains(
										textBox_name.getText().toString()))) {
					if (index != -1) {
						graphController.getStateList().get(index).setName(textBox_name.getText().toString());
						if (cB_start.isChecked())
							graphController.setSingleStartState(index);
						if (cB_end.isChecked())
							graphController.setSingleEndState(index);
						if(graphController.getCurrentType() == fsmType.Moore)
							graphController.getStateList().get(index).setStateOutput(inputPicker.getValue().toString());
						dialog.dismiss();
					} else {
						// create new state
						//Mealy
						if(graphController.getCurrentType() == fsmType.Mealy) {
							graphController.addState(textBox_name.getText().toString(), "test", cB_start.isChecked(),
									cB_end.isChecked(), touchedPoint_x, touchedPoint_y);
							dialog.dismiss();
						}
						//Moore
						else if(graphController.getCurrentType() == fsmType.Moore) {
							graphController.addState(textBox_name.getText().toString(), inputPicker.getValue().toString(), cB_start.isChecked(),
								cB_end.isChecked(), touchedPoint_x, touchedPoint_y);
							dialog.dismiss();
						}
					}
				} else {
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
				invalidate();
			}
		});
		dialog.show();
	}

	Transition selectedTransition = null;

	// ### show popup Edit Transition ###
	public void showIOTransitions() {	
		float textsize = Utils.SetTextSize("text", (int) (graphController.getDisplay_width() * 0.035), (int) (graphController.getDisplay_height() * 0.035));
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.activity_transition_popup);
		
		
		//###	only picker and text
		LinearLayout ll_picker = (LinearLayout)dialog.findViewById(R.id.ll_picker);
		
		final TextView tv_Input = new TextView(context);
		tv_Input.setText(R.string.input);
		tv_Input.setTextSize(textsize);
		tv_Input.setGravity(Gravity.CENTER);
		
		final TextView tv_Output = new TextView(context);
		tv_Output.setText(R.string.output);
		tv_Output.setTextSize(textsize);
		tv_Output.setGravity(Gravity.CENTER);
		
		final BinPicker inputPicker = new BinPicker(context, graphController.getInputCount(), 1,textsize);
		inputPicker.setGravity(Gravity.CENTER);
		
		final BinPicker outputPicker = new BinPicker(context, graphController.getOuputCount(), 2,textsize);
		outputPicker.setGravity(Gravity.CENTER);
		
		ll_picker.addView(tv_Input);
		tv_Input.setPadding(0, 0, 0, 10);
		ll_picker.addView(inputPicker);
		inputPicker.setPadding(0, 0, 0, 50);
		ll_picker.addView(tv_Output);
		tv_Output.setPadding(0, 0, 0, 10);
		ll_picker.addView(outputPicker);
		ll_picker.setBackgroundColor(Color.rgb(220, 220, 220));
		//###
		
		//###Pickers with Button
//		LinearLayout ll_PicAndBtn = (LinearLayout)dialog.findViewById(R.id.ll_picAndBtn);
		ImageButton btn_add = (ImageButton) dialog.findViewById(R.id.add_btn);
//		ll_PicAndBtn.addView(ll_picker);
//		ll_PicAndBtn.addView(btn_add);
		//###
		
		//###(Pickers with Button) and Transition list
		LinearLayout ll_main = (LinearLayout)dialog.findViewById(R.id.ll_main_vertical);
		final ListView lv_transitionList = (ListView) dialog.findViewById(R.id.transationListView);
		lv_transitionList.setBackgroundColor(Color.rgb(200, 200, 200));
//		ll_main.addView(ll_PicAndBtn);
//		ll_main.addView(lv_transitionList);
		//###
		
		
//		ImageButton btn_add = (ImageButton) dialog.findViewById(R.id.add_btn);
//		final TextView outputTv = (TextView) dialog.findViewById(R.id.tv_output);
//		final RelativeLayout inView = (RelativeLayout) dialog.findViewById(R.id.inputPicker);
//		final RelativeLayout outView = (RelativeLayout) dialog.findViewById(R.id.outputPicker);
//
//		final BinPicker inputPicker = new BinPicker(context, graphController.getInputCount(), 1,textsize);
//		inputPicker.setGravity(Gravity.CENTER);
//		inView.addView(inputPicker, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		
//		final BinPicker outputPicker = new BinPicker(context, graphController.getOuputCount(), 2,textsize);
//		outputPicker.setGravity(Gravity.CENTER);
//		outView.addView(outputPicker, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		


		
		selectedTransition = null;

		if (graphController.getCurrentType() == fsmType.Moore) {
			outputPicker.setVisibility(INVISIBLE);
			tv_Output.setVisibility(INVISIBLE);
		}
		
		// if is not backcon
		if (graphController.getSelectedState().getID() != graphController
				.getStateList().get(touchedStateIndex).getID()) {
			for (Transition t : graphController.getSelectedState().getScp().getConnectedTransitions()) {
				if (t == null)
					continue;
				if (touchedStateIndex < 0)
					break;
				if (t.getState_to().getID() == graphController.getStateList().get(touchedStateIndex).getID()) {
					selectedTransition = t;
					break;
				}
			}
		} else {
			for (Transition bt : graphController.getSelectedState().getScp().getConnectedTransitions()) {
				if (bt == null)
					continue;
				if (bt.isBackConnection()) {
					selectedTransition = bt;
					break;
				}
			}
		}

		transi_id.clear();
		transi_input.clear();
		transi_output.clear();

		if (selectedTransition != null) {
			dialog.setTitle(selectedTransition.getState_from().getName() + " --> " + selectedTransition.getState_to().getName());
			int i = 1;
			for (TransitionValue tV : selectedTransition.getValueList()) {
				if (selectedTransition.getValueList().size() >= 1) {
					transi_id.add(i + ".");
					transi_input.add(tV.getValue());
					transi_output.add(tV.getOutput());
					i++;
				}
			}
		} else
			dialog.setTitle("Transition erstellen");

		TransitionListAdapter adp_TransitionList = new TransitionListAdapter(dialog.getContext(), transi_id, transi_input, transi_output);
		lv_transitionList.setAdapter(adp_TransitionList);

		btn_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				input = inputPicker.getValue();
				output = outputPicker.getValue();

				if(!graphController.getSelectedState().checkForOutgoingValue2(input)){
					Message.message(context, "Eingang bereits vergeben!");
					return;
				}
				
				//check for input
				//moore
				if (graphController.getCurrentType() == fsmType.Moore) {
					if (selectedTransition != null) {
						selectedTransition.addValueOutput(input, null);
					} else if (!input.equals("")) {
						graphController.addTransition(graphController.getStateList().get(selectedStateIndex),
								graphController.getStateList().get(touchedStateIndex), input, null);
					}

					
				//mealy
				} else if (graphController.getCurrentType() == fsmType.Mealy) {
					// # Edit Transition #
					if (selectedTransition != null && !input.equals("") && !output.equals("")) {
						selectedTransition.addValueOutput(input, output);
					} else if (!input.equals("") && !output.equals("")) {
						// # create new Transition #
						graphController.addTransition(graphController.getStateList().get(selectedStateIndex),
								graphController.getStateList().get(touchedStateIndex), input, output);
						graphController.deSelectAll();
					}

				}
				invalidate();
				dialog.dismiss();
			}
		});

		// update
		lv_transitionList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				transi_id.get(arg2);
			}
		});

		// long click to delete data
		lv_transitionList.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				build = new AlertDialog.Builder(dialog.getContext());
				build.setTitle("Delete " + transi_input.get(arg2) + " " + transi_output.get(arg2));
				build.setMessage("Do you want to delete ?");
				build.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog1, int which) {
								if (selectedTransition.getValueList().size() > 1) {
									selectedTransition.getValueList().remove(arg2);
								} else {
									graphController.removeTransition(selectedTransition);
								}
								dialog.dismiss();
								invalidate();
							}
						});

				build.setNegativeButton("No",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog1, int which) {
								dialog1.cancel();
							}
						});
				AlertDialog alert = build.create();
				alert.show();
				return true;
			}
		});
		dialog.show();
	}
	
	// ### Gesture Recognize ###
	class Gesturelistener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			showState();
			return super.onDoubleTap(e);
		}

		@Override
		public void onShowPress(MotionEvent e) {
			if (touchedStateIndex != -1) {
				graphController.deSelectAll();
				graphController.getStateList().get(touchedStateIndex)
						.setSelected(true);
				selectedStateIndex = touchedStateIndex;
				System.out.println("Gesture:\tLong press on state");
			}
			System.out.println("Gesture:\tLong press");
			invalidate();
			super.onShowPress(e);
		}
		
		@Override
		public boolean onSingleTapUp(MotionEvent e)	{
			// ## single tap on state
			if (touchedStateIndex != -1
					&& graphController.getStateList().size() > 0) {
				// # if one is selected create transition, else select it #
				if (graphController.haveSelectedState()) {
					System.out.println("fromOutput"+graphController.getSelectedState().getCurrOutputCount() +" MaxOutput:"+ graphController.getOuputCount() +" toInput:"+
							graphController.getStateList().get(touchedStateIndex).getCurrInputCount() +" MaxInput:"+ graphController.getInputCount());
					showIOTransitions();
				} else {
					graphController.deSelectAll();
					graphController.getStateList().get(touchedStateIndex).setSelected(true);
					selectedStateIndex = touchedStateIndex;
				}
			}

			// ## single tap on transition notification ##
			if (touchedNotationIndex != -1
					&& graphController.getTransitionList().size() > 0) {
				if(!graphController.getTransitionList().get(touchedNotationIndex).isSelected())
					graphController.getTransitionList().get(touchedNotationIndex).setSelected(true);
				else graphController.removeTransition(graphController.getTransitionList().get(touchedNotationIndex));
			}

			// ## single tap on drag point ##
			if (touchedDragPointIndex != -1
					&& graphController.getTransitionList().size() > 0) {
				graphController.getTransitionList().get(touchedDragPointIndex).setSelected(true);
				if (graphController.getTransitionList().get(touchedDragPointIndex) != null) {
					if (graphController.getTransitionList().get(touchedDragPointIndex).isMarkedAsDeletion()) {
						graphController.removeTransition(graphController.getTransitionList().get(touchedDragPointIndex));
						invalidate();
					} else
						graphController.getTransitionList().get(touchedDragPointIndex).setMarkedAsDeletion(true);
				}
			}
			
			// ## single into the white ##
			if (touchedNotationIndex == -1 && touchedDragPointIndex == -1 && touchedStateIndex == -1) {
				graphController.deSelectAll();
				graphController.unmarkDeletion();
			}

			invalidate();
			// }
			System.out.println("action up");
			return super.onSingleTapUp(e);
		}

		@Override
		public boolean onDown(MotionEvent e) {
			System.out.println("onDown");
			return super.onDown(e);
		}
	}	
}