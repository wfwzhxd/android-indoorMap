package com.hunter.indoormap;


import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Line;
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
        Way.WayNode wayNode1 = new Way.WayNode(7.0390606f, -1.0645458f, 1, 2f);
        Way.WayNode wayNode2 = new Way.WayNode(7.325643f, -38.647953f, 1, 2f);
//        Way way = new Way(5, new Way.WayLine[]{new Way.WayLine(wayNode1, wayNode2)});
        assertTrue(new Way.WayLine(wayNode1, wayNode2).contains(gp(7.6946883f, -11.1176405f, 1)));
    }

    @Test
    public void p() {
        GPoint src = new GPoint(3, 3, 0);
        Point begin = new Point(0, 0);
        Point end = new Point(0, 5);
        Line line = new Line(new GPoint(begin.x, begin.y, 0), new GPoint(end.x, end.y, 0));
        Point pedal = new Point(0, 0);

        double dx = begin.x - end.x;
        double dy = begin.y - end.y;
        if(Math.abs(dx) < 0.00000001 && Math.abs(dy) < 0.00000001 )
        {
            pedal = begin;
        } else {
            double u = (src.x - begin.x)*(begin.x - end.x) +
                    (src.y - begin.y)*(begin.y - end.y);
            u = u/((dx*dx)+(dy*dy));

            pedal.x = (float) (begin.x + u*dx);
            pedal.y = (float) (begin.y + u*dy);
        }


        Log.o(pedal);
    }

}
