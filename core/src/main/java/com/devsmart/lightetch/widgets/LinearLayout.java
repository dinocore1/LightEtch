package com.devsmart.lightetch.widgets;


import com.devsmart.lightetch.View;
import com.devsmart.lightetch.ViewGroup;

public class LinearLayout extends ViewGroup {

    public static class LayoutParams extends MarginLayoutParams {

        public static final int GRAVITY_LEFT = 0;
        public static final int GRAVITY_RIGHT = 1;
        public static final int GRAVITY_CENTER = 2;

        public int weight;
        public int gravity;
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

        ViewGroup.LayoutParams thizlp = mLayoutParams;

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;
        int totalWeight = 0;

        int childMeasureSpecWidth = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST);
        int childMeasureSpecHeight = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);

        for(View child : children()){

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)child.getLayoutParams();
            totalWeight += lp.weight;

            child.measure(childMeasureSpecWidth, childMeasureSpecHeight);
            width = Math.max(width, child.getMeasuredWidth());
            height += child.getMeasuredHeight();

        }

        int delta = height - heightSize;
        if(delta != 0 && totalWeight > 0) {
            for(View child : children()) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)child.getLayoutParams();
                if(lp.weight > 0) {
                    int share = (int)(lp.weight * delta / totalWeight);
                    totalWeight -= share;
                    delta -= share;

                    child.measure(childMeasureSpecWidth, MeasureSpec.makeMeasureSpec(share, MeasureSpec.EXACTLY));
                }


            }
        }

    }
}
