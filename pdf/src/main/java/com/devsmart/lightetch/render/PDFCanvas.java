package com.devsmart.lightetch.render;


import com.devsmart.lightetch.Canvas;
import com.devsmart.lightetch.Paint;
import com.devsmart.lightetch.graphics.Color;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import java.io.IOException;

public class PDFCanvas implements Canvas {

    private final PDPage mPage;
    private PDPageContentStream mStream;
    private int mGraphicsStackHeight = 0;

    public PDFCanvas(PDDocument doc, PDPage page) throws IOException {
        mPage = page;
        mStream = new PDPageContentStream(doc, page);
    }

    public void done() {
        try {
            mStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getWidth() {
        return (int) mPage.getMediaBox().getWidth();
    }

    @Override
    public int getHeight() {
        return (int) mPage.getMediaBox().getHeight();
    }

    @Override
    public int save() {
        try {
            mStream.saveGraphicsState();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mGraphicsStackHeight++;
    }

    @Override
    public void restore() {
        try {
            mStream.restoreGraphicsState();
            mGraphicsStackHeight--;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void restoreToCount(int saveCount) {
        while(mGraphicsStackHeight > saveCount){
            restore();
        }

    }

    @Override
    public void rotate(float degrees) {
    }

    @Override
    public void scale(float sx, float sy) {

    }

    @Override
    public void translate(float dx, float dy) {

    }

    @Override
    public void shear(float sx, float sy) {

    }

    @Override
    public void drawLine(int startx, int starty, int stopx, int stopy, Paint paint) {
        try {
            save();
            mStream.setStrokingColor(Color.red(paint.mStrokeColor), Color.green(paint.mStrokeColor), Color.blue(paint.mStrokeColor));
            mStream.setNonStrokingColor(Color.red(paint.mFillColor), Color.green(paint.mFillColor), Color.blue(paint.mFillColor));
            mStream.setLineWidth(paint.mStrokeWidth);
            mStream.drawLine(startx, starty, stopx, stopy);
            restore();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
