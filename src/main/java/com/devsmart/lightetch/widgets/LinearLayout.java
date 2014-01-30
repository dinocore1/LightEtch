package com.devsmart.lightetch.widgets;


import com.devsmart.lightetch.ViewGroup;

public class LinearLayout extends ViewGroup {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    private int mOrientation = VERTICAL;

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mOrientation == VERTICAL){
            
        }
    }
}
