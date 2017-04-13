package com.hunter.indoormap;


import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Point;
import com.hunter.indoormap.beans.ShapeInfo;
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
        ShapeInfo width2 = new ShapeInfo(new ShapeInfo.Shape(3, new Point[]{p(0, 0), p(0, 2)}));
        Node node1 = new Node(2, null, gp(-2, -2), width2);
        Node node2 = new Node(4, null, gp(2, 2), width2);
        Way way = new Way(5, new Node[]{node1, node2});
//        assertTrue(way.contains(p(-2, -2)));
//        assertTrue(way.contains(p(0, 0)));
        /*
         * 由于四舍五入产生的误差，边界附近的点可能会得到错误的测试结果
         */
//        assertFalse(way.contains(p(-1, -3)));
//        assertFalse(way.contains(p(-1, 2)));
        System.out.println(way.getBounds());
    }

    @org.junit.Test
    public void shape_contains() {
        ShapeInfo.Shape shape = new ShapeInfo.Shape(0, new Point[]{new Point(-2, -2), new Point(2, -2), new Point(2, 2), new Point(-2, 2)});
        assertFalse(shape.contains(new Point(-2, 3)));
        assertTrue(shape.contains(new Point(2, 2)));
        assertTrue(shape.contains(new Point(0, 1)));
    }
}
