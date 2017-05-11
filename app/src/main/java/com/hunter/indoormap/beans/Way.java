package com.hunter.indoormap.beans;

import java.util.Arrays;

/**
 * Created by hunter on 3/25/17.
 */

public class Way extends MObj {

    public static class WayNode extends GPoint{

        float wide;

        public WayNode(GPoint gPoint, float wide) {
            this(gPoint.x, gPoint.y, gPoint.z, wide);
        }

        public WayNode(float x, float y, float wide) {
            this(x, y, 0, wide);
        }

        public WayNode(float x, float y, int z, float wide) {
            super(x, y, z);
            this.wide = wide;
        }

        public float getWide() {
            return wide;
        }

        public void setWide(float wide) {
            this.wide = wide;
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

    public static class WayLine extends Line<WayNode> {

        private Edges edges;

        public WayLine(WayNode start, WayNode end) {
            super(start, end);
        }

        @Override
        public void setStart(WayNode start) {
            super.setStart(start);
            edges = null;
        }

        @Override
        public void setEnd(WayNode end) {
            super.setEnd(end);
            edges = null;
        }

        public boolean contains(Point p) {
            return contains(new GPoint(p.x, p.y));
        }

        // just consider 2D
        @Override
        public boolean contains(GPoint gPoint) {
            if (edges == null) {
                calculateEdges(getStart(), getEnd());
            }
            return edges.contains(gPoint);
        }

        private void calculateEdges(WayNode start, WayNode end) {
            //TODO implements
            /*
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
            shapeInfo = new ShapeInfo(shape, degree);*/
        }


    }

    private WayLine[] wayLines;

    public Way(WayLine[] wayLines) {
        this(ID.NONE_ID, null, wayLines);
    }

    public Way(int id, WayLine[] wayLines) {
        this(id, null, wayLines);
    }

    public Way(int id, String name, WayLine[] wayLines) {
        super(id, name);
        setWayLines(wayLines);
    }

    public WayLine[] getWayLines() {
        return wayLines;
    }

    public void setWayLines(WayLine[] wayLines) {
        this.wayLines = wayLines;
        bounds = null;
    }

    @Override
    protected boolean ifContains(Point point) {
        for (int i=0; i < wayLines.length; i++) {
            if (wayLines[i].contains(point)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void calculateBounds() {
        int boundsMinX = Integer.MAX_VALUE;
        int boundsMinY = Integer.MAX_VALUE;
        int boundsMaxX = Integer.MIN_VALUE;
        int boundsMaxY = Integer.MIN_VALUE;

        /* 粗略计算 */
        for (int i = 0; i < wayLines.length; i++) {
            for(WayNode wayNode : new WayNode[]{wayLines[i].getStart(), wayLines[i].getEnd()}) {
                int halfRound = Math.round(wayNode.wide/2);
                boundsMinX = (int) Math.min(boundsMinX, wayNode.x-halfRound);
                boundsMaxX = (int) Math.max(boundsMaxX, wayNode.x+halfRound);
                boundsMinY = (int) Math.min(boundsMinY, wayNode.y-halfRound);
                boundsMaxY = (int) Math.max(boundsMaxY, wayNode.y+halfRound);
            }
        }
        bounds = new Rect(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY);
    }

    @Override
    public String toString() {
        return "Way{" +
                "id=" + id +
                ", name=" + name +
                ", wayLines=" + Arrays.toString(wayLines) +
                '}';
    }
}
