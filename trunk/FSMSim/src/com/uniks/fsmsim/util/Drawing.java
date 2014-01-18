package com.uniks.fsmsim.util;

import com.uniks.fsmsim.controller.GraphController;
import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.data.State;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

public class Drawing extends View{
	private GraphController graphController;

	//drawing and canvas paint
	private Paint paintCircle, paintText;
	boolean isPrepared = false;
	
	public Drawing(Context context, GraphController controller) {
		super(context);
		graphController = controller;
		setupDrawing();
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
		isPrepared = true;
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
		
		if(!isPrepared)return;
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
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		float x = event.getX(), y = event.getY();
		
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				State s = new State(graphController.getCurrentType(),"s1");

				return true;
				
			default: return false;
		}	
	}
	
}
