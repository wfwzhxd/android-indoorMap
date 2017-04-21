package com.hunter.indoormap;

import com.hunter.indoormap.beans.GPoint;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void createGPoint() {
        GPoint gPoint = new GPoint(4, 5, 6);
        assertEquals(6, gPoint.z);
    }
}