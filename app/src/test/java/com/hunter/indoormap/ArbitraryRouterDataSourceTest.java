package com.hunter.indoormap;

import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.data.DxfDataSource;
import com.hunter.indoormap.route.ArbitraryRouterDataSource;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;

/**
 * Created by hunter on 5/15/17.
 */

public class ArbitraryRouterDataSourceTest {

    @Test
    public void getNearestPoint() {
        DxfDataSource dataSource = new DxfDataSource(new File("/home/hunter/mapfile/SuShe.dxf"));
        dataSource.loadData();
        ArbitraryRouterDataSource arbitraryRouterDataSource = dataSource;
        Log.o(Arrays.toString(arbitraryRouterDataSource.getNearestPoint(new GPoint(2f, -7f, 1))));
    }
}
