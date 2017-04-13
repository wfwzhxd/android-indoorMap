package com.hunter.indoormap.beans;

import com.hunter.indoormap.CoordinateUtils;

import java.util.Arrays;

/**
 * Created by hunter on 3/25/17.
 */

public class Way extends MObj {
    /**
     *  道路中用到的 Node 中的 Shape 使用一种特殊的 Shape, 此 Shape 中
     *  只有两个 Node ， 两个 Node 之间的距离代表道路中当前 Node 的宽度。
     */
    Node[] nodes;
    boolean oneway = false;

    private ShapeInfo[] shapeInfos;

    public Way(int id, Node[] nodes) {
        this(id, null, nodes, false);
    }

    public Way(int id, String name, Node[] nodes) {
        this(id, name, nodes, false);
    }

    public Way(int id, String name, Node[] nodes, boolean oneway) {
        super(id, name);
        setNodes(nodes);
        this.oneway = oneway;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
        shapeInfos = new ShapeInfo[nodes.length>0?nodes.length-1:nodes.length];
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
            shapeInfos[i] = calculateShapeInfo(nodes[i], nodes[i+1]);
        }
        return shapeInfos[i];
    }

    @Override
    public boolean contains(Point point) {
        for (int i=0; i < nodes.length-1; i++) {
            point = CoordinateUtils.relativeCoord(nodes[i].xyz, point);
            if (getShapeInfo(i).contains(point)) {
                return true;
            }
        }
        return false;
    }

    private ShapeInfo calculateShapeInfo(Node start, Node end) {
        float degree = CoordinateUtils.calDegree(start.getXyz(), end.getXyz());
        System.out.println(start + " " + end + " degree " + degree);
        Point startPoint = start.getXyz();
        Point endPoint = CoordinateUtils.rotateAtPoint(startPoint, end.getXyz(), -degree, null);
        endPoint = CoordinateUtils.relativeCoord(start.getXyz(), endPoint);
        int startHalfWidth = Math.round(CoordinateUtils.calDistance(start.shapeInfo.getShape().points[0],start.shapeInfo.getShape().points[1])/2);
        Point spt = CoordinateUtils.pointOffset(Point.ORIGIN, 0, startHalfWidth);
        Point spb = CoordinateUtils.pointOffset(Point.ORIGIN, 0, -startHalfWidth);
        int endHalfWidth = Math.round(CoordinateUtils.calDistance(end.shapeInfo.getShape().points[0],end.shapeInfo.getShape().points[1])/2);
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
        for (int i = 0; i < nodes.length; i++) {
            int halfRound = Math.round(CoordinateUtils.calDistance(nodes[i].shapeInfo.getShape().points[0],nodes[i].shapeInfo.getShape().points[1])/2);
            System.out.println(i + " halfRound: " + halfRound);
            boundsMinX = Math.min(boundsMinX, nodes[i].xyz.x-halfRound);
            boundsMaxX = Math.max(boundsMaxX, nodes[i].xyz.x+halfRound);
            boundsMinY = Math.min(boundsMinY, nodes[i].xyz.y-halfRound);
            boundsMaxY = Math.max(boundsMaxY, nodes[i].xyz.y+halfRound);
            System.out.println(boundsMinX + " " + boundsMaxX + " " + boundsMinY + " " + boundsMaxY);
        }
        bounds = new Rect(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY);
    }
}
