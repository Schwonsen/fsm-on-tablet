package com.uniks.fsmsim;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.uniks.fsmsim.util.Message;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class GraphActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.graph_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.item_save:
	        	Message.message(this, "Automat wurde gespeichert!");
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
	public void onBackPressed(){
		startActivity(new Intent(GraphActivity.this, MainActivity.class));
		GraphActivity.this.finish();
	}
}
