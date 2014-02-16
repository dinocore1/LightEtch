package com.devsmart.lightetch.widgets;


import com.devsmart.lightetch.Context;
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

    public int orientation = VERTICAL;

    public LinearLayout(Context context) {
        super(context);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (orientation == VERTICAL) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void measureVertical(final int widthMeasureSpec, final int heightMeasureSpec) {

        ViewGroup.LayoutParams thizlp = mLayoutParams;

        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;
        int totalWeight = 0;

        for(View child : children()){

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)child.getLayoutParams();

            int childMeasureSpecWidth = MeasureSpec.makeMeasureSpec(widthSize - lp.marginLeft - lp.marginRight,
                    MeasureSpec.AT_MOST);
            int childMeasureSpecHeight = MeasureSpec.makeMeasureSpec(heightSize - lp.marginTop - lp.marginBottom,
                    MeasureSpec.AT_MOST);

            totalWeight += lp.weight;

            child.measure(childMeasureSpecWidth, childMeasureSpecHeight);
            width = Math.max(width, child.getMeasuredWidth() + lp.marginLeft + lp.marginRight);
            height += child.getMeasuredHeight();
        }

        int delta = height - heightSize;
        if(delta != 0 && totalWeight > 0) {
            delta = heightSize;
            for(View child : children()) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)child.getLayoutParams();
                if(lp.weight > 0) {
                    int share = (int)(lp.weight * delta / totalWeight);
                    totalWeight -= lp.weight;
                    delta -= share;

                    int childMeasureSpecWidth = MeasureSpec.makeMeasureSpec(widthSize - lp.marginLeft - lp.marginRight,
                            MeasureSpec.AT_MOST);

                    int childMeasureSpecHeight = MeasureSpec.makeMeasureSpec(share - lp.marginTop - lp.marginBottom,
                            MeasureSpec.EXACTLY);

                    child.measure(childMeasureSpecWidth, childMeasureSpecHeight);
                    width = Math.max(width, child.getMeasuredWidth() + lp.marginLeft + lp.marginRight);
                }
            }

            height = heightSize;
        }

        setMeasuredDimension(width, height);

    }

    private void measureHorizontal(final int widthMeasureSpec, final int heightMeasureSpec) {

        ViewGroup.LayoutParams thizlp = mLayoutParams;

        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;
        int totalWeight = 0;

        for(View child : children()){

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)child.getLayoutParams();

            int childMeasureSpecWidth = MeasureSpec.makeMeasureSpec(widthSize - lp.marginLeft - lp.marginRight,
                    MeasureSpec.AT_MOST);
            int childMeasureSpecHeight = MeasureSpec.makeMeasureSpec(heightSize - lp.marginTop - lp.marginBottom,
                    MeasureSpec.AT_MOST);

            totalWeight += lp.weight;

            child.measure(childMeasureSpecWidth, childMeasureSpecHeight);
            width += child.getMeasuredWidth();
            height = Math.max(height, child.getMeasuredHeight() + lp.marginTop + lp.marginBottom);
        }

        int delta = width - widthSize;
        if(delta != 0 && totalWeight > 0) {
            delta = widthSize;
            for(View child : children()) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)child.getLayoutParams();
                if(lp.weight > 0) {
                    int share = (int)(lp.weight * delta / totalWeight);
                    totalWeight -= lp.weight;
                    delta -= share;

                    int childMeasureSpecWidth = MeasureSpec.makeMeasureSpec(share - lp.marginLeft - lp.marginRight,
                            MeasureSpec.EXACTLY);

                    int childMeasureSpecHeight = MeasureSpec.makeMeasureSpec(heightSize - lp.marginTop - lp.marginBottom,
                            MeasureSpec.AT_MOST);

                    child.measure(childMeasureSpecWidth, childMeasureSpecHeight);
                    height = Math.max(height, child.getMeasuredHeight() + lp.marginTop + lp.marginBottom);
                }
            }

            width = widthSize;
        }

        setMeasuredDimension(width, height);

    }

    @Override
    public void onLayout(int left, int top, int right, int bottom) {

        int childLeft = left;
        int childTop = top;

        for(View child : children()){

            LinearLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();

            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();



            child.layout(childLeft + lp.marginLeft,
                    childTop + lp.marginTop,
                    childLeft + lp.marginLeft + childWidth,
                    childTop + lp.marginTop + childHeight);

            if(orientation == VERTICAL){
                childTop += childHeight + lp.marginTop + lp.marginBottom;
            } else {
                childLeft += childWidth + lp.marginLeft + lp.marginRight;
            }
        }
    }
}
