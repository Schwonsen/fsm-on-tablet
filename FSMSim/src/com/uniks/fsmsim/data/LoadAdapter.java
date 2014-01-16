package com.uniks.fsmsim.data;

import java.util.ArrayList;

import com.uniks.fsmsim.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LoadAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<String> fileName;
	
	public LoadAdapter(Context c, ArrayList<String> fName) {
		
		this.mContext = c;
		this.fileName = fName;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View child, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder mHolder;
		LayoutInflater layoutInflater;
		if(child == null) {
			layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			child = layoutInflater.inflate(R.layout.filelist, null);
			mHolder = new Holder();
			mHolder.tf_fName = (TextView) child.findViewById(R.id.tf_fName);
		}
		return child;
	}
	
	public class Holder {
		TextView tf_fName;
	}

}
