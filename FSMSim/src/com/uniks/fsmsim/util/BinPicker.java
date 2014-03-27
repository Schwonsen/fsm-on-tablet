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
	int cellWidth = 40;
	int cellHeight = 40;
	int bgColor = Color.rgb(80, 80, 80);
	
	boolean isUndef = false;
	
    TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT);
    

	

	
	//Output
	TextView output;
	
	public BinPicker(Context context, int count) {
		super(context);
		this.context = context;

		
		this.count = count;
		
		
		for(int i = 0; i < count;i++){
			binCode += "0";
		}
		
		final TextView cellUndef = new TextView(context);
		cellUndef.setTextSize(20);
		cellUndef.setText(" - ");
		cellUndef.setTextColor(Color.WHITE);
		cellUndef.setBackgroundColor(bgColor);
		cellUndef.setGravity(Gravity.CENTER);
		
		
		cellUndef.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				isUndef = !isUndef;
				
				if(isUndef){
					cellUndef.setBackgroundColor(Color.BLUE);
				}else{
					cellUndef.setBackgroundColor(bgColor);
				}
				System.out.println(getValue());
				return false;
			}
		});
		
		this.addView(cellUndef,cellWidth*2,cellHeight*2);
		



		this.picTable = getPicTable();
		this.addView(picTable);
	}
	

	
	public TableLayout getPicTable(){
		TableLayout picTable = new TableLayout(context);
		//Rows
		TableRow rowZero = new TableRow(context);
		TableRow rowOne = new TableRow(context);
		
		for (int j = 0; j < count; j++) {	
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
//						cellOne.setTextColor(Color.BLACK);
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
//						cellZero.setTextColor(Color.BLACK);
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
	
	public String getValue(){
		if(isUndef){
			return "-";
		}
		else return binCode;
	}
	
	

}
