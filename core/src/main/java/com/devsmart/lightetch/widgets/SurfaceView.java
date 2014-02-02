package com.devsmart.lightetch.widgets;


import com.devsmart.lightetch.Canvas;
import com.devsmart.lightetch.View;

public class SurfaceView extends View {

    public interface Callback {
        void onDraw(SurfaceView sv, Canvas canvas);
    }

    private Callback mOnDrawCallback;

    public SurfaceView() {

    }

    public void setOnDraw(Callback cb) {
        mOnDrawCallback = cb;
    }

    @Override
    public void draw(Canvas canvas) {
        if(mOnDrawCallback != null) {
            mOnDrawCallback.onDraw(this, canvas);
        }
    }
}
