package com.hunter.indoormap;

import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.route.RouterDataSource;

import static com.hunter.indoormap.TestUtils.*;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Created by hunter on 5/7/17.
 */

public class RouterDataSourceTest {

    RouterDataSource routerDS = new RouterDataSource(null) {};

    @Test
    public void wNodeTest() {
        GPoint p1 = gp(1.7f, 2.34524f, 3);
        RouterDataSource.Wnode<GPoint> w1 = new RouterDataSource.Wnode<>(p1);
        routerDS.addWnode(w1);
        assertEquals(w1, routerDS.getWnode(gp(1.7f, 2.34566f, 3)));
        assertNull(routerDS.getWnode(gp(0, 3, 1)));
    }
}
