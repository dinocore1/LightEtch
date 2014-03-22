package com.devsmart.lightetch;


import com.devsmart.lightetch.graphics.RectF;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class LayoutInflatorTest {

    @Test
    public void inflateTest() throws Exception {

        Context ctx = new Context() {
            @Override
            public void getStringBounds(String string, RectF bounds, Paint paint) {

            }
        };

        InputStream in;
        in = LayoutInflatorTest.class.getClassLoader().getSystemResourceAsStream("testlayout.xml");
        Assert.assertNotNull(in);

        View newView = LayoutInflator.inflate(ctx, in);
        Assert.assertNotNull(newView);

    }
}
