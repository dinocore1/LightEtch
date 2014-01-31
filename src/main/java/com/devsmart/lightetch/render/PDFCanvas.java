package com.devsmart.lightetch.render;

import com.devsmart.lightetch.Canvas;
import com.pdfjet.Page;

import java.awt.geom.AffineTransform;
import java.io.IOException;


public class PDFCanvas implements Canvas {

    private final Page mPage;
    private AffineTransform mTransform;

    public PDFCanvas(Page page) {
        mPage = page;
        mTransform = new AffineTransform();
    }


    @Override
    public int getWidth() {
        return (int) mPage.getWidth();
    }

    @Override
    public int getHeight() {
        return (int) mPage.getHeight();
    }

    @Override
    public int save() {
        return 0;
    }

    @Override
    public void restore() {

    }

    @Override
    public void restoreToCount(int saveCount) {

    }

    private float degreeToRadian(float degrees) {
        return (float) (degrees * Math.PI / 180.0);
    }

    @Override
    public void rotate(float degrees) {
        mTransform.rotate(degreeToRadian(degrees));
    }

    @Override
    public void scale(float sx, float sy) {
        mTransform.scale(sx, sy);
    }

    @Override
    public void translate(float dx, float dy) {
        mTransform.translate(dx, dy);
    }

    @Override
    public void shear(float sx, float sy) {
        mTransform.shear(sx, sy);
    }

    @Override
    public void drawLine(int startx, int starty, int stopx, int stopy) {
        float[] srcPts = new float[]{startx, starty, stopx, stopy};
        float[] dstPts = new float[4];
        mTransform.transform(srcPts, 0, dstPts, 0, 2);
        try {
            mPage.drawLine(dstPts[0], dstPts[1], dstPts[2], dstPts[3]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
