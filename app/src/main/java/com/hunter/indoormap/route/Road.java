package com.hunter.indoormap.route;

import com.hunter.indoormap.CoordinateUtils;
import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Way;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hunter on 5/6/17.
 */

public class Road extends Way {

    public static final float DEFAULT_WIDTH = 1f;

    private float length;

    public Road(WayNode[] wayNodes) {
        super(0, wayNodes);
    }

    @Override
    public void setWayNodes(WayNode[] wayNodes) {
        super.setWayNodes(wayNodes);
        init();
    }

    public void setWayNodes(GPoint[] gPoints) {
        List<WayNode> wayNodes = new LinkedList<>();
        for (GPoint gPoint : gPoints) {
            wayNodes.add(new WayNode(gPoint, DEFAULT_WIDTH));
        }
        super.setWayNodes(wayNodes.toArray(new WayNode[wayNodes.size()]));
        init();
    }

    private void init() {
        if (isValid()) {
            calLength();
        }
    }

    private void calLength() {
        length = 0;
        WayNode[] wayNodes = getWayNodes();
        for (int i=1; i<wayNodes.length; i++) {
            length += CoordinateUtils.calDistance(wayNodes[i-1], wayNodes[i]);
        }
    }

    public boolean isValid() {
        return getWayNodes() != null && getWayNodes().length > 1;
    }
}
