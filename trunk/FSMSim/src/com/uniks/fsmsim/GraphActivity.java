package com.uniks.fsmsim;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Debug;

import com.uniks.fsmsim.controller.GraphController;
import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.data.DbHelper;
import com.uniks.fsmsim.util.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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

	public GraphActivity() {

	}

	private void initViewData() {
		tV_test = (TextView) findViewById(R.id.textView1);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);

		Bundle b = getIntent().getExtras();
		controller = new GraphController(fsmType.getEnumByValue(b
				.getInt("fsmType")), b.getInt("inputCount"),
				b.getInt("outputCount"));

		initViewData();
		tV_test.setText("StartData: " + controller.getInputCount() + " "
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

		btn_save = (Button) dialog.findViewById(R.id.btn_save);
		btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
		fileName = (EditText) findViewById(R.id.eT_dataname);

		isUpdate = getIntent().getExtras().getBoolean("update");
		if (isUpdate) {
			fName = getIntent().getExtras().getString("FName");
			fileName.setText(fName);
		}

		mHelper = new DbHelper(this);

		// if button is clicked, make save entry
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fName = fileName.getText().toString().trim();
				if (fName.length() > 0) {
					saveData();
				} else {
					Message.message(context,
							"Datei Name bereits vergeben oder falsches Eingabe Format");
				}
				Message.message(context, "Automat " + file
						+ " wurde gespeichert!");
				dialog.dismiss();
			}
		});

		// if button is clicked, close the custom dialog
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();

		// startActivity(new Intent(GraphActivity.this, SaveActivity.class));
	}
	
	//saves Data into SQL

	private void saveData() {
		dataBase = mHelper.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(DbHelper.KEY_FNAME, fName);

		if (isUpdate) {
			dataBase.insert(DbHelper.TABLE_NAME, fName, values);
		}

		dataBase.close();
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.item_save:
			showPopup();
			return true;
		case R.id.item_load:
			Message.message(this, "Automat wurde geladen!");
			return true;
		case R.id.item_new:
			startActivity(new Intent(GraphActivity.this, GraphActivity.class));
			GraphActivity.this.finish();
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
