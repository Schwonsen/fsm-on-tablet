package com.uniks.fsmsim;

import java.util.ArrayList;

import com.uniks.fsmsim.controller.GraphController;
import com.uniks.fsmsim.controller.MainController;
import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.data.DbHelper;
import com.uniks.fsmsim.data.TransitionListAdapter;
import com.uniks.fsmsim.util.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class TransitionPopUp extends Activity implements
		android.view.View.OnClickListener {

	private ImageButton btn_add;
	private EditText edit_input, edit_output;
	private DbHelper mHelper;
	private SQLiteDatabase dataBase;
	private String id, input, output;
	private boolean isUpdate;

	private ArrayList<String> transi_id = new ArrayList<String>();
	private ArrayList<String> transi_input = new ArrayList<String>();
	private ArrayList<String> transi_output = new ArrayList<String>();
	private ListView transiList;
	private AlertDialog.Builder build;
	private GraphController graphController;
	private int selectedStateIndex;
	private int touchedStateIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transition_popup);

		btn_add = (ImageButton) findViewById(R.id.add_btn);
		edit_input = (EditText) findViewById(R.id.input_txt);
		edit_output = (EditText) findViewById(R.id.output_txt);
		transiList = (ListView) findViewById(R.id.transationListView);

		if (isUpdate) {
			edit_input.setText(input);
			edit_output.setText(output);
		}
		mHelper = new DbHelper(this);

		btn_add.setOnClickListener(this);

		// update
		transiList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				edit_input.setText(transi_input.get(arg2));
				edit_output.setText(transi_output.get(arg2));
				transi_id.get(arg2);

				isUpdate = true;
			}
		});
		// long click to delete data
		transiList.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {

				build = new AlertDialog.Builder(TransitionPopUp.this);
				build.setTitle("Delete " + transi_input.get(arg2) + " "
						+ transi_output.get(arg2));
				build.setMessage("Do you want to delete ?");
				build.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

								Toast.makeText(
										getApplicationContext(),
										transi_input.get(arg2) + " "
												+ transi_output.get(arg2)
												+ " is deleted.", 3000).show();

								dataBase.delete(
										DbHelper.TABLE_NAME,
										DbHelper.KEY_ID + "="
												+ transi_id.get(arg2), null);
								displayData();
								dialog.cancel();
							}
						});

				build.setNegativeButton("No",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				AlertDialog alert = build.create();
				alert.show();

				return true;
			}
		});
	}

	@Override
	public void onClick(View v) {
		input = edit_input.getText().toString().trim();
		output = edit_output.getText().toString().trim();
		
		//TODO bug eddy schau mal drüber
//		if (graphController.getCurrentType() == fsmType.Moore) {
//			if (!edit_input.getText().toString().equals("")) {
//				graphController.addTransition(graphController.getStateList().get(selectedStateIndex),
//				graphController.getStateList().get(touchedStateIndex), edit_input.getText().toString(), null);
//				saveData();
//			} else {
//				AlertDialog.Builder builder = new AlertDialog.Builder(TransitionPopUp.this);
//				builder.setMessage("Transition braucht einen Wert bei Eingang!")
//						.setCancelable(false)
//						.setPositiveButton("Ok",
//								new DialogInterface.OnClickListener() {
//									public void onClick(
//										DialogInterface dialog,int id) {
//										dialog.cancel();
//									}
//								});
//				AlertDialog alert = builder.create();
//				alert.show();
//			}
//
//		} else if (graphController.getCurrentType() == fsmType.Mealy) {
//			if (!edit_input.getText().toString().equals("") && !edit_output.getText().toString().equals("")) {
//				// # create new Transition #
//				graphController.addTransition(graphController.getStateList().get(selectedStateIndex),
//				graphController.getStateList().get(touchedStateIndex), edit_input.getText().toString(), 
//								edit_output.getText().toString());
//				graphController.deSelectAll();
//				saveData();
//			} else {
//				AlertDialog.Builder builder = new AlertDialog.Builder(TransitionPopUp.this);
//				builder.setMessage("Transition braucht einen Wert bei Eingang und Ausgabe!")
//						.setCancelable(false)
//						.setPositiveButton("Ok",
//								new DialogInterface.OnClickListener() {
//									public void onClick(
//										DialogInterface dialog,int id) {
//										dialog.cancel();
//									}
//								});
//				AlertDialog alert = builder.create();
//				alert.show();
//			}
//		}
		
		
		
		
		
		
		
		
		
		
		
		if (input.length() > 0 && output.length() > 0) {
			isUpdate = false;
			saveData();
		} else {

			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
					TransitionPopUp.this);
			alertBuilder.setTitle("Invalid Data");
			alertBuilder.setMessage("Please, Enter valid data");
			alertBuilder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();

						}
					});
			alertBuilder.create().show();
		}
	}

	private void saveData() {
		dataBase = mHelper.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(DbHelper.KEY_INPUT, input);
		values.put(DbHelper.KEY_OUTPUT, output);

		System.out.println("");
		if (isUpdate) {
			dataBase.update(DbHelper.TABLE_NAME, values, DbHelper.KEY_ID + "="
					+ id, null);
		} else {
			dataBase.insert(DbHelper.TABLE_NAME, null, values);
		}
		dataBase.close();
		finish();
	}

	private void displayData() {
		dataBase = mHelper.getWritableDatabase();
		Cursor mCursor = dataBase.rawQuery("SELECT * FROM "
				+ DbHelper.TABLE_NAME, null);

		transi_id.clear();
		transi_input.clear();
		transi_output.clear();
		if (mCursor.moveToFirst()) {
			do {
				transi_id.add(mCursor.getString(mCursor
						.getColumnIndex(DbHelper.KEY_ID)));
				transi_input.add(mCursor.getString(mCursor
						.getColumnIndex(DbHelper.KEY_INPUT)));
				transi_output.add(mCursor.getString(mCursor
						.getColumnIndex(DbHelper.KEY_OUTPUT)));

			} while (mCursor.moveToNext());
		}
		TransitionListAdapter transiadpt = new TransitionListAdapter(
				TransitionPopUp.this, transi_id, transi_input, transi_output);
		transiList.setAdapter(transiadpt);
		mCursor.close();
	}

	@Override
	protected void onResume() {
		displayData();
		super.onResume();
	}
}
