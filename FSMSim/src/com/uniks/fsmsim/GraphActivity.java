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
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
	View drawView;
	
	public GraphActivity() {
		//must have an empty constructor
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	    drawView = new DrawingV2(this, controller);
		setContentView(drawView);
		
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
	
	String simulationValue = "", simulationOutput = "";
	private State stateInSimulation;
	private Transition transitionInSimulation;
	
	public void showSimulationTable() 
	{		
		//init simulationValue
		simulationValue = "";
		simulationOutput = "";
		for(int i = 0; i < controller.getInputCount();i++){
			simulationValue += "0";
		}
		
		//check if contains startstate
		if(controller.getStartState()== null){
			Message.message(context, "Bitte Startzustand setzten");
			return;
		}
		//init startState
		controller.getStartState().setInSimulation(true);
		stateInSimulation = controller.getStartState();
		drawView.invalidate();

//TODO
		counter = 1;
		RelativeLayout sim = new RelativeLayout(this);
		
		RelativeLayout.LayoutParams tlp = new RelativeLayout.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT,
				TableLayout.LayoutParams.MATCH_PARENT);
		
		//UI Elements
		View popupview = getLayoutInflater().inflate(R.layout.sim_pop, sim,false);
		Button btnClock = (Button) popupview.findViewById(R.id.takt);
		ImageView cancelPopup = (ImageView) popupview.findViewById(R.id.cancelImage);
		TableLayout table = (TableLayout) popupview.findViewById(R.id.tableView_Values);
		TextView a = (TextView) popupview.findViewById(R.id.tv_output);
		TextView b = (TextView) popupview.findViewById(R.id.eingang_tv);
		final TextView simulationOutputTV = (TextView) popupview.findViewById(R.id.output_value);
		
		popupview.setLayoutParams(tlp);
		popupview.setBackgroundColor(Color.WHITE);
		
		//calculate size 
		int cellWidth = (int)(controller.getDisplay_width() / 40), cellHeight = (int)(controller.getDisplay_width() / 40);
		popupview.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);

		int x = (int)((cellWidth * controller.getInputCount()) + a.getMeasuredWidth() + b.getMeasuredWidth())+20;
		int y = (int)(cellHeight*4);
		
		final PopupWindow tablePopup = new PopupWindow(popupview,x,y);
		tablePopup.setOutsideTouchable(false);
		tablePopup.setTouchable(true);
		tablePopup.setBackgroundDrawable(new BitmapDrawable());
		tablePopup.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_gradient));
		popupview.setAlpha(0.75f);
		
		//Rows
		TableRow rowHeader = new TableRow(this);
		TableRow numbersZero = new TableRow(this);
		TableRow numbersOne = new TableRow(this);
		
		//Columns
		for (int j = 0; j <= controller.getInputCount(); j++) {
			TextView cell = new TextView(this);
			cell.setTextSize((int)(18));
			cell.setText(" x" + (j+1) +"");
			rowHeader.addView(cell,cellWidth,cellHeight);
			
			final TextView zero = new TextView(this);
			zero.setTextSize(20);
			zero.setText(" 0 ");
			zero.setTag(j);
			zero.setBackgroundColor(Color.BLUE);
			zero.setTextColor(Color.WHITE);
			numbersZero.addView(zero,cellWidth,cellHeight);
			
			final TextView one = new TextView(this);
			one.setTextSize(20);
			one.setText(" 1 ");
			one.setTag(j);
			numbersOne.addView(one,cellWidth,cellHeight);
			
			
			//OnTouch 
			zero.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					//show active
						zero.setBackgroundColor(Color.BLUE);
						zero.setTextColor(Color.WHITE);
					//edit simulationValue
						StringBuilder sb = new StringBuilder(simulationValue);
						sb.setCharAt((Integer)zero.getTag(), '0');
						simulationValue = sb.toString();
						System.out.println(simulationValue);
					//show opposite as non active
						one.setBackgroundColor(Color.WHITE);
						one.setTextColor(Color.BLACK);
					return false;
				}
			});
			
			one.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					//show active
						one.setBackgroundColor(Color.BLUE);
						one.setTextColor(Color.WHITE);
					//edit simulationValue
						StringBuilder sb = new StringBuilder(simulationValue);
						sb.setCharAt((Integer)one.getTag(), '1');
						simulationValue = sb.toString();
						System.out.println(simulationValue);
					//show opposite as non active
						zero.setBackgroundColor(Color.WHITE);
						zero.setTextColor(Color.BLACK);
					return false;
				}
			});
		}
		
		table.addView(rowHeader);
		table.addView(numbersZero);
		table.addView(numbersOne);
		
		
		//Clock Button
		btnClock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Transition t = stateInSimulation.getTransitionTo(simulationValue);
				if(t != null){
					//unmark last transition
					if(transitionInSimulation != null)
						transitionInSimulation.setInSimulation(false);
					
					//mark valid transition in simulation
					transitionInSimulation = t;
					t.setInSimulation(true);
					
					//unmark last state and mark next
					stateInSimulation.setInSimulation(false);	
					t.getState_to().setInSimulation(true);
					
					//set new state in simulation
					stateInSimulation = t.getState_to();
					
					//set Output
					if(controller.getCurrentType() == fsmType.Mealy){
						simulationOutput += t.getOutputFromValue(simulationValue);
					}else{
						simulationOutput += stateInSimulation.getStateOutput();
					}
					
					simulationOutputTV.setText(simulationOutput);
				}
				drawView.invalidate();
			}
		});
		
		
		//close the popup
		cancelPopup.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(transitionInSimulation != null){
					transitionInSimulation.setInSimulation(false);
					transitionInSimulation = null;
				}
				if(stateInSimulation != null){
					stateInSimulation.setInSimulation(false);
					stateInSimulation = null;
				}
				drawView.invalidate();
				tablePopup.dismiss();
				counter = 0;
				return false;
			}
		});
		
		tablePopup.showAtLocation(popupview, Gravity.BOTTOM | Gravity.LEFT, 0,0);	
	}
		
	public void showTransitionTable() {

		//TODO
		TableLayout layout = new TableLayout(this);
		
		TableLayout.LayoutParams tlp = new TableLayout.LayoutParams(
				TableLayout.LayoutParams.WRAP_CONTENT,
				TableLayout.LayoutParams.WRAP_CONTENT);
		
		TableLayout table = new TableLayout(this);
		
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

		View popupview = getLayoutInflater().inflate(R.layout.table_popup, layout, false);
		ScrollView scroll = (ScrollView) popupview.findViewById(R.id.scrollTable);
		ImageView cancelBtn = (ImageView) popupview.findViewById(R.id.cancelTable);
		scroll.addView(layout);
		popupview.setLayoutParams(tlp);
		popupview.setBackgroundColor(Color.WHITE);
		int x = controller.getDisplay_width() / 2 - 255;
		int y = controller.getDisplay_height() / 2;
		
		final PopupWindow tablePopup = new PopupWindow(popupview, x, y);
		
		tablePopup.setOutsideTouchable(false);
		tablePopup.setTouchable(true);
		tablePopup.setBackgroundDrawable(new BitmapDrawable());
		tablePopup.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_gradient));
		popupview.setAlpha(0.75f);
				
		//close the popup
		cancelBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
	            if(tablePopup.isShowing()) {
	            	tablePopup.dismiss();
	            	counter2 = 0;
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
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Sicher? Alles nicht gespeicherte geht verloren!")
					.setCancelable(false)
					.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Message.message(context, "Neuer Automat!");
							controller.clear();
							drawView.invalidate();
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
				"Zurück zum Auswahl Menu? Alles nicht gespeicherte geht verloren!")
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