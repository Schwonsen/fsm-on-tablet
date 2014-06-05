package com.uniks.fsmsim.util;

import android.content.Context;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Message {
	
	public static void message(Context context, String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static void message(Context context, int i) {
		// TODO Auto-generated method stub
		Toast.makeText(context, i, Toast.LENGTH_LONG).show();
	}

}
