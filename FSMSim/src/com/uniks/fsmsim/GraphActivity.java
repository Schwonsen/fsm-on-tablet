package com.uniks.fsmsim;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;

import com.uniks.fsmsim.controller.GraphController;
import com.uniks.fsmsim.controller.MainController.fsmType;
import com.uniks.fsmsim.data.SaveFile;
import com.uniks.fsmsim.data.SaveFile.SaveState;
import com.uniks.fsmsim.data.SaveFile.SaveTransition;
import com.uniks.fsmsim.data.State;
import com.uniks.fsmsim.data.Transition;
import com.uniks.fsmsim.data.TransitionValue;
import com.uniks.fsmsim.util.DrawingV2;
import com.uniks.fsmsim.util.Message;
import com.uniks.fsmsim.util.SimpleVerticalPicker;
import com.uniks.fsmsim.util.Utils;

import android.util.DisplayMetrics;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class GraphActivity extends Activity {
	GraphController controller;
	TextView tV_test;
	final Context context = this;

	PopupWindow popupWinow;
	PopupWindow tablePopup;
	PopupWindow tablePop;
	private int counter;
	private int counter2;
	Bundle mainMenuContent;
	protected boolean isTouched = false;
	DrawingV2 drawView;
	
	float textsize = 20;
	int cellSize = 30;
	
	int bgColor1 = Color.rgb(80, 80, 80);
	int bgColor2 = Color.rgb(100, 100, 100);
	int bgColor3 = Color.rgb(120, 120, 120);
	int bgColor4 = Color.rgb(150, 150, 150);
	int bgColor5 = Color.rgb(200, 200, 200);
	
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
		
		//## TextSize	#
		textsize = Utils.SetTextSize("text", (int) (controller.getDisplay_width() * 0.035), (int) (controller.getDisplay_height() * 0.035));
		
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.graph_menu, menu);
		return true;
	}	

	// Saves FSM popup
	private void showSavePopup() {
		final SaveFile sv = new SaveFile();
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.activity_popup_save);
		dialog.setTitle(R.string.popup_save);
		dialog.setCancelable(false);
		
		final EditText et_saveName = (EditText) dialog.findViewById(R.id.eT_dataname);
		
		Button dialogButton = (Button) dialog.findViewById(R.id.btn_save);
		
		// if button is clicked, make save entry
		dialogButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(et_saveName.getText().toString().length()== 0){
					Message.message(context, getString(R.string.msg_wrong_dataname));
					return;
				}
				if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
					Message.message(context,getString(R.string.msg_no_sdCard));
				}
				
				//add all states to saveFile
				for(State s : controller.getStateList()){
					sv.addState(s.getName(), s.getStateOutput(), s.isStartState(), s.isEndState(), s.getX(), s.getY());
				}
				for(Transition t : controller.getTransitionList()){
					sv.addTransition(t.getState_from().getName(), t.getState_to().getName(), t.getValueList(), t.getDragPoint().x, t.getDragPoint().y );
				}
				
				//save
				if(sv.saveAll(et_saveName.getText().toString(),
						controller.getInputCount(),controller.getOuputCount(),
						(controller.getCurrentType()==fsmType.Mealy)?0:1))
				{
					Message.message(context, "Automat " + et_saveName.getText().toString()
							+ " " + getString(R.string.msg_file_saved));
				}
				else Message.message(context,getString(R.string.msg_error_save));
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
	}

	//Load FSM Popup
	public void showLoadPopup() {
		//list files
		String path = Environment.getExternalStorageDirectory().getPath()+"/fsmSave/";
		File f = new File(path);        
		File file[] = f.listFiles();
		List<String> filenames = new ArrayList<String>();
		
		//check if files available
		if(file == null || file.length == 0){
			Message.message(context, getString(R.string.msg_nofiles_forsave));
			return;
		}
		
		for (int i=0; i < file.length; i++)
		{
			filenames.add(file[i].getName());
		}
		
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.activity_popup_load);
		dialog.setTitle(R.string.popup_load);
		dialog.setCancelable(true);
		
		LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.loadItemLayout);
		final SimpleVerticalPicker svp = new SimpleVerticalPicker(context, filenames, textsize, 600);
		ll.addView(svp);
		
		
		Button loadButton = new Button(context);
		loadButton.setText(R.string.popup_load);
		
		Button deleteButton = new Button(context);
		deleteButton.setText(R.string.delete);
		
		//OnClick
		loadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SaveFile sv = (SaveFile) SaveFile.loadSerializedObject(new File(Environment.getExternalStorageDirectory().getPath()+"/fsmSave/"+svp.getOutput()));
				
				controller.clear();
				controller.setInputCount(sv.getInputCount());
				controller.setOuputCount(sv.getOuputCount());
				controller.setCurrentType((sv.getFsmType()==0)?fsmType.Mealy:fsmType.Moore);
				
				for(SaveState ss : sv.getStates()){
					if(ss != null)
					controller.addState(ss.getName(),ss.getOutput(),ss.isStart(),ss.isEnd(),ss.getPosX(),ss.getPosY());
				}

				for(SaveTransition st : sv.getTransition()){
					if(controller.getStateByName(st.getFrom()) != null && controller.getStateByName(st.getTo()) != null){
						controller.addTransition(
								controller.getStateByName(st.getFrom()),
								controller.getStateByName(st.getTo()),
								st.getValueList(),
								new PointF(st.getDragpoint_x(),st.getDragpoint_y()));
					}
				}
				if(tablePopup != null) {
					controller.removePossibleSimulations();
					tablePopup.dismiss();
					counter = 0;
				}
				if(tablePop != null) {
	            	tablePop.dismiss();
	            	counter2 = 0;
	            }
				drawView.invalidate();
				dialog.dismiss();
			}
		});
		
		//OnClick
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//delete selected
				File file = new File(Environment.getExternalStorageDirectory().getPath()+"/fsmSave/"+svp.getOutput());
				boolean deleted = file.delete();
				
				if(deleted)Message.message(context, getString(R.string.msg_delete));
				else Message.message(context, getString(R.string.msg_delete_canceled));
				
				String path = Environment.getExternalStorageDirectory().getPath()+"/fsmSave/";
				File f = new File(path);        
				File files[] = f.listFiles();
				List<String> filenames = new ArrayList<String>();
				
				//check if files available
				if(files == null || files.length == 0){
					dialog.dismiss();
					return;
				}
				
				for (int i=0; i < files.length; i++)
				{
					filenames.add(files[i].getName());
				}
				svp.createView(filenames);
			}
		});
		
		RelativeLayout ll_2buttons = new RelativeLayout(context);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		
		ll_2buttons.addView(loadButton);
		ll_2buttons.addView(deleteButton, params);
		ll.addView(ll_2buttons, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		dialog.show();
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
		if(controller.getStartState() == null){
			Message.message(context, getString(R.string.msg_set_start_state));
			return;
		}

		//init startState
		controller.getStartState().setInSimulation(true);
		stateInSimulation = controller.getStartState();
		
		if(stateInSimulation.getTransitionTo(simulationValue) != null){
			stateInSimulation.getTransitionTo(simulationValue).setPossibleSimulation(true);
		}
		
		final SimulationPicker simPicker = new SimulationPicker(context, controller.getInputCount(), cellSize, (int)textsize);

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
		table.addView(simPicker);

		TextView b = (TextView) popupview.findViewById(R.id.eingang_tv);
		b.setTextColor(Color.WHITE);
		b.setTextSize(textsize);
		
		popupview.setLayoutParams(tlp);
		popupview.setBackgroundColor(Color.WHITE);
		
		//calculate size 
		popupview.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
		popupview.setBackgroundColor(bgColor3);
		int x = (int)((table.getMeasuredWidth() + b.getMeasuredWidth()+30));
		int y = (int)(table.getMeasuredHeight())+20;

		tablePopup = new PopupWindow(popupview,x,LayoutParams.WRAP_CONTENT);
		tablePopup.setOutsideTouchable(false);
		tablePopup.setTouchable(true);
		tablePopup.setBackgroundDrawable(new BitmapDrawable());
		tablePopup.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_gradient));
		popupview.setAlpha(0.75f);
				
		//Clock Button
		btnClock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Transition t = stateInSimulation.getTransitionTo(simPicker.getValue());
				if(t != null){
					controller.removePossibleSimulations();
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
					
//					//set Output
				if(controller.getCurrentType() == fsmType.Mealy){
					simPicker.setFinalOutput();
				}else{
					simulationOutput = stateInSimulation.getStateOutput();
					simPicker.setFinalOutput(simulationOutput);
				}
					
				}
				
				controller.removePossibleSimulations();
				if(stateInSimulation.getTransitionTo(simPicker.getValue()) != null){
					stateInSimulation.getTransitionTo(simPicker.getValue()).setPossibleSimulation(true);
				}
				if(stateInSimulation.isEndState())
					stateInSimulation.setInSimulationEnd(true);
				else stateInSimulation.setInSimulationEnd(false);
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
				controller.removePossibleSimulations();
				drawView.invalidate();
				tablePopup.dismiss();
				counter = 0;
				return false;
			}
		});
		
		tablePopup.showAtLocation(popupview, Gravity.BOTTOM | Gravity.LEFT, 0,0);	
		drawView.invalidate();
	}
		
	public void showTransitionTable() {
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

		View popupview = getLayoutInflater().inflate(R.layout.table_popup, layout, false);
		ScrollView scroll = (ScrollView) popupview.findViewById(R.id.scrollTable);
		ImageView cancelBtn = (ImageView) popupview.findViewById(R.id.cancelTable);
		scroll.addView(layout);
		popupview.setLayoutParams(tlp);
		popupview.setBackgroundColor(Color.WHITE);
		layout.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
		tablePop = new PopupWindow(popupview, layout.getMeasuredWidth(), LayoutParams.WRAP_CONTENT);
		popupview.setBackgroundColor(bgColor2);
		layout.setBackgroundColor(bgColor5);
		tablePop.setOutsideTouchable(false);
		tablePop.setTouchable(true);
		tablePop.setBackgroundDrawable(new BitmapDrawable());
		tablePop.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_gradient));
		popupview.setAlpha(0.75f);
				
		//close the popup
		cancelBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
	            if(tablePop.isShowing()) {
	            	tablePop.dismiss();
	            	counter2 = 0;
	            }
				return true;
			}
		});

		popupview.getLayoutParams().width = 220;layout.requestLayout();
		tablePop.showAtLocation(popupview, Gravity.BOTTOM | Gravity.RIGHT, 0,0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.item_save:
			showSavePopup();
			return true;
		
		case R.id.item_load:
			showLoadPopup();
			return true;
			
		case R.id.item_grit:
			 drawView.setDrawLines(!drawView.isDrawLines());
			 drawView.invalidate();
			return true;
			
		case R.id.item_new:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					getString(R.string.msg_warning_newtable))
					.setCancelable(false)
					.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Message.message(context, getString(R.string.msg_new_machine));
							controller.clear();
							if(tablePopup != null) {
								controller.removePossibleSimulations();
								tablePopup.dismiss();
								counter = 0;
							}
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
				getString(R.string.msg_back_to_main))
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
	
	public class SimulationPicker extends LinearLayout{
		TableLayout tl_picTable;
		LinearLayout ll_picAndOutput;
		TextView tv_output;
		TextView tv_finalOutput;
		
		int count;
		String binCode = "";
		String finaleOutput = "0";
		Context context;
		int cellWidth = 30;
		int cellHeight = 30;
		int textSize = 20;

		
	    TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(
	            TableLayout.LayoutParams.WRAP_CONTENT,
	            TableLayout.LayoutParams.WRAP_CONTENT);
		
	    
		public SimulationPicker(Context context, int count, int cellSize, int textSise) {
			super(context);
			this.cellHeight = this.cellWidth = cellSize;
			this.textSize = textSise;
			this.context = context;
			tv_output = new TextView(context);
			tv_finalOutput = new TextView(context);
			ll_picAndOutput = new LinearLayout(context);
			ll_picAndOutput.setOrientation(LinearLayout.VERTICAL);
			this.count = count;
			
			for(int i = 0; i < count;i++){
				binCode += "0";
			}
			
			this.tl_picTable = getPicTable();
			ll_picAndOutput.addView(tl_picTable);
			tl_picTable.setPadding(0, 0, 10, 0);
			
			TextView pad1 = new TextView(context);
			pad1.setBackgroundColor(bgColor3);
			this.addView(pad1,cellWidth/2,cellHeight*3);
			
			tv_output.setTextSize(textSize);
			tv_output.setText(binCode);
			tv_output.setBackgroundColor(bgColor2);
			tv_output.setTextColor(Color.BLUE);
			tv_output.setGravity(Gravity.CENTER);
			ll_picAndOutput.addView(tv_output);
			this.addView(ll_picAndOutput);
			
			tv_finalOutput.setTextSize(textSize);
			tv_finalOutput.setText("\nAusgabe:\n"+finaleOutput);
			tv_finalOutput.setBackgroundColor(bgColor3);
			tv_finalOutput.setTextColor(Color.WHITE);
			tv_finalOutput.setGravity(Gravity.CENTER);
			tv_finalOutput.setPadding(0, 0, 10, 0);
			this.addView(tv_finalOutput,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			setFinalOutput();
		}

		
		public TableLayout getPicTable(){
			TableLayout picTable = new TableLayout(context);
			//Rows
			TableRow rowDesc = new TableRow(context);
			TableRow rowZero = new TableRow(context);
			TableRow rowOne = new TableRow(context);
			
			for (int j = 0; j < count; j++) {		
				final TextView cellDesc = new TextView(context);
				cellDesc.setTextSize(textSize);
				cellDesc.setText("x"+j);
				cellDesc.setBackgroundColor(bgColor2);
				cellDesc.setTextColor(Color.WHITE);
				cellDesc.setGravity(Gravity.CENTER);
				rowDesc.addView(cellDesc,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				
				final TextView cellZero = new TextView(context);
				cellZero.setTextSize(textSize);
				cellZero.setText(" 0 ");
				cellZero.setTag(j);
				cellZero.setBackgroundColor(Color.BLUE);
				cellZero.setTextColor(Color.WHITE);
				cellZero.setGravity(Gravity.CENTER);
				rowZero.addView(cellZero,textSize,LayoutParams.WRAP_CONTENT);
				
				final TextView cellOne = new TextView(context);
				cellOne.setBackgroundColor(bgColor1);
				cellOne.setTextColor(Color.WHITE);
				cellOne.setTextSize(textSize);
				cellOne.setText(" 1 ");
				cellOne.setTag(j);
				cellOne.setGravity(Gravity.CENTER);
				rowOne.addView(cellOne,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				
				//OnTouch 
				cellZero.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View arg0, MotionEvent arg1) {
						//show active
							cellZero.setBackgroundColor(Color.BLUE);
						//edit simulationValue
							StringBuilder sb = new StringBuilder(binCode);
							sb.setCharAt((Integer)cellZero.getTag(), '0');
							binCode = sb.toString();
						//show opposite as non active
							cellOne.setBackgroundColor(bgColor1);
							tv_output.setText(binCode);
							
							controller.removePossibleSimulations();
							if(stateInSimulation.getTransitionTo(binCode) != null){
								stateInSimulation.getTransitionTo(binCode).setPossibleSimulation(true);
							}
							if(stateInSimulation.isEndState())
								stateInSimulation.setInSimulationEnd(true);
							else stateInSimulation.setInSimulationEnd(false);
							
							//show output
							if(controller.getCurrentType() == fsmType.Mealy){
								setFinalOutput();
							}
							drawView.invalidate();
						return false;
					}
				});
				
				cellOne.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						//show active
							cellOne.setBackgroundColor(Color.BLUE);
						//edit simulationValue
							StringBuilder sb = new StringBuilder(binCode);
							sb.setCharAt((Integer)cellOne.getTag(), '1');
							binCode = sb.toString();
						//show opposite as non active
							cellZero.setBackgroundColor(bgColor1);
							tv_output.setText(binCode);
							
							controller.removePossibleSimulations();
							if(stateInSimulation.getTransitionTo(binCode) != null){
								stateInSimulation.getTransitionTo(binCode).setPossibleSimulation(true);
							}
							if(stateInSimulation.isEndState())
								stateInSimulation.setInSimulationEnd(true);
							else stateInSimulation.setInSimulationEnd(false);
							
							//show output
							if(controller.getCurrentType() == fsmType.Mealy){
								setFinalOutput();
							}	
							drawView.invalidate();
							
						return false;
					}
				});
			}
			picTable.addView(rowDesc);
			picTable.addView(rowZero);
			picTable.addView(rowOne);
			picTable.setLayoutParams(trParams);			
			return picTable;
		}
		
		public String getValue(){
			return binCode;
		}
		public void setFinalOutput(String finalOutput){
			this.finaleOutput = finalOutput;
			tv_finalOutput.setText("\nAusgabe:\n"+finalOutput);
		}
		public void setFinalOutput(){
			if(stateInSimulation.getTransitionTo(binCode) != null){
				setFinalOutput(stateInSimulation.getTransitionTo(binCode).getOutputFromValue(binCode));
			}else{
				setFinalOutput("x");
			}
		}
	}
}