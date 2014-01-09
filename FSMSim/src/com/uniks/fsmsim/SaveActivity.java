package com.uniks.fsmsim;

import com.uniks.fsmsim.util.Message;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SaveActivity extends Activity {

	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_popup_save);
	 // Verhindert das schlieﬂen des Popups wenn man ausserhalb der Dialog
	 // Box klickt
//	 this.setFinishOnTouchOutside(false);
	
	 }
	
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	 // Inflate the menu; this adds items to the action bar if it is present.
	 getMenuInflater().inflate(R.menu.main, menu);
	 return true;
	 }
	
//	 public void btnOnClickSave(View view) {
//	 Message.message(this, "Automat wurde gespeichert!");
//	 }
//	
//	 public void btnOnClickCancel(DialogInterface dialog, int id) {
//	 dialog.dismiss();
//	
//	 }
//
//	@Override
//	public void onBackPressed() {
//		SaveActivity.this.finish();
//	}
}
