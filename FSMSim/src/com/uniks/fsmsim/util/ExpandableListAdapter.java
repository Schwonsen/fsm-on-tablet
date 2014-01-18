package com.uniks.fsmsim.util;

import java.util.List;
import java.util.Map;

import com.uniks.fsmsim.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Activity context;
	private List<String> output;
	private Map<String, List<String>> outputName ;
	
	public ExpandableListAdapter(Activity context, List<String> outputs,
			Map<String, List<String>> outputName) {
		this.context = context;
		this.outputName = outputName;
		this.output = output;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return outputName.get(output.get(groupPosition)).get(childPosition);
	
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		final String output = (String) getChild(groupPosition, childPosition);
		LayoutInflater inflater = context.getLayoutInflater();
		
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.child_item, null);
		}
		
		TextView item = (TextView) convertView.findViewById(R.id.output);
		
		item.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				//TODO HIer etwas tun
			}
		});
		
		item.setText(output);
		return convertView;
	}

	@Override
	public int getChildrenCount(int arg0) {
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String laptoName = (String) getGroup(groupPosition);
		
		if(convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			convertView = infalInflater.inflate(R.layout., root)
		}
		
		return null;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

}
