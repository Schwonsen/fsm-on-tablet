package com.uniks.fsmsim.data;

import java.util.ArrayList;

import com.uniks.fsmsim.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TransitionListAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<String> id;
	private ArrayList<String> inputT;
	private ArrayList<String> outputT;
	
	public TransitionListAdapter(Context c, ArrayList<String> id, ArrayList<String> input, ArrayList<String> output) {
		this.context = c;
		this.id = id;
		this.inputT = input;
		this.outputT = output;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return id.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int pos, View child, ViewGroup parent) {
		Holder mHolder;
		LayoutInflater inflater;
		if(child == null) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			child = inflater.inflate(R.layout.listcell, null);
			mHolder = new Holder();
			mHolder.txt_id = (TextView) child.findViewById(R.id.txt_id);
			mHolder.txt_input = (TextView) child.findViewById(R.id.txt_input);
			mHolder.txt_output = (TextView) child.findViewById(R.id.txt_output);
			child.setTag(mHolder);
		} else {
			mHolder = (Holder) child.getTag();
		}
		mHolder.txt_id.setText(id.get(pos));
		mHolder.txt_input.setText(inputT.get(pos));
		mHolder.txt_output.setText(outputT.get(pos));
		
		return child;
	}
	
	public class Holder {
		TextView txt_id;
		TextView txt_input;
		TextView txt_output;
	}
	
	

}
