package com.uniks.fsmsim.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimpleVerticalPicker extends LinearLayout{
	Context context;
	float textSize;
	int width;
	
	int bgColor = Color.rgb(80, 80, 80);
//	List<String> elements = new ArrayList<String>();
	List<TextView> allTextViews;
	
	String output;
	
	public SimpleVerticalPicker(Context context, List<String> elements, float textSize, int width) {
		super(context);
		this.context = context;
		this.textSize = textSize;
		this.width = width;
		this.setOrientation(LinearLayout.VERTICAL);
		createView(elements);

	}
	public void createView(List<String> elements){
		this.removeAllViews();
		allTextViews = new ArrayList<TextView>();
		
		boolean first = true;
		for(String s : elements){
			final TextView tv = new TextView(context);
			tv.setTextSize(textSize);
			tv.setText(s);
			tv.setGravity(Gravity.CENTER);
			tv.setBackgroundColor(bgColor);
			tv.setTextColor(Color.WHITE);
			
			if(first){
				output = s;
				tv.setBackgroundColor(Color.BLUE);
				first = false;
			}
			
			allTextViews.add(tv);
			tv.measure(0, 0);
			this.addView(tv);
			
			//OnTouch 
			tv.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					//show all as non active
					resetTextViews();
					//show active
					tv.setBackgroundColor(Color.BLUE);
					//edit output
						output = tv.getText().toString();
					return false;
				}
			});
		}
	}
	
	public void resetTextViews(){
		if(allTextViews != null)
		for(TextView tv : allTextViews){
			tv.setBackgroundColor(bgColor);
		}
	}
	
	public String getOutput(){
		return output;
	}
}
