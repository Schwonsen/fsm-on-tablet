package com.uniks.fsmsim;

import android.annotation.SuppressLint;
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
import com.uniks.fsmsim.util.Drawing;
import com.uniks.fsmsim.util.DrawingV2;
import com.uniks.fsmsim.util.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
	
	public GraphActivity() {
		//must have empty constructor
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_graph);
		Bundle b = getIntent().getExtras();
		controller = new GraphController(fsmType.getEnumByValue(b
				.getInt("fsmType")), b.getInt("inputCount"),
				b.getInt("outputCount"));
		
		//##	Windowsize	##
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		controller.setDisplay_height(displaymetrics.heightPixels);
		controller.setDisplay_width(displaymetrics.widthPixels);
		
		//##	Top BarSize	##
//	    int barSize = 0;
//	    switch (displaymetrics.densityDpi) {
//	        case DisplayMetrics.DENSITY_HIGH:
//	            System.out.println("display high");
//	            barSize = 48;
//	            break;
//	        case DisplayMetrics.DENSITY_MEDIUM:
//	        	System.out.println("display medium/default");
//	            barSize = 32;
//	            break;
//	        case DisplayMetrics.DENSITY_LOW:
//	        	System.out.println("display low");
//	            barSize = 24;
//	            break;
//	        default:
//	        	System.out.println("display Unknown density");
//	    }
//	    controller.setDisplay_barSize(barSize);
	    
	    Resources resources = context.getResources();
	    int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
	    if (resourceId > 0) {
	    	controller.setDisplay_TopBarSize(resources.getDimensionPixelSize(resourceId));
	    }
	    

	    resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
	    if (resourceId > 0) {
	        controller.setDisplay_BotBarSize(resources.getDimensionPixelSize(resourceId));
	    }
		

		
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

		
		popupview.setLayoutParams(tlp);
		popupview.setBackgroundColor(Color.WHITE);
		
		final PopupWindow tablePopup = new PopupWindow(popupview,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		tablePopup.setOutsideTouchable(false);
		tablePopup.setTouchable(true);
		tablePopup.setBackgroundDrawable(new BitmapDrawable());
		popupview.setAlpha(0.75f);

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
//	            // TODO Auto-generated method stub
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

		for (int i = 0; i < controller.getStateList().size(); i++) {

			TableRow row = new TableRow(this);

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
				if (i == 0 && j == 0) {
					cell.setText("Eingabe");
				} else if (i == 0 && j == 1) {
					cell.setText("  Zt  ");
				} else if (i == 0 && j == 2) {
					cell.setText("Zt+r");
				} else if (i == 0 && j == 3) {
					cell.setText("Ausgabe");
				}

				for(State s : controller.getStateList()) {
					for(Transition t : controller.getTransitionList()) {
						// Eingabe
						if (i != 0 && j == 0)
							cell.setText("0\n1");
//							cell.setText()
						// Zustände
						// TODO Namen der states_from
						if (i != 0 && j != 0 && j != 3)
							cell.setText(controller.getStatenames().get(i) + "\n" + controller.getStatenames().get(i));
						// TODO Name der states_to
						if (i != 0 && j == 2)
							cell.setText("s1\ns2");
						// Ausgabe
						// TODO werter der Ausgabe
						if (i != 0 && j == 3)
							cell.setText("1\n1");
						}
				}
				cell.setPadding(6, 4, 6, 4);
				row.addView(cell);
			}
			table.addView(row);
		}
		layout.addView(table); 
		
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
//		    startActivity(new Intent(GraphActivity.this, TransitionPopUp.class));
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