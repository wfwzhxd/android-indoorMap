package com.hunter.indoormap;

import com.hunter.indoormap.CoordinateUtils;
import com.hunter.indoormap.beans.Point;

import static com.hunter.indoormap.TestUtils.*;

import org.junit.*;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Created by hunter on 4/12/17.
 */

public class CoordinateUtilsTest {

    @org.junit.Test
    public void calDegree() {
        float degree = CoordinateUtils.calDegree(p(0, 0), p(2, 2));
        assertEquals(45, degree, 0.001);
        degree = CoordinateUtils.calDegree(p(0, 0), p(0, 2));
        assertEquals(90, degree, 0.001);
        degree = CoordinateUtils.calDegree(p(0, 0), p(-2, 2));
        assertEquals(135, degree, 0.001);
        degree = CoordinateUtils.calDegree(p(0, 0), p(-2, 0));
        assertEquals(180, degree, 0.001);
        degree = CoordinateUtils.calDegree(p(0, 0), p(-2, -2));
        assertEquals(225, degree, 0.001);
        degree = CoordinateUtils.calDegree(p(0, 0), p(2, -2));
        assertEquals(315, degree, 0.001);
    }

    @org.junit.Test
    public void calDistance() {
        float distance = CoordinateUtils.calDistance(p(0, 0), p(0, 2));
        assertEquals(2, distance, 0.001);
        distance = CoordinateUtils.calDistance(p(0, 0), p(2, 0));
        assertEquals(2, distance, 0.001);
        distance = CoordinateUtils.calDistance(p(0, 0), p(-2, 2));
        assertEquals(2.82, distance, 0.01);
    }

    @org.junit.Test
    public void rotateAtPoint() {
        Point point = CoordinateUtils.rotateAtPoint(p(0, 0), p(2, 0), 90, null);
        assertEquals(p(0, 2), point);
        point = CoordinateUtils.rotateAtPoint(p(1, 1), p(2, 0), 135, null);
        assertEquals(p(1, 2), point);
        point = CoordinateUtils.rotateAtPoint(p(1, 1), p(2, 0), 180, null);
        assertEquals(p(0, 2), point);
        point = CoordinateUtils.rotateAtPoint(p(-4, 0), p(-2, -2), 270, null);
        assertEquals(p(-6, -2), point);
    }
}
