package com.devsmart.lightetch.graphics;


public class RectF {

    private float mLeft;
    private float mTop;
    private float mRight;
    private float mBottom;

    public RectF() {}

    public RectF(RectF copy) {
        mLeft = copy.mLeft;
        mTop = copy.mTop;
        mRight = copy.mRight;
        mBottom = copy.mBottom;
    }

    public RectF(float left, float top, float right, float bottom) {
        set(left, top, right, bottom);
    }

    public float left() {
        return mLeft;
    }

    public float right() {
        return mRight;
    }

    public float top() {
        return mTop;
    }

    public float bottom() {
        return mBottom;
    }

    public float width() {
        return mRight - mLeft;
    }

    public float height() {
        return mBottom - mTop;
    }

    public void set(float left, float top, float right, float bottom) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;

        if(right < left) {
            throw new RuntimeException("cannot have negative width");
        }
        if(bottom < top) {
            throw new RuntimeException("cannot have negative height");
        }
    }

    public void offset(float dx, float dy) {
        mRight += dx;
        mLeft += dx;
        mTop += dy;
        mBottom += dy;
    }

    public void setWidth(float width) {
        mRight = mLeft + width;
    }

    public void setHeight(float height) {
        mBottom = mTop + height;
    }

}
