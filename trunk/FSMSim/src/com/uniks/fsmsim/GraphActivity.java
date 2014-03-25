package com.uniks.fsmsim;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import com.uniks.fsmsim.controller.GraphController;
import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.data.DbHelper;
import com.uniks.fsmsim.data.State;
import com.uniks.fsmsim.data.Transition;
import com.uniks.fsmsim.data.Transition.TransitionValue;
import com.uniks.fsmsim.util.DrawingV2;
import com.uniks.fsmsim.util.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class GraphActivity extends Activity {
	GraphController controller;
	TextView tV_test;
	private String fName;
	private EditText fileName;
	private Button btn_cancel;
	private Button btn_save;
	private SQLiteDatabase dataBase;
	private DbHelper mHelper;
	final Context context = this;
	private EditText file;
	private boolean onMenuTableTouch;
	private GestureDetector mDetector = null; 
	PopupWindow popupWinow;
	private int counter;
	private int counter2;
	Bundle mainMenuContent;
	protected boolean isTouched = false;
	
	public GraphActivity() {
		//must have an empty constructor
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_graph);
		mainMenuContent = getIntent().getExtras();
		controller = new GraphController(fsmType.getEnumByValue(mainMenuContent
				.getInt("fsmType")), mainMenuContent.getInt("inputCount"),
				mainMenuContent.getInt("outputCount"));
		
		//##	Windowsize	##
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		controller.setDisplay_height(displaymetrics.heightPixels);
		controller.setDisplay_width(displaymetrics.widthPixels);
		
		//##	Top BarSize	##    
	    Resources resources = context.getResources();
	    int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
	    if (resourceId > 0) {
	    	controller.setDisplay_TopBarSize(resources.getDimensionPixelSize(resourceId));
	    }
	    
	    //##	Bot BarSize	##    
	    resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
	    if (resourceId > 0) {
	        controller.setDisplay_BotBarSize(resources.getDimensionPixelSize(resourceId));
	    }
	    
	    //##	Set ContetntView	## 
		setContentView(new DrawingV2(this,controller));
		
		System.out.println("StartData: " + controller.getInputCount() + " "
				+ controller.getOuputCount() + " "
				+ controller.getCurrentType());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.graph_menu, menu);
		return true;
	}

	// Methode um Popups anzuzeigen
	private void showPopup() {

		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.activity_popup_save);
		dialog.setTitle(R.string.popup_save);
		dialog.setCancelable(false);
		
		Button dialogButton = (Button) dialog.findViewById(R.id.btn_save);
		
		// if button is clicked, make save entry
		dialogButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				file = (EditText) findViewById(R.id.eT_dataname);
				Message.message(context, "Automat " + file
						+ " wurde gespeichert!");
				dialog.dismiss();
			}
		});

		dialogButton = (Button) dialog.findViewById(R.id.btn_cancel);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();

		// startActivity(new Intent(GraphActivity.this, SaveActivity.class));
	}

	public void showload() {
//		startActivity(new Intent(GraphActivity.this, LoadActivity.class));
	}
	
	public void showSimulationTable() 
	{		
		counter = 1;
		
		RelativeLayout sim = new RelativeLayout(this);
		
		RelativeLayout.LayoutParams tlp = new RelativeLayout.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT,
				TableLayout.LayoutParams.MATCH_PARENT);
		
		View popupview = getLayoutInflater().inflate(R.layout.sim_pop, sim,false);
		Button btnClock = (Button) popupview.findViewById(R.id.takt);
		ImageView cancelPopup = (ImageView) popupview.findViewById(R.id.cancelImage);
		TableLayout table = (TableLayout) popupview.findViewById(R.id.tableView_Values);
		
		popupview.setLayoutParams(tlp);
		popupview.setBackgroundColor(Color.WHITE);
		
		final PopupWindow tablePopup = new PopupWindow(popupview,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		tablePopup.setOutsideTouchable(false);
		tablePopup.setTouchable(true);
		tablePopup.setBackgroundDrawable(new BitmapDrawable());
		popupview.setAlpha(0.75f);
		
		TableRow rowHeader = new TableRow(this);
		TableRow numbersZero = new TableRow(this);
		TableRow numbersOne = new TableRow(this);
		
		//Columns
		for (int j = 1; j <= controller.getInputCount(); j++) {
			TextView cell = new TextView(this);
			cell.setText(" x" + j +"");
			cell.setPadding(6, 4, 6, 4);
			rowHeader.addView(cell);
			
			final TextView zero = new TextView(this);
			zero.setTextSize(20);
			zero.setText(" 0 ");
			numbersZero.addView(zero);
			
			final TextView one = new TextView(this);
			one.setTextSize(20);
			one.setText(" 1 ");
			numbersOne.addView(one);
					
			zero.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					if(isTouched == false) {
						zero.setBackgroundColor(Color.BLUE);
						zero.setTextColor(Color.WHITE);
						isTouched = true;
					} else {
						zero.setBackgroundColor(Color.WHITE);
						zero.setTextColor(Color.BLACK);
						isTouched = false;
					}
					return false;
				}
			});
			
			one.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(isTouched == false) {
						one.setBackgroundColor(Color.BLUE);
						one.setTextColor(Color.WHITE);
						isTouched = true;
					} else {
						one.setBackgroundColor(Color.WHITE);
						one.setTextColor(Color.BLACK);
						isTouched = false;
					}
					return false;
				}
			});
		}
		
		for(int i = 0; i < 3; i++) {
			if(i == 0)
			table.addView(rowHeader);
			if(i == 1)
			table.addView(numbersZero);
			if(i == 2)
			table.addView(numbersOne);
		}
		
		btnClock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Zeichnen nach dem Takten
				Message.message(context, "Takt");
			}
		});
		//close the popup
		cancelPopup.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				tablePopup.dismiss();
				counter = 0;
				return false;
			}
		});
		
		
//		tablePopup.setTouchInterceptor(new View.OnTouchListener() {
//
//	        @Override
//	        public boolean onTouch(View v, MotionEvent event) {
//	            if (event.getAction() == MotionEvent.ACTION_MOVE) {
//	                tablePopup.dismiss();
//	                counter = 0;
////	                Message.message(context, "Test");
//	            }
//	            return true;
//	        }
//	    });
		tablePopup.showAtLocation(popupview, Gravity.BOTTOM | Gravity.LEFT, 0,0);	
	}
		
	public void showTransitionTable() {

		TableLayout layout = new TableLayout(this);
		
		TableLayout.LayoutParams tlp = new TableLayout.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT,
				TableLayout.LayoutParams.MATCH_PARENT);

		TableLayout table = new TableLayout(this);
		table.setLayoutParams(tlp);
		table.setBackgroundColor(Color.WHITE);
		
		TableRow rowHeader = new TableRow(this);
		//Columns
		for (int j = 0; j < 4; j++) {
			TextView cell = new TextView(this) {
				@Override
				protected void onDraw(Canvas canvas) {
					super.onDraw(canvas);
					Rect rect = new Rect();
					Paint paint = new Paint();
					paint.setStyle(Paint.Style.STROKE);
					paint.setColor(Color.BLACK);
					paint.setStrokeWidth(2);
					getLocalVisibleRect(rect);
					canvas.drawRect(rect, paint);
				}
			};

			if (j == 0) {
				cell.setText("Eingabe");
			} else if (j == 1) {
				cell.setText("  Zt  ");
			} else if (j == 2) {
				cell.setText("Zt+r");
			} else if (j == 3) {
				cell.setText("Ausgabe");
			}
			
			cell.setPadding(6, 4, 6, 4);
			rowHeader.addView(cell);
		}
		table.addView(rowHeader);
		
		//TODO
		//Rows
		for (State s : controller.getStateList()) {
			TableRow row = new TableRow(this);
			
			//Cells
			String input ="", currState ="", stateTo ="", output ="";
			for(Transition t : s.getScp().getOutgoingTransitions()) {
				for(TransitionValue ts : t.getValueList()){
					input += ts.getValue() + "\n";
					currState += s.getName() + "\n";
					stateTo += t.getState_to().getName() + "\n";
					if(controller.getCurrentType() == fsmType.Mealy)
						output += ts.getOutput() + "\n";
					else output += t.getState_to().getStateOutput() + "\n";
				}
			}
			
			
			if(input.length() > 0 && currState.length() > 0 && stateTo.length() > 0 && output.length() > 0 ){
				//remove last /n
				input = input.substring(0, input.length() - 1);
				currState = currState.substring(0, currState.length() - 1);
				stateTo = stateTo.substring(0, stateTo.length() - 1);
				output = output.substring(0, output.length() - 1);
			
			
			//Columns
			for (int j = 0; j < 4; j++) {
				TextView cell = new TextView(this) {
					@Override
					protected void onDraw(Canvas canvas) {
						super.onDraw(canvas);
						Rect rect = new Rect();
						Paint paint = new Paint();
						paint.setStyle(Paint.Style.STROKE);
						paint.setColor(Color.BLACK);
						paint.setStrokeWidth(2);
						getLocalVisibleRect(rect);
						canvas.drawRect(rect, paint);
					}
				};
				
				//write content to cells
				if(j == 0)
					cell.setText(input);
				if(j == 1)
					cell.setText(currState);
				if(j == 2)
					cell.setText(stateTo);
				if(j == 3)
					cell.setText(output);
				
				cell.setPadding(6, 4, 6, 4);
				row.addView(cell);
				}
			}
			
			table.addView(row);
		}
		
		layout.addView(table); 
		System.out.println("new table");
		View popupview = layout;

		final PopupWindow tablePopup = new PopupWindow(popupview,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		tablePopup.setOutsideTouchable(true);
		tablePopup.setTouchable(true);
		tablePopup.setBackgroundDrawable(new BitmapDrawable());
		popupview.setAlpha(0.75f);
		tablePopup.setTouchInterceptor(new View.OnTouchListener() {

	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            if (event.getAction() == MotionEvent.ACTION_MOVE) {
	            	if(tablePopup.isShowing()) {
	            		tablePopup.dismiss();
	            		counter2 = 0;
	            	}
	            }
	            return true;
	        }
	    });
		tablePopup.showAtLocation(popupview, Gravity.BOTTOM | Gravity.RIGHT, 0,0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.item_save:
			showPopup();
			return true;
		case R.id.item_load:
			// showload();
			Message.message(this, "Automat wurde geladen!");
			return true;
		case R.id.item_new:
			Message.message(context, "Neuer Automat!");
			controller.clear();
//			findViewById(android.R.id.content).postInvalidate();
			setContentView(new DrawingV2(this,controller));
			return true;
		case R.id.item_simulation:
			if (counter == 0) {
				showSimulationTable();
			}
			return true;
		case R.id.item_table:
			if (counter2 == 0) {
			showTransitionTable();
			counter2 = 1;
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Sind sie Sicher? Alles nicht gespeicherte geht verloren!")
				.setCancelable(false)
				.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						startActivity(new Intent(GraphActivity.this,
								MainActivity.class));
						GraphActivity.this.finish();

					}
				})
				.setNegativeButton("Nein",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}
}