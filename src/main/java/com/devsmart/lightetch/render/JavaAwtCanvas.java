package com.devsmart.lightetch.render;


import com.devsmart.lightetch.Canvas;
import com.devsmart.lightetch.Paint;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class JavaAwtCanvas implements Canvas {

    private final int mWidth;
    private final int mHeight;
    private final Graphics2D mGraphics;

    private static class GraphicsState {
        private final AffineTransform mTransform;

        public GraphicsState(Graphics2D g) {
            mTransform = g.getTransform();
        }

        public void set(Graphics2D g) {
            g.setTransform(mTransform);
        }
    }

    private ArrayList<GraphicsState> mGraphicsStack = new ArrayList<GraphicsState>();

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
        mGraphicsStack.add(new GraphicsState(mGraphics));
        return mGraphicsStack.size()-1;
    }

    @Override
    public void restore() {
        GraphicsState state = mGraphicsStack.remove(mGraphicsStack.size()-1);
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
    public void drawLine(int startx, int starty, int stopx, int stopy) {
        mGraphics.drawLine(startx, starty, stopx, stopy);
    }
}
