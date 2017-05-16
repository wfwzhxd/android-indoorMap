package com.hunter.indoormap;

import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Line;
import com.hunter.indoormap.route.ARouterDataSource;

import static com.hunter.indoormap.TestUtils.*;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hunter on 5/7/17.
 */

public class ARouterDataSourceTest {

    @Test
    public void wNodeTest() {
        ARouterDataSource routerDS = new ARouterDataSource(null);
        GPoint p1 = gp(1.7f, 2.34524f, 3);
        ARouterDataSource.Wnode<GPoint> w1 = new ARouterDataSource.Wnode<>(p1);
        routerDS.addWnode(w1);
        assertEquals(w1, routerDS.getWnode(gp(1.7f, 2.34566f, 3)));
        assertNull(routerDS.getWnode(gp(0, 3, 1)));
    }

    @Test
    public void sliceInitLinesTest() {
        List<Line> lineList = new LinkedList<>();
        lineList.add(new Line(new GPoint(0, 0, 0), new GPoint(0, 5, 0)));
        lineList.add(new Line(new GPoint(0, 0, 0), new GPoint(5, 0, 0)));
        lineList.add(new Line(new GPoint(3, 2, 0), new GPoint(0, 2, 0)));
        lineList.add(new Line(new GPoint(0, 2, -1), new GPoint(0, 2, 2)));
        lineList.add(new Line(new GPoint(0, 1, 0), new GPoint(0, 2, 1)));
        ARouterDataSource aRds = new ARouterDataSource(lineList);
        Log.o(aRds.getWnode(new GPoint(0, 2, 0)));
        Log.o(aRds.getWnode(new GPoint(0, 2, 1)));
    }
}
