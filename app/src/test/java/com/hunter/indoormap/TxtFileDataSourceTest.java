package com.hunter.indoormap;

import android.app.Notification;

import com.hunter.indoormap.beans.FloorMap;
import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Point;
import com.hunter.indoormap.beans.ShapeInfo;
import com.hunter.indoormap.beans.Way;
import com.hunter.indoormap.data.TxtFileDataSource;

import org.junit.*;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

import static com.hunter.indoormap.TestUtils.*;

/**
 * Created by hunter on 4/15/17.
 */

public class TxtFileDataSourceTest {

    private static final String TESTDATA_PATH = "./app/src/test/java/com/hunter/indoormap/testData";

    private static final Class<TxtFileDataSource> clazz = TxtFileDataSource.class;

    static Method getMethod(String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {}
        return null;
    }

    static Field getField(String fieldName) {
        Field result = null;
        Class<? super TxtFileDataSource> clazz = TxtFileDataSourceTest.clazz;
        while (result == null || !clazz.equals(Object.class)) {
            try {
                result = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {}
            clazz = clazz.getSuperclass();
        }
        return result;
    }

    static Object invoke(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Object invoke(Method method, Object target, Object... objs) {
        try {
            return method.invoke(target, objs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    static TxtFileDataSource getInstanceWithShapesLoaded() {
        TxtFileDataSource txtFileDataSource = new TxtFileDataSource(new File(TESTDATA_PATH));
        Method method = getMethod("loadShapes");
        method.setAccessible(true);
        invoke(method, txtFileDataSource);
        Field field = getField("shapes");
        field.setAccessible(true);
        Map<Integer, ShapeInfo.Shape> shapes = (Map<Integer, ShapeInfo.Shape>) invoke(field, txtFileDataSource);
        Log.e("", shapes.toString());
        return txtFileDataSource;
    }

    @org.junit.Test
    public void parsePointTest() {
        TxtFileDataSource txtFileDataSource = new TxtFileDataSource(new File("/"));
        Method method = getMethod("parsePoint", String.class);
        method.setAccessible(true);
        GPoint gPoint = (GPoint) invoke(method, txtFileDataSource, "1");
        assertEquals(null, gPoint);
        gPoint = (GPoint) invoke(method, txtFileDataSource, "1,2");
        assertEquals(p(1, 2), gPoint);
        gPoint = (GPoint) invoke(method, txtFileDataSource, "-1,2,-125");
        assertEquals(gp(-1, 2, -125), gPoint);
        gPoint = (GPoint) invoke(method, txtFileDataSource, "-1,2,-125,3");
        assertEquals(null, gPoint);
    }

    @Test
    public void parsePointsTest() {
        TxtFileDataSource txtFileDataSource = new TxtFileDataSource(new File("/"));
        Method method = getMethod("parsePoints", String.class);
        method.setAccessible(true);
        Point[] points = (Point[]) invoke(method, txtFileDataSource, "1,2;3,4,5");
        assertArrayEquals(new Point[]{gp(1, 2), gp(3, 4, 5)}, points);
        points = (Point[]) invoke(method, txtFileDataSource, "1,2");
        assertArrayEquals(new Point[]{gp(1, 2)}, points);
    }

    @Test
    public void parseWayNodeTest() {
        TxtFileDataSource txtFileDataSource = new TxtFileDataSource(new File("/"));
        Method method = getMethod("parseWayNode", String.class);
        method.setAccessible(true);
        Way.WayNode wayNode = (Way.WayNode) invoke(method, txtFileDataSource, "0,1,-5,2.3");
        assertEquals(new Way.WayNode(0, 1, -5, 2.3f), wayNode);
        wayNode = (Way.WayNode) invoke(method, txtFileDataSource, "0,1,2.3");
        assertNull(wayNode);
    }

    @Test
    public void parseWayNodesTest() {
        TxtFileDataSource txtFileDataSource = new TxtFileDataSource(new File("/"));
        Method method = getMethod("parseWayNodes", String.class);
        method.setAccessible(true);
        Way.WayNode[] wayNodes = (Way.WayNode[]) invoke(method, txtFileDataSource, "0,1,-5,2.3");
        assertArrayEquals(new Way.WayNode[]{new Way.WayNode(0, 1, -5, 2.3f)}, wayNodes);
        wayNodes = (Way.WayNode[]) invoke(method, txtFileDataSource, "0,1,-5,2.3;-15,7,0,-0.5");
        assertArrayEquals(new Way.WayNode[]{new Way.WayNode(0, 1, -5, 2.3f), new Way.WayNode(-15, 7, 0, -0.5f)}, wayNodes);
    }

    @Test
    public void parseLine2ShapeTest() {
        TxtFileDataSource txtFileDataSource = new TxtFileDataSource(new File("."));
        Method method = getMethod("parseLine2Shape", String.class);
        method.setAccessible(true);
        ShapeInfo.Shape shape = (ShapeInfo.Shape) invoke(method, txtFileDataSource, "3|1,1;-1,1;-1,-1");
        assertEquals(new ShapeInfo.Shape(3, new Point[]{p(1,1), p(-1, 1), p(-1, -1)}), shape);
    }

    @Test
    public void parseLine2WayTest() {
        TxtFileDataSource txtFileDataSource = new TxtFileDataSource(new File("."));
        Method method = getMethod("parseLine2Way", String.class);
        method.setAccessible(true);
        Way way = (Way) invoke(method, txtFileDataSource, "3001|DaZhangLu|1|0|3,5,2,8;-11,-15,2,5.4");
        assertEquals(new Way(3001, "DaZhangLu", true, false, new Way.WayNode[]{new Way.WayNode(3, 5, 2, 8), new Way.WayNode(-11, -15, 2, 5.4f)}), way);
    }

    @Test
    public void loadShapesTest() {
        TxtFileDataSource txtFileDataSource = new TxtFileDataSource(new File(TESTDATA_PATH));
        Method method = getMethod("loadShapes");
        method.setAccessible(true);
        invoke(method, txtFileDataSource);
        Field field = getField("shapes");
        field.setAccessible(true);
        Map<Integer, ShapeInfo.Shape> shapes = (Map<Integer, ShapeInfo.Shape>) invoke(field, txtFileDataSource);
        Log.o(shapes.toString());
    }

    @Test
    public void parseLine2FloorMapTest() {
        TxtFileDataSource txtFileDataSource = getInstanceWithShapesLoaded();
        Method method = getMethod("parseLine2FloorMap", String.class);
        method.setAccessible(true);
        FloorMap floorMap = (FloorMap) invoke(method, txtFileDataSource, "2003|ThreeFloor|3|1000|0");
        assertNull(floorMap);
    }

    @Test
    public void loadFloorsTest() {
        TxtFileDataSource txtFileDataSource = getInstanceWithShapesLoaded();
        Method method = getMethod("loadFloors");
        method.setAccessible(true);
        boolean result = (boolean) invoke(method, txtFileDataSource);
        assertTrue(result);
        Field field = getField("floorMaps");
        field.setAccessible(true);
        List<FloorMap> floorMaps = (List<FloorMap>) invoke(field, txtFileDataSource);
        Log.o(floorMaps.toString());
    }

    @Test
    public void loadWaysTest() {
        TxtFileDataSource txtFileDataSource = getInstanceWithShapesLoaded();
        Method method = getMethod("loadFloors");
        method.setAccessible(true);
        boolean result = (boolean) invoke(method, txtFileDataSource);
        assertTrue(result);
        method = getMethod("loadWays");
        method.setAccessible(true);
        result = (boolean) invoke(method, txtFileDataSource);
        assertTrue(result);
        Field field = getField("ways");
        field.setAccessible(true);
        Map<Integer, LinkedList<Way>> ways = (Map<Integer, LinkedList<Way>>) invoke(field, txtFileDataSource);
        Log.o(ways.toString());
    }

    @Test
    public void parseLine2NodeTest() {
        TxtFileDataSource txtFileDataSource = getInstanceWithShapesLoaded();
        Method method = getMethod("parseLine2Node", String.class);
        method.setAccessible(true);
        Node node = (Node) invoke(method, txtFileDataSource, "4001|School|1|102,-5,2|1002|180");
        Log.o(node.toString());
    }

    @Test
    public void loadNodesTest() {
        TxtFileDataSource txtFileDataSource = getInstanceWithShapesLoaded();
        Method method = getMethod("loadFloors");
        method.setAccessible(true);
        boolean result = (boolean) invoke(method, txtFileDataSource);
        assertTrue(result);
        method = getMethod("loadNodes");
        method.setAccessible(true);
        result = (boolean) invoke(method, txtFileDataSource);
        assertTrue(result);
        Field field = getField("nodes");
        field.setAccessible(true);
        Map<Integer, LinkedList<Node>> nodes = (Map<Integer, LinkedList<Node>>) invoke(field, txtFileDataSource);
        Log.o(nodes.toString());
    }

    @Test
    public void test() {
        TxtFileDataSource txtFileDataSource = new TxtFileDataSource(new File(TESTDATA_PATH));
    }

}
