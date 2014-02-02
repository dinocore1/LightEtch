package com.devsmart.lightetch.render;


import com.devsmart.lightetch.Canvas;
import com.devsmart.lightetch.Paint;
import com.devsmart.lightetch.graphics.Color;


import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class JavaAwtCanvas implements Canvas {

    private final int mWidth;
    private final int mHeight;
    private final Graphics2D mGraphics;


    private static class AwtGraphicsState extends GraphicsState {

        public AwtGraphicsState(Graphics2D graphics2D) {
            mTransform = graphics2D.getTransform();
            mPaint = new Paint();
            java.awt.Color c = graphics2D.getColor();
            mPaint.mFillColor = Color.argb(
                    c.getAlpha(),
                    c.getRed(),
                    c.getGreen(),
                    c.getBlue());

        }

        public void set(Graphics2D graphics2D) {

        }
    }

    private ArrayList<AwtGraphicsState> mGraphicsStack = new ArrayList<AwtGraphicsState>();

    public JavaAwtCanvas(BufferedImage image) {
        mGraphics = image.createGraphics();
        mWidth = image.getWidth();
        mHeight = image.getHeight();
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    @Override
    public int save() {
        mGraphicsStack.add(new AwtGraphicsState(mGraphics));
        return mGraphicsStack.size()-1;
    }

    @Override
    public void restore() {
        AwtGraphicsState state = mGraphicsStack.remove(mGraphicsStack.size()-1);
        state.set(mGraphics);
    }

    @Override
    public void restoreToCount(int saveCount) {

    }

    @Override
    public void rotate(float degrees) {
        mGraphics.rotate(degrees);
    }

    @Override
    public void scale(float sx, float sy) {
        mGraphics.scale(sx, sy);
    }

    @Override
    public void translate(float dx, float dy) {
        mGraphics.translate(dx, dy);
    }

    @Override
    public void shear(float sx, float sy) {
        mGraphics.shear(sx, sy);
    }

    @Override
    public void drawLine(int startx, int starty, int stopx, int stopy, Paint paint) {
        mGraphics.drawLine(startx, starty, stopx, stopy);
    }
}
