package com.devsmart.lightetch;


import java.util.Collections;

public class View implements Drawable{

    public static class MeasureSpec {

        private static final int MODE_SHIFT = 30;
        private static final int MODE_MASK = 0x3 << MODE_SHIFT;

        /**
         * Measure specification mode: The parent has not imposed any constraint
         * on the child. It can be whatever size it wants.
         */
        public static final int UNSPECIFIED = 0 << MODE_SHIFT;

        /**
         * Measure specification mode: The parent has determined an exact size
         * for the child. The child is going to be given those bounds regardless
         * of how big it wants to be.
         */
        public static final int EXACTLY = 1 << MODE_SHIFT;

        /**
         * Measure specification mode: The child can be as large as it wants up
         * to the specified size.
         */
        public static final int AT_MOST = 2 << MODE_SHIFT;

        public static int makeMeasureSpec(int size, int mode) {
            return (size & ~MODE_MASK) | (mode & MODE_MASK);
        }

        /**
         * Extracts the mode from the supplied measure specification.
         *
         * @param measureSpec the measure specification to extract the mode from
         * @return {@link View.MeasureSpec#UNSPECIFIED},
         *         {@link View.MeasureSpec#AT_MOST} or
         *         {@link View.MeasureSpec#EXACTLY}
         */
        public static int getMode(int measureSpec) {
            return (measureSpec & MODE_MASK);
        }

        /**
         * Extracts the size from the supplied measure specification.
         *
         * @param measureSpec the measure specification to extract the size from
         * @return the size in pixels defined in the supplied measure specification
         */
        public static int getSize(int measureSpec) {
            return (measureSpec & ~MODE_MASK);
        }

    }

    private static final int PFLAG_MEASURED_DIMENSION_SET = 1;
    private int mPrivateFlags;

    protected ViewGroup.LayoutParams mLayoutParams;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    int mLeft;
    int mTop;
    int mRight;
    int mBottom;

    public void setLayoutParams(ViewGroup.LayoutParams params) {
        mLayoutParams = params;
    }

    public ViewGroup.LayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    public final void measure(int widthMeasureSpec, int heightMeasureSpec) {
        mPrivateFlags &= ~PFLAG_MEASURED_DIMENSION_SET;
        onMeasure(widthMeasureSpec, heightMeasureSpec);
        if((mPrivateFlags & PFLAG_MEASURED_DIMENSION_SET) != PFLAG_MEASURED_DIMENSION_SET){
            throw new IllegalStateException("onMeasure did not set the measured dimension by calling setMeasuredDimension()");
        }
    }

    /**
     * Measure the view and its contents.
     * <p>
     * <strong>CONTRACT:</strong> When overriding this method, you must call
     * {@link #setMeasuredDimension(int, int)} to store the measured view size.
     * </p>
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    public void setMeasuredDimension(int width, int height) {
        mMeasuredWidth = width;
        mMeasuredHeight = height;
        mPrivateFlags |=  PFLAG_MEASURED_DIMENSION_SET;
    }

    public final void layout(int left, int top, int right, int bottom) {
        onLayout(left, top, right, bottom);
    }

    public void onLayout(int left, int top, int right, int bottom) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
    }

    public int getMeasuredWidth() {
        return mMeasuredWidth;
    }

    public int getMeasuredHeight() {
        return mMeasuredHeight;
    }

    @Override
    public void draw(Canvas canvas) {

    }

    public Iterable<View> children() {
        return Collections.emptyList();
    }
}
