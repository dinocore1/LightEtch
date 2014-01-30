package com.devsmart.lightetch;


public interface Canvas {

    public int getWidth();
    public int getHeight();

    /**
     * Saves the current matrix and clip onto a private stack. Subsequent calls to translate,scale,rotate,skew,concat
     * or clipRect,clipPath will all operate as usual, but when the balancing call to restore() is made, those calls
     * will be forgotten, and the settings that existed before the save() will be reinstated.
     * @return The value to pass to restoreToCount() to balance this save()
     */
    public int save();

    /**
     * This call balances a previous call to save(), and is used to remove all modifications to the matrix/clip state
     * since the last save call. It is an error to call restore() more times than save() was called.
     */
    public void restore();

    /**
     * Efficient way to pop any calls to save() that happened after the save count reached saveCount. It is an error
     * for saveCount to be less than 1. Example: int count = canvas.save(); ... // more calls potentially to save()
     * canvas.restoreToCount(count); // now the canvas is back in the same state it was before the initial // call to
     * save().
     * @param saveCount The save level to restore to.
     */
    public void restoreToCount(int saveCount);

    /**
     * Preconcat the current matrix with the specified rotation.
     * @param degrees
     */
    public void rotate(float degrees);
    public void scale(float sx, float sy);
    public void translate(float dx, float dy);
    public void skew(float sx, float sy);

    public void drawPoint(float x, float y, Paint paint);
    public void drawLine(float startx, float starty, float stopx, float stopy, Paint paint);

}
