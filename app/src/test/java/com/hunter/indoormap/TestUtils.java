package com.hunter.indoormap;

import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Point;

/**
 * Created by hunter on 4/12/17.
 */

public class TestUtils {
    public static Point p(int x, int y) {
        return new Point(x, y);
    }

    public static GPoint gp(int x, int y) {
        return new GPoint(x, y);
    }
}
