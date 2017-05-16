package com.hunter.indoormap.route;

import android.support.annotation.NonNull;

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

    public Road(@NonNull GPoint[] gPoints) {
        this.wayNodes = new WayNode[gPoints.length];
        for (int i=0; i<gPoints.length; i++) {
            wayNodes[i] = new WayNode(gPoints[i], DEFAULT_WIDTH);
        }
        init();
    }

    public Road(@NonNull List<GPoint> gPoints) {
        this(gPoints.toArray(new GPoint[gPoints.size()]));
    }

    private void init() {
        if (isValid()) {
            calLength();
        }
    }

    private void calLength() {
        length = 0;
        if (wayNodes.length == 1) return;
        for (int i=1; i<wayNodes.length; i++) {
            length += CoordinateUtils.calDistance(wayNodes[i-1], wayNodes[i]);
        }
    }

    public boolean isValid() {
        return wayNodes != null && wayNodes.length != 0;
    }

    public WayNode[] getWayNodes() {
        return wayNodes;
    }

    public Road addWayNode2First(@NonNull GPoint node) {
        WayNode[] newWayNodes = new WayNode[wayNodes.length + 1];
        newWayNodes[0] = new WayNode(node, DEFAULT_WIDTH);
        System.arraycopy(wayNodes, 0, newWayNodes, 1, wayNodes.length);
        wayNodes = newWayNodes;
        calLength();
        return this;
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
