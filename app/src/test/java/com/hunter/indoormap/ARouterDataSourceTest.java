package com.hunter.indoormap;

import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.route.ARouterDataSource;

import static com.hunter.indoormap.TestUtils.*;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Created by hunter on 5/7/17.
 */

public class ARouterDataSourceTest {

    ARouterDataSource routerDS = new ARouterDataSource(null) {};

    @Test
    public void wNodeTest() {
        GPoint p1 = gp(1.7f, 2.34524f, 3);
        ARouterDataSource.Wnode<GPoint> w1 = new ARouterDataSource.Wnode<>(p1);
        routerDS.addWnode(w1);
        assertEquals(w1, routerDS.getWnode(gp(1.7f, 2.34566f, 3)));
        assertNull(routerDS.getWnode(gp(0, 3, 1)));
    }
}
