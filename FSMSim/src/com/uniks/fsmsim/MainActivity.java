package com.uniks.fsmsim;

import com.uniks.fsmsim.R;
import com.uniks.fsmsim.controller.MainController;
import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.util.Message;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.RadioButton;

public class MainActivity extends Activity implements NumberPicker.OnValueChangeListener{
	/*
	 * System out 
	 * Error - !Error: [class] [description]
	 */

	private MainController controller;
	RadioButton rB_Mealy;
	RadioButton rB_Moore;

	NumberPicker nP_Input;
	NumberPicker nP_Output;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Wake up Device on debug
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		
		//init controller
		controller = new MainController(); 
		controller.setCurrentType(fsmType.Mealy);
		controller.setInputCount(0);
		controller.setOuputCount(0);
		initViewData();
	}
	
	private void initViewData()
	{

		rB_Mealy = (RadioButton)findViewById(R.id.rB_Mealy);
		rB_Moore = (RadioButton)findViewById(R.id.rB_Moore);

		nP_Input = (NumberPicker)findViewById(R.id.nP_Input);
		nP_Output = (NumberPicker)findViewById(R.id.nP_Output);
		
		// maximalwerte für Ein- und Ausgänge
		nP_Input.setMaxValue(8);
        nP_Input.setMinValue(1);
        nP_Input.setValue(1);
        nP_Input.setOnValueChangedListener(this);
        controller.setInputCount(1);
        
        nP_Output.setMaxValue(8);
        nP_Output.setMinValue(1);
        nP_Output.setValue(1);
        nP_Output.setOnValueChangedListener(this);
        controller.setOuputCount(1);
		
		rB_Mealy.setChecked(true);
	}

	//Adjust values
	public void rB_Mealy_OnClick(View view){
		if(rB_Mealy.isChecked()){
			rB_Moore.setChecked(false);
			controller.setCurrentType(fsmType.Mealy);
		}
	}
	public void rB_Moore_OnClick(View view){
		if(rB_Moore.isChecked()){
			rB_Mealy.setChecked(false);
			controller.setCurrentType(fsmType.Moore);
		}
	}
	
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
    	if(picker == nP_Input){
    		controller.setInputCount(picker.getValue());
    	}
    	else if (picker == nP_Output){
    		controller.setOuputCount(picker.getValue());
    	}
    }
    
    private void showAboutPopup() {
    	final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.activity_about);
		dialog.setCancelable(true);
		dialog.show();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.item_about:
			showAboutPopup();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void btnOnClickStart(View view) {
		Intent intent = new Intent(MainActivity.this, GraphActivity.class);
		
		Bundle b = new Bundle();
		b.putInt("inputCount", controller.getInputCount());
		b.putInt("outputCount", controller.getOuputCount());
		b.putInt("fsmType", controller.getCurrentType().getValue());
		
		intent.putExtras(b); 
		startActivity(intent);
		this.finish();
	}
	
	@Override
	public void onBackPressed(){
		MainActivity.this.finish();
	}
	


}
