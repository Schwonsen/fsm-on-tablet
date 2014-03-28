package com.uniks.fsmsim.util;

import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;

public class Utils {
	public static float SetTextSize(String text, int width, int height)
	{
	    Paint paint = new Paint();
	    float textWidth = paint.measureText(text);
	    float textSize = (int) ((width / textWidth) * paint.getTextSize());
	    paint.setTextSize(textSize);

	    textWidth = paint.measureText(text);
	    textSize = (int) ((width / textWidth) * paint.getTextSize());

	    // Re-measure with font size near our desired result
	    paint.setTextSize(textSize);

	    // Check height constraints
	    FontMetricsInt metrics = paint.getFontMetricsInt();
	    float textHeight = metrics.descent - metrics.ascent;
	    if (textHeight > height)
	    {
	        textSize = (int) (textSize * (height / textHeight));
	        paint.setTextSize(textSize);
	    }
	    return textSize;
	}
}
