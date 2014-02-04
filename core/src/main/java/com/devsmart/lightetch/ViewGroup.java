package com.devsmart.lightetch;


import java.util.ArrayList;

public class ViewGroup extends View {

    public static class LayoutParams {

        public static final int FILL_PARENT = -1;
        public static final int WRAP_CONTENT = -2;

        public int width = WRAP_CONTENT;
        public int height = WRAP_CONTENT;


    }

    public static class MarginLayoutParams extends LayoutParams {
        public int marginTop;
        public int marginBottom;
        public int marginLeft;
        public int marginRight;
    }

    private ArrayList<View> mChildren = new ArrayList<View>();

    public ViewGroup(Context context) {
        super(context);
    }

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
    public final void draw(Canvas canvas) {}
}
