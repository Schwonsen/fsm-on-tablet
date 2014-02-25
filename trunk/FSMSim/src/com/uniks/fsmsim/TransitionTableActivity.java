package com.uniks.fsmsim;

import com.uniks.fsmsim.util.Message;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TransitionTableActivity extends View {
	
	public TransitionTableActivity(Context context) {
		super(context);
		
		// TODO Auto-generated constructor stub
		
		TableLayout layout = new TableLayout(context);
	    
	    TableLayout table = new TableLayout(context);
	    
	    for (int i = 0; i < 4; i++) {
            
            TableRow row = new TableRow(context);
             
            for (int j = 0; j < 4; j++) {
                 
            TextView cell = new TextView(context) {
                @Override
                protected void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    Rect rect = new Rect();
                    Paint paint = new Paint();
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.WHITE);
                    paint.setStrokeWidth(2);
                    getLocalVisibleRect(rect);
                    canvas.drawRect(rect, paint);       
                }
            };
            
            if(i == 0 && j == 0) {
            	cell.setText("Eingabe");
            }
            else if(i == 0 && j == 1) {
            	cell.setText("  Zt  ");
            }
            else if(i == 0 && j == 2) {
            	cell.setText("Zt+r");
            }
            else if(i == 0 && j == 3) {
            	cell.setText("Ausgabe");
            }
            
            //Eingabe
            if(i != 0 && j == 0)
            	cell.setText("0\n1");
            //Zustände
            //TODO Namen der states_from
            if(i != 0 && j != 0 && j != 3)
            	cell.setText("S1\nS1");
            //TODO Name der states_to
            if(i != 0 && j == 2)
            	cell.setText("S2\nS2");
            //Ausgabe
            //TODO werter der Ausgabe
            if(i != 0 && j == 3)
            	cell.setText("1\n1");
            
            cell.setPadding(6, 4, 6, 4);
            row.addView(cell);
            }
            table.addView(row);
        }
        layout.addView(table);
	}
}
