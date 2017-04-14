package com.hunter.indoormap.beans;

import com.hunter.indoormap.CoordinateUtils;

/**
 * Created by hunter on 3/25/17.
 */

public class Way extends MObj {

    public static class WayNode extends GPoint{
        float wide;

        public WayNode(int x, int y, float wide) {
            this(x, y, 0, wide);
        }

        public WayNode(int x, int y, int z, float wide) {
            super(x, y, z);
            this.wide = wide;
        }
    }

    WayNode[] wayNodes;


    boolean oneway = false;

    private ShapeInfo[] shapeInfos;

    public Way(int id, WayNode[] wayNodes) {
        this(id, null, wayNodes);
    }

    public Way(int id, String name, WayNode[] wayNodes) {
        this(id, name, wayNodes, false);
    }

    public Way(int id, String name, WayNode[] wayNodes, boolean oneway) {
        super(id, name);
        setWayNodes(wayNodes);
        this.oneway = oneway;
    }

    public WayNode[] getWayNodes() {
        return wayNodes;
    }

    public void setWayNodes(WayNode[] wayNodes) {
        this.wayNodes = wayNodes;
        shapeInfos = new ShapeInfo[wayNodes.length>0?wayNodes.length-1:wayNodes.length];
        bounds = null;
    }

    public boolean isOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    private ShapeInfo getShapeInfo(int i) {
        if (shapeInfos[i] == null) {
            shapeInfos[i] = calculateShapeInfo(wayNodes[i], wayNodes[i+1]);
        }
        return shapeInfos[i];
    }

    @Override
    public boolean contains(Point point) {
        for (int i=0; i < wayNodes.length-1; i++) {
            point = CoordinateUtils.relativeCoord(wayNodes[i], point);
            if (getShapeInfo(i).contains(point)) {
                return true;
            }
        }
        return false;
    }

    private ShapeInfo calculateShapeInfo(WayNode start, WayNode end) {
        float degree = CoordinateUtils.calDegree(start, end);
        System.out.println(start + " " + end + " degree " + degree);
        Point endPoint = CoordinateUtils.rotateAtPoint(start, end, -degree, false);
        endPoint = CoordinateUtils.relativeCoord(start, endPoint);
        int startHalfWidth = Math.round(start.wide/2);
        Point spt = CoordinateUtils.pointOffset(Point.ORIGIN, 0, startHalfWidth);
        Point spb = CoordinateUtils.pointOffset(Point.ORIGIN, 0, -startHalfWidth);
        int endHalfWidth = Math.round(end.wide/2);
        Point ept = CoordinateUtils.pointOffset(endPoint, 0, endHalfWidth);
        Point epb = CoordinateUtils.pointOffset(endPoint, 0, -endHalfWidth);
        Point[] points = new Point[]{spt, spb, epb, ept};
        ShapeInfo.Shape shape = new ShapeInfo.Shape(-1, points);
        return new ShapeInfo(shape, degree);
    }


    @Override
    protected void calculateBounds() {
        int boundsMinX = Integer.MAX_VALUE;
        int boundsMinY = Integer.MAX_VALUE;
        int boundsMaxX = Integer.MIN_VALUE;
        int boundsMaxY = Integer.MIN_VALUE;
        /* 粗略计算 */
        for (int i = 0; i < wayNodes.length; i++) {
            int halfRound = Math.round(wayNodes[i].wide/2);
            System.out.println(i + " halfRound: " + halfRound);
            boundsMinX = Math.min(boundsMinX, wayNodes[i].x-halfRound);
            boundsMaxX = Math.max(boundsMaxX, wayNodes[i].x+halfRound);
            boundsMinY = Math.min(boundsMinY, wayNodes[i].y-halfRound);
            boundsMaxY = Math.max(boundsMaxY, wayNodes[i].y+halfRound);
            System.out.println(boundsMinX + " " + boundsMaxX + " " + boundsMinY + " " + boundsMaxY);
        }
        bounds = new Rect(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY);
    }
}
