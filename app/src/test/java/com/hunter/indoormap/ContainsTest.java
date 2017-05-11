package com.hunter.indoormap;


import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Point;
import com.hunter.indoormap.beans.Way;
import static com.hunter.indoormap.TestUtils.*;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * Created by hunter on 4/2/17.
 */

public class ContainsTest {

    @org.junit.Test
    public void way_contains() {
        Way.WayNode wayNode1 = new Way.WayNode(2, 2, 2);
        Way.WayNode wayNode2 = new Way.WayNode(5, 2, 2);
        Way way = new Way(5, new Way.WayLine[]{new Way.WayLine(wayNode1, wayNode2)});
        assertTrue(way.contains(p(2, 2)));
        assertTrue(way.contains(p(4.9f, 2)));
        assertTrue(way.contains(p(3, 1.01f)));
        assertTrue(way.contains(p(4, 2.99f)));
        assertTrue(way.contains(p(3.58f, 2.164f)));
        assertFalse(way.contains(p(5.1f, 2)));
        // 由于四舍五入产生的误差，边界附近的点可能会得到错误的测试结果
    }

}
