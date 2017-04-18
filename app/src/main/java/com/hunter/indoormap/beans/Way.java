package com.hunter.indoormap.beans;

import com.hunter.indoormap.CoordinateUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

        public float getWide() {
            return wide;
        }

        @Override
        public String toString() {
            return "WayNode{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    ", wide=" + wide +
                    '}';
        }
    }

    WayNode[] wayNodes;


    boolean oneway = false;

    private transient ShapeInfo[] shapeInfos;

    public Way(int id, WayNode[] wayNodes) {
        this(id, null, wayNodes);
    }

    public Way(int id, String name, WayNode[] wayNodes) {
        this(id, name, true, false, wayNodes);
    }

    public Way(int id, String name, boolean show, boolean oneway, WayNode[] wayNodes) {
        super(id, name, show);
        setWayNodes(wayNodes);
        this.oneway = oneway;
    }

    public WayNode[] getWayNodes() {
        return wayNodes;
    }

    public void setWayNodes(WayNode[] wayNodes) {
        this.wayNodes = wayNodes;
        generateShapeInfos();
        bounds = null;
    }

    private void generateShapeInfos() {
        shapeInfos = new ShapeInfo[wayNodes.length>0?wayNodes.length-1:0];
        for (int i=0; i < wayNodes.length-1; i++) {
            shapeInfos[i] = calculateShapeInfo(wayNodes[i], wayNodes[i+1]);
        }
    }

    public ShapeInfo[] getShapeInfos() {
        return shapeInfos;
    }

    public boolean isOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    @Override
    public boolean contains(Point point) {
        for (int i=0; i < wayNodes.length-1; i++) {
            point = CoordinateUtils.relativeCoord(wayNodes[i], point);
            if (shapeInfos[i].contains(point)) {
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
            boundsMinX = (int) Math.min(boundsMinX, wayNodes[i].x-halfRound);
            boundsMaxX = (int) Math.max(boundsMaxX, wayNodes[i].x+halfRound);
            boundsMinY = (int) Math.min(boundsMinY, wayNodes[i].y-halfRound);
            boundsMaxY = (int) Math.max(boundsMaxY, wayNodes[i].y+halfRound);
            System.out.println(boundsMinX + " " + boundsMaxX + " " + boundsMinY + " " + boundsMaxY);
        }
        bounds = new Rect(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY);
    }

    @Override
    public String toString() {
        return "Way{" +
                "id=" + id +
                ", name=" + name +
                ", show=" + show +
                ", wayNodes=" + Arrays.toString(wayNodes) +
                ", oneway=" + oneway +
                ", shapeInfos=" + shapeInfos +
                '}';
    }
}
