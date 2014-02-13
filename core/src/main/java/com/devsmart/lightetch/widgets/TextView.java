package com.devsmart.lightetch.widgets;


import com.devsmart.lightetch.*;
import com.devsmart.lightetch.graphics.RectF;

import java.util.ArrayList;

public class TextView extends View {

    public String text = "";
    public Paint paint = new Paint();

    public TextView(Context context) {
        super(context);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        RectF bounds = new RectF();

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int marginHeight = MeasureSpec.getSize(heightMeasureSpec);
        if(mLayoutParams instanceof ViewGroup.MarginLayoutParams){
            width -= ((ViewGroup.MarginLayoutParams)mLayoutParams).marginLeft;
            width -= ((ViewGroup.MarginLayoutParams)mLayoutParams).marginRight;
            marginHeight += ((ViewGroup.MarginLayoutParams)mLayoutParams).marginTop;
            marginHeight += ((ViewGroup.MarginLayoutParams)mLayoutParams).marginBottom;
        }


        float measureWidth = 0;
        float measuredHeight = 0;
        ArrayList<String> lines = new ArrayList<String>();


        switch(MeasureSpec.getMode(widthMeasureSpec)){
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                measureWidth = splitText(text, width, lines);
                break;
        }

        mContext.getStringBounds(text, bounds, paint);
        measuredHeight = bounds.height() * lines.size() + marginHeight;

        setMeasuredDimension((int) Math.ceil(measureWidth), (int) Math.ceil(measuredHeight));

    }

    private float splitText(String text, final int maxWidth, final ArrayList<String> lines) {
        float maxMeasured = Float.MIN_VALUE;
        StringBuilder buf = new StringBuilder();
        RectF bounds = new RectF();
        text = text.trim();

        while(text.length() > 0) {
            mContext.getStringBounds(text, bounds, paint);
            while(bounds.width() > maxWidth){
                int splitidx = text.lastIndexOf(' ');
                buf.insert(0, text.substring(splitidx));
                text = text.substring(0, splitidx).trim();
                mContext.getStringBounds(text, bounds, paint);
            }
            lines.add(text);
            text = buf.toString().trim();
            buf = new StringBuilder();
            maxMeasured = Math.max(maxMeasured, bounds.width());
        }

        return maxMeasured;
    }

    @Override
    public void draw(Canvas canvas) {

        ArrayList<String> lines = new ArrayList<String>();
        splitText(text, getMeasuredWidth(), lines);

        RectF bounds = new RectF();
        mContext.getStringBounds(text, bounds, paint);
        final float height = bounds.height();

        float y = 0;

        for(String line : lines) {
            canvas.drawText(line, 0, (int) y, paint);
            y += height;
        }
    }
}
