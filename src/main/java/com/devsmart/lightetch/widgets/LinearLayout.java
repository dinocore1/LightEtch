package com.devsmart.lightetch.widgets;


import com.devsmart.lightetch.View;
import com.devsmart.lightetch.ViewGroup;

public class LinearLayout extends ViewGroup {

    public class LayoutParams extends MarginLayoutParams {
        public int mWeight;
    }

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    private int mOrientation = VERTICAL;

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mOrientation == VERTICAL) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
            //measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void measureVertical(final int widthMeasureSpec, final int heightMeasureSpec) {

        LinearLayout.LayoutParams thizlp = (LayoutParams) mLayoutParams;

        int widthSize = MeasureSpec.getSize(widthMeasureSpec) - thizlp.mMarginLeft - thizlp.mMarginRight;
        int heightSize = MeasureSpec.getSize(heightMeasureSpec) - thizlp.mMarginTop - thizlp.mMarginBottom;

        int width = 0;
        int height = 0;
        int totalWeight = 0;

        int childMeasureSpecWidth = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST);
        int childMeasureSpecHeight = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);

        for(View child : children()){

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)child.getLayoutParams();
            totalWeight += lp.mWeight;

            child.measure(childMeasureSpecWidth, childMeasureSpecHeight);
            width = Math.max(width, child.getMeasuredWidth());
            height += child.getMeasuredHeight();

        }

        int delta = height - heightSize;
        if(delta != 0 && totalWeight > 0) {
            for(View child : children()) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)child.getLayoutParams();
                if(lp.mWeight > 0) {
                    int share = (int)(lp.mWeight * delta / totalWeight);
                    totalWeight -= share;
                    delta -= share;

                    child.measure(childMeasureSpecWidth, MeasureSpec.makeMeasureSpec(share, MeasureSpec.EXACTLY));
                }


            }
        }

    }
}
