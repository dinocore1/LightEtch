package com.devsmart.lightetch;


import com.devsmart.lightetch.graphics.RectF;

public class Renderer {

    private static class DrawVisitor {

        private final Canvas mCanvas;

        public DrawVisitor(Canvas canvas) {
            mCanvas = canvas;
        }

        void visit(View view) {

            final RectF bounds = view.getBounds();
            mCanvas.save();
            mCanvas.translate(bounds.left(), bounds.top());
            for(View v : view.children()){
                visit(v);
            }
            if(view.mBackground != null){
                view.mBackground.draw(mCanvas);
            }
            view.draw(mCanvas);
            mCanvas.restore();
        }
    }

    public static void render(View root, Canvas canvas) {

        final int width = canvas.getWidth();
        final int height = canvas.getHeight();

        //measure pass
        root.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST));

        //layout pass
        root.layout(0, 0, root.getMeasuredWidth(), root.getMeasuredHeight());

        //draw pass
        DrawVisitor drawVisitor = new DrawVisitor(canvas);
        drawVisitor.visit(root);
    }
}
