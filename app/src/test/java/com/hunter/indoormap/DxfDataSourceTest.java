package com.hunter.indoormap;

import com.hunter.indoormap.beans.Edges;
import static com.hunter.indoormap.beans.Way.*;
import com.hunter.indoormap.data.DxfDataSource;

import static com.hunter.indoormap.TestUtils.*;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hunter on 5/10/17.
 */

public class DxfDataSourceTest {

    @Test
    public void parseDxfTest() {
        DxfDataSource dxfDataSource = new DxfDataSource(new File("/home/hunter/hospital.dxf"));
        dxfDataSource.loadData();
        Log.o(dxfDataSource.getNodes(null, 0));
        Log.o(dxfDataSource.getWays(null, 0));
    }

    @Test
    public void packageEdgesTest() {
        Class clazz = DxfDataSource.class;
        DxfDataSource dxfDataSource = new DxfDataSource(new File("/home/hunter/hospital.dxf"));
        Method method = getMethod(clazz, "packageEdges", Edges.class);
        method.setAccessible(true);
        Edges edges = new Edges(null);
        List<WayLine> wayLines = new LinkedList<>();
        edges.setTag(wayLines);
        wayLines.add(new WayLine(new WayNode(1, 1, 0), new WayNode(1, 2, 0)));
        wayLines.add(new WayLine(new WayNode(2, 1, 0), new WayNode(2, 2, 0)));
        wayLines.add(new WayLine(new WayNode(1, 2, 0), new WayNode(2, 2, 0)));
        wayLines.add(new WayLine(new WayNode(1, 1, 0), new WayNode(2, 1, 0)));
        Object result = invoke(method, dxfDataSource, edges);
        Log.o(result + " " + edges);
    }
}
