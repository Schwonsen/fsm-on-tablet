package com.uniks.fsmsim.util;

import com.uniks.fsmsim.R;
import com.uniks.fsmsim.controller.GraphController;
import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.data.State;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Drawing extends View{
	private GraphController graphController;
	private GestureDetectorCompat detector;
	private Context context;

	//drawing and canvas paint
	private Paint paintCircle, paintText;
	
	public Drawing(Context context, GraphController controller) {
		super(context);
		this.context = context;
		graphController = controller;
		setupDrawing();
		detector = new GestureDetectorCompat(context, new Gesturelistener());
	}
	
	//setup drawing
	private void setupDrawing(){
		//prepare for drawing and setup paint stroke properties
		paintCircle = new Paint();
		paintText = new Paint();
		paintCircle.setStyle(Paint.Style.STROKE);
		paintCircle.setStrokeWidth(5f);
		paintCircle.setColor(Color.BLACK);
		paintText.setTextSize(30);
//		isPrepared = true;
	}
	
	//size assigned to view
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		System.out.println("scale! w:"+oldw+" to:"+w+" h:"+oldh+ " to:"+h);
	}
	
	//draw the view - will be called after touch event
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		//Draw states
		for (State state : graphController.getStateList()) {
			
			canvas.drawCircle(state.getX(), state.getY(), state.getRadius(), paintCircle);
			
			if(state.getType() == fsmType.Moore){
				canvas.drawLine(state.getX() - state.getRadius(), state.getY(), state.getX()
						+ state.getRadius(), state.getY(), paintCircle);
				
				canvas.drawText(state.getName(), state.getX()-15, state.getY()-10, paintText);
				canvas.drawText(state.getStateOutput(), state.getX()-15, state.getY()+30, paintText);
			}else{
				canvas.drawText(state.getName(), state.getX()-15, state.getY()+7, paintText);
			}
		}
	}
	
	
	private int touchedLoc = -1;
	float oldX, oldY;
	@Override
	public boolean onTouchEvent(MotionEvent event){
		float x = event.getX(), y = event.getY();
		this.detector.onTouchEvent(event);
		
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				int i = 0;
				for (State s : graphController.getStateList()) {
					if(x <= (s.getX()+s.getRadius()) && x >= (s.getX()-s.getRadius())){
						if(y <= (s.getY()+s.getRadius()) && y >= (s.getY()-s.getRadius())){
							touchedLoc = i;
							oldX = s.getX();
							oldY = s.getY();
							break;
						}
					}
					i++;
				}

				break;
				
			case MotionEvent.ACTION_UP:
				touchedLoc = -1;
				if(Math.abs((oldX - x)) < 3 && Math.abs((oldY - y)) < 3){
					System.out.println("open popUp here");
				}
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
	
	public void showState(){
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.edit_state_popup);
		dialog.setTitle("Zustand erstellen");
		
		Button btnOk = (Button) (Button) dialog.findViewById(R.id.btn_ok);
		
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Message.message(context, "Zustand wurde erstellt!");
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	class Gesturelistener extends GestureDetector.SimpleOnGestureListener{
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			System.out.println("doubleTap");
			showState();
			return super.onDoubleTap(e);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			System.out.println("Long press");
			super.onLongPress(e);
		}
		
		
	}
	
}
