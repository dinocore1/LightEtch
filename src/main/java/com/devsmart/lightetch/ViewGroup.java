package com.devsmart.lightetch;


import java.util.ArrayList;

public class ViewGroup extends View {

    private ArrayList<View> mChildren = new ArrayList<View>();


    public void addView(View view) {
        mChildren.add(view);
    }

    public void removeAllChildren() {
        mChildren.clear();
    }

    public void removeChildAt(int index) {
        mChildren.remove(index);
    }

    public int getChildCount() {
        return mChildren.size();
    }

    public View getViewAt(int index) {
        return mChildren.get(index);
    }

    @Override
    public Iterable<View> children() {
        return mChildren;
    }

    @Override
    public void draw(Canvas canvas) {
        for(View v : mChildren){
            v.draw(canvas);
        }
    }
}
