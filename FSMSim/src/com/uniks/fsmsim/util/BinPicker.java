package com.uniks.fsmsim.util;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BinPicker extends TableLayout{
	int count;
	String binCode = "";
	Context context;
	int cellWidth = 40;
	int cellHeight = 40;
	
	//Rows
	TableRow rowUndef;
	TableRow rowZero;
	TableRow rowOne;
	
	//Output
	TextView output;
	
	public BinPicker(Context context, int count) {
		super(context);
		this.context = context;
		this.count = count;
		
		rowUndef = new TableRow(context);
		rowZero = new TableRow(context);
		rowOne = new TableRow(context);
		
		for(int i = 0; i < count;i++){
			binCode += "0";
		}
		
		for (int j = 0; j < count; j++) {
			
			final TextView cellUndef = new TextView(context);
			cellUndef.setTextSize((int)(18));
			cellUndef.setText(" - ");
			cellUndef.setTag(j);
			rowUndef.addView(cellUndef,cellWidth,cellHeight);
			
			final TextView cellZero = new TextView(context);
			cellZero.setTextSize(20);
			cellZero.setText(" 0 ");
			cellZero.setTag(j);
			cellZero.setBackgroundColor(Color.BLUE);
			cellZero.setTextColor(Color.WHITE);
			rowZero.addView(cellZero,cellWidth,cellHeight);
			
			final TextView cellOne = new TextView(context);
			cellOne.setTextSize(20);
			cellOne.setText(" 1 ");
			cellOne.setTag(j);
			rowOne.addView(cellOne,cellWidth,cellHeight);
			
			
			//OnTouch 
			cellZero.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					//show active
						cellZero.setBackgroundColor(Color.BLUE);
						cellZero.setTextColor(Color.WHITE);
					//edit simulationValue
						StringBuilder sb = new StringBuilder(binCode);
						sb.setCharAt((Integer)cellZero.getTag(), '0');
						binCode = sb.toString();
					//show opposite as non active
						cellOne.setBackgroundColor(Color.WHITE);
						cellOne.setTextColor(Color.BLACK);
						cellUndef.setBackgroundColor(Color.WHITE);
						cellUndef.setTextColor(Color.BLACK);
						System.out.println(binCode);
					return false;
				}
			});
			
			cellOne.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					//show active
						cellOne.setBackgroundColor(Color.BLUE);
						cellOne.setTextColor(Color.WHITE);
					//edit simulationValue
						StringBuilder sb = new StringBuilder(binCode);
						sb.setCharAt((Integer)cellOne.getTag(), '1');
						binCode = sb.toString();
					//show opposite as non active
						cellZero.setBackgroundColor(Color.WHITE);
						cellZero.setTextColor(Color.BLACK);
						cellUndef.setBackgroundColor(Color.WHITE);
						cellUndef.setTextColor(Color.BLACK);
						System.out.println(binCode);
					return false;
				}
			});
			
			cellUndef.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					//show active
						cellUndef.setBackgroundColor(Color.BLUE);
						cellUndef.setTextColor(Color.WHITE);
					//edit simulationValue
						StringBuilder sb = new StringBuilder(binCode);
						sb.setCharAt((Integer)cellOne.getTag(), '-');
						binCode = sb.toString();
					//show opposite as non active
						cellZero.setBackgroundColor(Color.WHITE);
						cellZero.setTextColor(Color.BLACK);
						cellOne.setBackgroundColor(Color.WHITE);
						cellOne.setTextColor(Color.BLACK);
						System.out.println(binCode);
					return false;
				}
			});
		}
		
		addView(rowUndef);
		addView(rowZero);
		addView(rowOne);
	}
	
	public String getValue(){
		return binCode;
	}
	
	

}
