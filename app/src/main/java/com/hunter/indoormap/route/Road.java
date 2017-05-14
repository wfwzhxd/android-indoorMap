package com.hunter.indoormap.route;

import com.hunter.indoormap.CoordinateUtils;
import com.hunter.indoormap.beans.GPoint;
import static com.hunter.indoormap.beans.Way.WayNode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hunter on 5/6/17.
 */

public class Road {

    public static final float DEFAULT_WIDTH = 1f;

    private float length;

    private WayNode[] wayNodes;

    public Road(List<GPoint> gPoints) {
        this.wayNodes = new WayNode[gPoints.size()];
        for (int i=0; i<gPoints.size(); i++) {
            wayNodes[i] = new WayNode(gPoints.get(i), DEFAULT_WIDTH);
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

    public WayNode[] getWayNodes() {
        return wayNodes;
    }

    public float getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "Road{" +
                "length=" + length +
                ", wayNodes=" + Arrays.toString(wayNodes) +
                '}';
    }
}
