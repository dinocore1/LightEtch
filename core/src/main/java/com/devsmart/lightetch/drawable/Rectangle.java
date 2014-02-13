package com.devsmart.lightetch.drawable;

import com.devsmart.lightetch.Canvas;
import com.devsmart.lightetch.Drawable;
import com.devsmart.lightetch.Paint;
import com.devsmart.lightetch.graphics.RectF;


public class Rectangle extends Drawable {

    public int mColor;
    public int width;
    public int height;

    private Paint mPaint;

    private Paint getPaint() {
        if(mPaint == null){
            mPaint = new Paint();
            mPaint.mFillColor = mColor;
            mPaint.mStrokeWidth = 10;
        }
        return mPaint;
    }

    @Override
    public void draw(Canvas canvas) {
        RectF bounds = getBounds();
        canvas.drawRect(0, 0, bounds.right(), bounds.bottom(), getPaint());
    }
}
