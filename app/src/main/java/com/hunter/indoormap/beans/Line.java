package com.hunter.indoormap.beans;

import com.hunter.indoormap.MathUtils;

/**
 * Created by hunter on 5/8/17.
 */

public class Line<P extends GPoint> {
    private P start;
    private P end;

    private MaxMin maxMin;

    public Line(P start, P end) {
        this.start = start;
        this.end = end;
    }

    public P getStart() {
        return start;
    }

    public void setStart(P start) {
        this.start = start;
        maxMin = null;
    }

    public P getEnd() {
        return end;
    }

    public void setEnd(P end) {
        this.end = end;
        maxMin = null;
    }

    private void createMaxMin() {
        maxMin = new MaxMin();
        maxMin.maxX = start.x > end.x ? start.x : end.x;
        maxMin.minX = start.x < end.x ? start.x : end.x;
        maxMin.maxY = start.y > end.y ? start.y : end.y;
        maxMin.minY = start.y < end.y ? start.y : end.y;
        maxMin.maxZ = start.z > end.z ? start.z : end.z;
        maxMin.minZ = start.z < end.z ? start.z : end.z;
    }

    public boolean contains(GPoint e) {
        if (e == null) {
            throw new NullPointerException();
        }
        if (start.equals(e) || end.equals(e)) {
            return true;
        }
        if (maxMin == null) {
            createMaxMin();
        }
        if (maxMin.contains(e)) {
            // test X Y
            if (!MathUtils.isEqual((e.x-start.x)*(end.y-start.y), (end.x-start.x)*(e.y-start.y))) {
                return false;
            }
            // test Y Z
            return MathUtils.isEqual((e.z-start.z)*(end.y-start.y), (end.z-start.z)*(e.y-start.y));
        }
        return false;
    }

    @Override
    public String toString() {
        return "Line{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

    class MaxMin {
        float maxX;
        float minX;
        float maxY;
        float minY;
        float maxZ;
        float minZ;

        boolean contains(GPoint e) {
            return e.x >= minX && e.x <= maxX && e.y >= minY && e.y <= maxY && e.z >=minZ && e.z <= maxZ;
        }
    }
}
