package com.devsmart.lightetch.pdf;

import com.devsmart.lightetch.*;
import com.devsmart.lightetch.graphics.Color;
import com.devsmart.lightetch.render.PDFCanvas;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;


public class PDFTest {

    private static class LineView extends View {

        Paint mPaint = new Paint();

        public LineView(Context context) {
            super(context);
            mPaint.mFillColor = Color.argb(0xff, 0xff, 0x0, 0x0);
            mPaint.mStrokeColor = Color.argb(0xff, 0x0, 0xff, 0xff);
            mPaint.mStrokeWidth = 5f;
        }

        @Override
        public void draw(Canvas canvas) {
            //canvas.rotate(45);
            canvas.drawLine(0,60,mRight,60, mPaint);
        }
    }

    @Test
    public void test1() throws Exception {

        FileOutputStream out = new FileOutputStream(new File("test.pdf"));
        PDDocument pdf = new PDDocument();
        PDPage page = new PDPage();
        pdf.addPage(page);


        PDFCanvas canvas = new PDFCanvas(pdf, page);

        Renderer.render(new LineView(canvas), canvas);

        canvas.done();

        pdf.save(out);
        out.close();

    }

    @Test
    public void test2() throws Exception {
        FileOutputStream out = new FileOutputStream(new File("test.pdf"));
        PDDocument pdf = new PDDocument();
        PDPage page = new PDPage();
        pdf.addPage(page);


        PDFCanvas canvas = new PDFCanvas(pdf, page);

        View root = LayoutInflator.inflate(canvas, LayoutInflatorTest.class.getClassLoader().getSystemResourceAsStream("testlayout.xml"));
        Renderer.render(root, canvas);

        canvas.done();

        pdf.save(out);
        pdf.close();
        out.close();
    }
}
