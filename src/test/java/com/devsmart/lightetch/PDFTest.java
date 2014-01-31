package com.devsmart.lightetch;

import com.devsmart.lightetch.render.PDFCanvas;
import com.pdfjet.Letter;
import com.pdfjet.PDF;
import com.pdfjet.Page;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;


public class PDFTest {

    private static class LineView extends View {

        @Override
        public void draw(Canvas canvas) {
            canvas.rotate(45);
            canvas.drawLine(0,30,mRight,30);
        }
    }

    @Test
    public void test1() throws Exception {

        FileOutputStream out = new FileOutputStream(new File("test.pdf"));
        PDF pdf = new PDF(out);
        Page page = new Page(pdf, Letter.PORTRAIT);

        PDFCanvas canvas = new PDFCanvas(page);

        Renderer.render(new LineView(), canvas);


        pdf.flush();
        out.close();

    }
}
