package com.devsmart.lightetch;


import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class LayoutInflatorTest {

    @Test
    public void inflateTest() throws Exception {

        InputStream in;
        in = LayoutInflatorTest.class.getClassLoader().getSystemResourceAsStream("testlayout.xml");

        View newView = LayoutInflator.inflate(in);
        Assert.assertNotNull(newView);

    }
}
