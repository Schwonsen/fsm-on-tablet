//package com.uniks.fsmsim;
//
//import java.util.ArrayList;
//import com.uniks.fsmsim.data.DbHelper;
//import com.uniks.fsmsim.data.LoadAdapter;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.Toast;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.AdapterView.OnItemLongClickListener;
//import android.widget.ListView;
//
//public class LoadActivity extends Activity {
//
//	private DbHelper mHelper;
//	private SQLiteDatabase dataBase;
//	private AlertDialog.Builder build;
//
//	private ArrayList<String> file_fName = new ArrayList<String>();
//
//	private ListView fileList;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_load);
//
//		fileList = (ListView) findViewById(R.id.list);
//
//		mHelper = new DbHelper(this);
//
//		// click to load data
//
//		fileList.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//
//		// long click to delete data
//		fileList.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//					final int arg2, long arg3) {
//
//				build = new AlertDialog.Builder(LoadActivity.this);
//				build.setTitle("Delete " + file_fName.get(arg2));
//				build.setMessage("Do you want to delete ?");
//				build.setPositiveButton("Yes",
//						new DialogInterface.OnClickListener() {
//
//							public void onClick(DialogInterface dialog,
//									int which) {
//
//								Toast.makeText(
//										getApplicationContext(),
//										file_fName.get(arg2) + " "
//												+ " is deleted.", 3000).show();
//
//								dataBase.delete(
//										DbHelper.TABLE_NAME, null, null);
//								displayData();
//								dialog.cancel();
//							}
//						});
//
//				build.setNegativeButton("No",
//						new DialogInterface.OnClickListener() {
//
//							public void onClick(DialogInterface dialog,
//									int which) {
//								dialog.cancel();
//							}
//						});
//				AlertDialog alert = build.create();
//				alert.show();
//
//				return true;
//			}
//		});
//
//	}
//	
//	@Override
//	protected void onResume() {
//		displayData();
//		super.onResume();
//	}
//	
//	private void displayData() {
//		dataBase = mHelper.getWritableDatabase();
//		Cursor mCursor = dataBase.rawQuery("SELECT * FROM "
//				+ DbHelper.TABLE_NAME, null);
//		file_fName.clear();
//		if (mCursor.moveToFirst()) {
//			do {
//				file_fName.add(mCursor.getString(mCursor.getColumnIndex(DbHelper.KEY_FNAME)));
//
//			} while (mCursor.moveToNext());
//		}
//		LoadAdapter disadpt = new LoadAdapter(LoadActivity.this,file_fName);
//		fileList.setAdapter(disadpt);
//		mCursor.close();
//	}
//}
