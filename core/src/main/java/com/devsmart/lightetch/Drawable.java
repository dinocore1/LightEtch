package com.devsmart.lightetch;


import com.devsmart.lightetch.graphics.RectF;

public abstract class Drawable {

    private RectF mBounds = new RectF();

    public void setBounds(RectF bounds) {
        mBounds = new RectF(bounds);
    }

    public RectF getBounds() {
        return mBounds;
    }

    public abstract void draw(Canvas canvas);
}
