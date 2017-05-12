package com.hunter.indoormap;

import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.data.DxfDataSource;
import com.hunter.indoormap.route.Road;
import com.hunter.indoormap.route.impl.AstarRouter;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;

/**
 * Created by hunter on 5/12/17.
 */

public class AstarRouterTest {

    @Test
    public void routeTest() {
        DxfDataSource dxfDataSource = new DxfDataSource(new File("/home/hunter/hospital.dxf"));
        dxfDataSource.loadData();
        AstarRouter astarRouter = new AstarRouter(dxfDataSource);
        GPoint qiantai = new GPoint(8.4826f, -8.0234f, 0);
        GPoint hushizhan = new GPoint(3.1587f, -4.8710f, 0);
        GPoint zhongyike = new GPoint(10.979889f, -16.50050f, 1);
        Road[] roads = astarRouter.route(qiantai, zhongyike, null);
        Log.o(Arrays.toString(roads));
    }
}
