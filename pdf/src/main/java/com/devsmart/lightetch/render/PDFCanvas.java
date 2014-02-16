package com.devsmart.lightetch.render;


import and.awt.geom.AffineTransform;
import com.devsmart.lightetch.Canvas;
import com.devsmart.lightetch.Context;
import com.devsmart.lightetch.Paint;
import com.devsmart.lightetch.graphics.Color;
import com.devsmart.lightetch.graphics.RectF;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;

public class PDFCanvas implements Canvas, Context {

    private final PDPage mPage;
    private PDPageContentStream mStream;
    private int mGraphicsStackHeight = 0;

    public PDFCanvas(PDDocument doc, PDPage page) throws IOException {
        mPage = page;
        mStream = new PDPageContentStream(doc, page);


        AffineTransform matrix = new AffineTransform();
        matrix.translate(0, getHeight());
        matrix.scale(1, -1);
        mStream.concatenate2CTM(matrix);

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
        try {
            AffineTransform matrix = new AffineTransform();
            matrix.rotate(degrees / 180.0 * Math.PI);
            mStream.concatenate2CTM(matrix);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void scale(float sx, float sy) {
        try {
            AffineTransform matrix = new AffineTransform();
            matrix.scale(sx, sy);
            mStream.concatenate2CTM(matrix);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void translate(float dx, float dy) {
        try {
            AffineTransform matrix = new AffineTransform();
            matrix.translate(dx, dy);
            mStream.concatenate2CTM(matrix);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shear(float sx, float sy) {
        try {
            AffineTransform matrix = new AffineTransform();
            matrix.shear(sx, sy);
            mStream.concatenate2CTM(matrix);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getStringBounds(String string, RectF bounds, Paint paint) {

        try {
            PDFont font = PDType1Font.HELVETICA_BOLD;
            float width = font.getStringWidth(string) / 1000 * paint.mTextSize;
            float height = font.getFontBoundingBox().getHeight() / 1000 * paint.mTextSize;

            bounds.setWidth(width);
            bounds.setHeight(height);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawText(String string, int x, int y, Paint paint) {
        try {
            PDFont font = PDType1Font.HELVETICA_BOLD;

            mStream.beginText();
            mStream.setFont(font, paint.mTextSize);

            AffineTransform matrix = new AffineTransform();
            //matrix.translate(0, getHeight());
            matrix.scale(1, -1);
            mStream.setTextMatrix(matrix);

            mStream.moveTextPositionByAmount(x, -y);
            mStream.drawString(string);
            mStream.endText();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawLine(int startx, int starty, int stopx, int stopy, Paint paint) {
        try {
            save();
            mStream.setStrokingColor(Color.red(paint.mStrokeColor), Color.green(paint.mStrokeColor), Color.blue(paint.mStrokeColor));
            mStream.setNonStrokingColor(Color.red(paint.mFillColor), Color.green(paint.mFillColor), Color.blue(paint.mFillColor));
            mStream.setLineWidth(paint.mStrokeWidth);
            mStream.drawLine(startx, getHeight()-starty, stopx, getHeight()-stopy);
            restore();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawRect(float left, float top, float width, float height, Paint paint) {
        try {
            save();
            mStream.setStrokingColor(Color.red(paint.mStrokeColor), Color.green(paint.mStrokeColor), Color.blue(paint.mStrokeColor));
            mStream.setNonStrokingColor(Color.red(paint.mFillColor), Color.green(paint.mFillColor), Color.blue(paint.mFillColor));
            mStream.setLineWidth(paint.mStrokeWidth);
            mStream.fillRect(left, top, width, height);
            restore();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
