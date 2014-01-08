package com.uniks.fsmsim;

import com.uniks.fsmsim.R;
import com.uniks.fsmsim.controller.MainController;

import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends Activity implements NumberPicker.OnValueChangeListener{

	private MainController controller;
	RadioButton rB_Mealy;
	RadioButton rB_Moore;

	NumberPicker nP_Input;
	NumberPicker nP_Output;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		controller = new MainController(); 
		initViewData();
	}
	
	private void initViewData()
	{

		rB_Mealy = (RadioButton)findViewById(R.id.rB_Mealy);
		rB_Moore = (RadioButton)findViewById(R.id.rB_Moore);

		nP_Input = (NumberPicker)findViewById(R.id.nP_Input);
		nP_Output = (NumberPicker)findViewById(R.id.nP_Output);
		
		nP_Input.setMaxValue(4);
        nP_Input.setMinValue(0);
        nP_Input.setValue(0);
        nP_Input.setOnValueChangedListener(this);
        
        nP_Output.setMaxValue(4);
        nP_Output.setMinValue(0);
        nP_Output.setValue(0);
        nP_Output.setOnValueChangedListener(this);
		
		rB_Mealy.setChecked(true);
	}
	
	public void rB_Mealy_OnClick(View view){
		if(rB_Mealy.isChecked())
			rB_Moore.setChecked(false);
	}
	public void rB_Moore_OnClick(View view){
		if(rB_Moore.isChecked())
			rB_Mealy.setChecked(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void btnOnClickStart(View view) {

		startActivity(new Intent(MainActivity.this, GraphActivity.class));
		MainActivity.this.finish();
	}
	
	@Override
	public void onBackPressed(){
		MainActivity.this.finish();
	}
	
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Toast.makeText(this, "change"+ picker.getValue(), Toast.LENGTH_SHORT).show();

    }

}
