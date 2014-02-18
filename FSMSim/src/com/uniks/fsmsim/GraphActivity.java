package com.uniks.fsmsim;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
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
	private boolean isUpdate;
	final Context context = this;
	private EditText file;
//	private GestureDetectorCompat mDetector; 


	public GraphActivity() {
		//must have empty constructor
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		mDetector = new GestureDetectorCompat(this, new GestureListener());
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
		

		
		setContentView(new DrawingV2(this,controller));

		
		System.out.println("StartData: " + controller.getInputCount() + " "
				+ controller.getOuputCount() + " "
				+ controller.getCurrentType());
	}
	
//	@Override 
//    public boolean onTouchEvent(MotionEvent event){ 
//        this.mDetector.onTouchEvent(event);
//        return super.onTouchEvent(event);
//    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.graph_menu, menu);
		return true;
	}

	// Methode um Popups anzuzeigen
	private void showPopup() {
		//
		// LayoutInflater inflator = (LayoutInflater) getBaseContext()
		// .getSystemService(LAYOUT_INFLATER_SERVICE);
		//
		// View popupview = inflator.inflate(R.layout.activity_popup_save,
		// null);
		//
		// final PopupWindow savePopup = new PopupWindow(popupview,
		// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// savePopup.showAtLocation(popupview, Gravity.CENTER, 0, 0);

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
		startActivity(new Intent(GraphActivity.this, LoadActivity.class));
	}
	
	public void showTransitionTable() {

		LayoutInflater inflator = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		View popupview = inflator.inflate(R.layout.activity_transition_table, null);

		final PopupWindow tablePopup = new PopupWindow(popupview,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tablePopup.showAtLocation(popupview, Gravity.CENTER, 0, 0);
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
			return true;
		case R.id.item_simulation:
			startActivity(new Intent(GraphActivity.this, TransitionTableActivity.class));
//			showTransitionTable();
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