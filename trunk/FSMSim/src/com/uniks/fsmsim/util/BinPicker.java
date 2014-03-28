package com.uniks.fsmsim.util;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BinPicker extends LinearLayout{
	TableLayout picTable;
	int count;
	String binCode = "";
	Context context;
	int cellWidth = 30;
	int cellHeight = 30;
	int bgColor = Color.rgb(80, 80, 80);
	
	TextView output;
	
	
    TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT);
	
    
	public BinPicker(Context context, int count) {
		super(context);
		this.context = context;
		output = new TextView(context);
		this.setOrientation(LinearLayout.VERTICAL);

		
		this.count = count;
		
		
		for(int i = 0; i < count;i++){
			binCode += "0";
		}
		

		this.picTable = getPicTable();
		this.addView(picTable);
		
		output.setTextSize(20);
		output.setText(binCode);
		output.setBackgroundColor(Color.rgb(100, 100, 100));
		output.setTextColor(Color.WHITE);
		output.setGravity(Gravity.CENTER);
		this.addView(output);
		
		
	}

	
	public TableLayout getPicTable(){
		TableLayout picTable = new TableLayout(context);
		//Rows
		TableRow rowUndef = new TableRow(context);
		TableRow rowZero = new TableRow(context);
		TableRow rowOne = new TableRow(context);
		
		for (int j = 0; j < count; j++) {	
			
			final TextView cellUndef = new TextView(context);
			cellUndef.setTextSize(20);
			cellUndef.setText(" - ");
			cellUndef.setTag(j);
			cellUndef.setBackgroundColor(bgColor);
			cellUndef.setTextColor(Color.WHITE);
			cellUndef.setGravity(Gravity.CENTER);
			rowUndef.addView(cellUndef,cellWidth,cellHeight);
			
			final TextView cellZero = new TextView(context);
			cellZero.setTextSize(20);
			cellZero.setText(" 0 ");
			cellZero.setTag(j);
			cellZero.setBackgroundColor(Color.BLUE);
			cellZero.setTextColor(Color.WHITE);
			cellZero.setGravity(Gravity.CENTER);
			rowZero.addView(cellZero,cellWidth,cellHeight);
			
			final TextView cellOne = new TextView(context);
			cellOne.setBackgroundColor(bgColor);
			cellOne.setTextColor(Color.WHITE);
			cellOne.setTextSize(20);
			cellOne.setText(" 1 ");
			cellOne.setTag(j);
			cellOne.setGravity(Gravity.CENTER);
			rowOne.addView(cellOne,cellWidth,cellHeight);
			
			
			//OnTouch 
			cellZero.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					//show active
						cellZero.setBackgroundColor(Color.BLUE);
//						cellZero.setTextColor(bgColor);
					//edit simulationValue
						StringBuilder sb = new StringBuilder(binCode);
						sb.setCharAt((Integer)cellZero.getTag(), '0');
						binCode = sb.toString();
					//show opposite as non active
						cellOne.setBackgroundColor(bgColor);
						cellUndef.setBackgroundColor(bgColor);
//						cellOne.setTextColor(Color.BLACK);
						output.setText(binCode);
						System.out.println(binCode);
					return false;
				}
			});
			
			cellOne.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					//show active
						cellOne.setBackgroundColor(Color.BLUE);
//						cellOne.setTextColor(bgColor);
					//edit simulationValue
						StringBuilder sb = new StringBuilder(binCode);
						sb.setCharAt((Integer)cellOne.getTag(), '1');
						binCode = sb.toString();
					//show opposite as non active
						cellZero.setBackgroundColor(bgColor);
						cellUndef.setBackgroundColor(bgColor);
//						cellZero.setTextColor(Color.BLACK);
						output.setText(binCode);
						System.out.println(binCode);
					return false;
				}
			});
			
			cellUndef.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					//show active
						cellUndef.setBackgroundColor(Color.BLUE);
//						cellOne.setTextColor(bgColor);
					//edit simulationValue
						StringBuilder sb = new StringBuilder(binCode);
						sb.setCharAt((Integer)cellOne.getTag(), '-');
						binCode = sb.toString();
					//show opposite as non active
						cellZero.setBackgroundColor(bgColor);
						cellOne.setBackgroundColor(bgColor);
//						cellZero.setTextColor(Color.BLACK);
						output.setText(binCode);
						System.out.println(binCode);
					return false;
				}
			});
		}
		
		picTable.addView(rowUndef);
		picTable.addView(rowZero);
		picTable.addView(rowOne);
		picTable.setLayoutParams(trParams);
		
		return picTable;
	}
	
	public String getValue(){
		return binCode;
	}
}
