package com.hunter.indoormap.route;

import com.hunter.indoormap.CoordinateUtils;
import com.hunter.indoormap.beans.GPoint;
import static com.hunter.indoormap.beans.Way.WayNode;

import java.util.Arrays;

/**
 * Created by hunter on 5/6/17.
 */

public class Road {

    public static final float DEFAULT_WIDTH = 1f;

    private float length;

    private WayNode[] wayNodes;

    public Road(GPoint[] gPoints) {
        this.wayNodes = new WayNode[gPoints.length];
        for (int i=0; i<gPoints.length; i++) {
            wayNodes[i] = new WayNode(gPoints[i], DEFAULT_WIDTH);
        }
        init();
    }

    public Road(WayNode[] wayNodes) {
        this.wayNodes = wayNodes;
        init();
    }


    private void init() {
        if (isValid()) {
            calLength();
        }
    }

    private void calLength() {
        length = 0;
        for (int i=1; i<wayNodes.length; i++) {
            length += CoordinateUtils.calDistance(wayNodes[i-1], wayNodes[i]);
        }
    }

    public boolean isValid() {
        return wayNodes != null && wayNodes.length > 1;
    }

    @Override
    public String toString() {
        return "Road{" +
                "length=" + length +
                ", wayNodes=" + Arrays.toString(wayNodes) +
                '}';
    }
}
