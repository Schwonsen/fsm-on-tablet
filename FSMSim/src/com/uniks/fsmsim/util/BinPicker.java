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
	TableLayout picTableMoore;
	int count;
	int type;
	String binCode = "";
	Context context;
	int cellWidth = 30;
	int cellHeight = 30;
	int bgColor = Color.rgb(80, 80, 80);
	float textSize;
	
	TextView output;
	
	
    TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT);
	
    //Type of BinPicker: if 1 BinPicker with UndefValues else only with 0/1 value
	public BinPicker(Context context, int count, int type, float textSize) {
		super(context);
		this.context = context;
		this.textSize = textSize;
		output = new TextView(context);
		this.setOrientation(LinearLayout.VERTICAL);
		this.count = count;
		
		for(int i = 0; i < count;i++){
			binCode += "0";
		}
		
		this.type = type;
		this.picTableMoore = getPicTableMoore();
		this.picTable = getPicTable();
		if(type == 1) {
			this.addView(picTable);
		} else {
			this.addView(picTableMoore);
		}
		
		output.setTextSize(textSize);
		output.setText(binCode);
		output.setBackgroundColor(Color.rgb(200, 200, 200));
		output.setTextColor(Color.BLUE);
		output.setGravity(Gravity.CENTER);
		this.addView(output);
	}
	
	public TableLayout getPicTableMoore() {
		TableLayout picTable = new TableLayout(context);
		//Rows
		TableRow rowZero = new TableRow(context);
		TableRow rowOne = new TableRow(context);
		
		for (int j = 0; j < count; j++) {	
					
			final TextView cellZero = new TextView(context);
			cellZero.setTextSize(textSize);
			cellZero.setText(" 0 ");
			cellZero.setTag(j);
			cellZero.setBackgroundColor(Color.BLUE);
			cellZero.setTextColor(Color.WHITE);
			cellZero.setGravity(Gravity.CENTER);
			rowZero.addView(cellZero,(int)textSize*2,LayoutParams.WRAP_CONTENT);
			
			final TextView cellOne = new TextView(context);
			cellOne.setBackgroundColor(bgColor);
			cellOne.setTextColor(Color.WHITE);
			cellOne.setTextSize(textSize);
			cellOne.setText(" 1 ");
			cellOne.setTag(j);
			cellOne.setGravity(Gravity.CENTER);
			rowOne.addView(cellOne,(int)textSize*2, LayoutParams.WRAP_CONTENT);
			
			
			//OnTouch 
			cellZero.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					//show active
						cellZero.setBackgroundColor(Color.BLUE);
					//edit simulationValue
						StringBuilder sb = new StringBuilder(binCode);
						sb.setCharAt((Integer)cellZero.getTag(), '0');
						binCode = sb.toString();
					//show opposite as non active
						cellOne.setBackgroundColor(bgColor);
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
					//edit simulationValue
						StringBuilder sb = new StringBuilder(binCode);
						sb.setCharAt((Integer)cellOne.getTag(), '1');
						binCode = sb.toString();
					//show opposite as non active
						cellZero.setBackgroundColor(bgColor);
						output.setText(binCode);
						System.out.println(binCode);
					return false;
				}
			});
		}
		picTable.addView(rowZero);
		picTable.addView(rowOne);
		picTable.setLayoutParams(trParams);
		
		return picTable;
	}

	
	public TableLayout getPicTable(){
		TableLayout picTable = new TableLayout(context);
		//Rows
		TableRow rowUndef = new TableRow(context);
		TableRow rowZero = new TableRow(context);
		TableRow rowOne = new TableRow(context);
		
		for (int j = 0; j < count; j++) {	
			
			final TextView cellUndef = new TextView(context);
			cellUndef.setTextSize(textSize);
			cellUndef.setText(" - ");
			cellUndef.setTag(j);
			cellUndef.setBackgroundColor(bgColor);
			cellUndef.setTextColor(Color.WHITE);
			cellUndef.setGravity(Gravity.CENTER);
			rowUndef.addView(cellUndef,(int)textSize*2,LayoutParams.WRAP_CONTENT);
			
			final TextView cellZero = new TextView(context);
			cellZero.setTextSize(textSize);
			cellZero.setText(" 0 ");
			cellZero.setTag(j);
			cellZero.setBackgroundColor(Color.BLUE);
			cellZero.setTextColor(Color.WHITE);
			cellZero.setGravity(Gravity.CENTER);
			rowZero.addView(cellZero,(int)textSize*2,LayoutParams.WRAP_CONTENT);
			
			final TextView cellOne = new TextView(context);
			cellOne.setBackgroundColor(bgColor);
			cellOne.setTextColor(Color.WHITE);
			cellOne.setTextSize(textSize);
			cellOne.setText(" 1 ");
			cellOne.setTag(j);
			cellOne.setGravity(Gravity.CENTER);
			rowOne.addView(cellOne,(int)textSize*2, LayoutParams.WRAP_CONTENT);
			
			//OnTouch 
			cellZero.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					//show active
						cellZero.setBackgroundColor(Color.BLUE);
					//edit simulationValue
						StringBuilder sb = new StringBuilder(binCode);
						sb.setCharAt((Integer)cellZero.getTag(), '0');
						binCode = sb.toString();
						cellOne.setBackgroundColor(bgColor);
						cellUndef.setBackgroundColor(bgColor);
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
					//edit simulationValue
						StringBuilder sb = new StringBuilder(binCode);
						sb.setCharAt((Integer)cellOne.getTag(), '1');
						binCode = sb.toString();
					//show opposite as non active
						cellZero.setBackgroundColor(bgColor);
						cellUndef.setBackgroundColor(bgColor);
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
					//edit simulationValue
						StringBuilder sb = new StringBuilder(binCode);
						sb.setCharAt((Integer)cellOne.getTag(), '-');
						binCode = sb.toString();
					//show opposite as non active
						cellZero.setBackgroundColor(bgColor);
						cellOne.setBackgroundColor(bgColor);
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
