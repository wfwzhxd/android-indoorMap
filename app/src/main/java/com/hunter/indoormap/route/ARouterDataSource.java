package com.hunter.indoormap.route;

import android.support.annotation.NonNull;

import com.hunter.indoormap.Log;
import com.hunter.indoormap.MathUtils;
import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Line;
import com.hunter.indoormap.beans.Rect;
import com.hunter.indoormap.beans.Shape;
import com.hunter.indoormap.beans.Way.WayLine;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by hunter on 5/6/17.
 */

public class ARouterDataSource implements ArbitraryRouterDataSource{

    private final Comparator<GPoint> xComparator = new Comparator<GPoint>() {
        @Override
        public int compare(GPoint o1, GPoint o2) {
            return MathUtils.isEqual(o1.x, o2.x)  ? 0 : (o1.x - o2.x > 0 ? 1 : -1);
        }
    };

    private final Comparator<GPoint> yComparator = new Comparator<GPoint>() {
        @Override
        public int compare(GPoint o1, GPoint o2) {
            return MathUtils.isEqual(o1.y, o2.y)  ? 0 : (o1.y - o2.y > 0 ? 1 : -1);
        }
    };

    private final Comparator<GPoint> gPointComparator = new Comparator<GPoint>() {
        @Override
        public int compare(GPoint o1, GPoint o2) {
            if (o1.x != o2.x) {
                return xComparator.compare(o1, o2);
            } else if (o1.y != o2.y) {
                return yComparator.compare(o1, o2);
            }
            return o1.z - o2.z;
        }
    };

    private ZXYIndexer<Wnode> zxyIndexer;

    Map<Integer, List<PointsLine>> horPointsLines = new LinkedHashMap<>();

    class PointsLine implements Shape<GPoint>{
        final WayLine line;
        final GPoint[] gPoints;

        public PointsLine(WayLine line, Set<GPoint> points) {
            this.line = line;
            gPoints = points.toArray(new GPoint[points.size()]);
            if (Math.abs(line.getStart().x-line.getEnd().x) > Math.abs(line.getStart().y-line.getEnd().y)) {
                Arrays.sort(gPoints, xComparator);
            } else {
                Arrays.sort(gPoints, yComparator);
            }
        }

        @Override
        public Rect getBounds() {
            return line.getBounds();
        }

        @Override
        public boolean contains(GPoint gPoint) {
            return line.contains(gPoint);
        }

        @NonNull
        public GPoint[] getNearestPoint(GPoint src) {
            if (src == null) return new GPoint[0];
            if (contains(src)) {
                Log.o(this + " do contains " + src);
                GPoint pedal = new GPoint(src);
                GPoint begin = line.getStart();
                GPoint end = line.getEnd();
                double dx = begin.x - end.x;
                double dy = begin.y - end.y;
                if(Math.abs(dx) < 0.00000001 && Math.abs(dy) < 0.00000001 )
                {
                    // unreachable
                    pedal = begin;
                } else {
                    double u = (src.x - begin.x)*(begin.x - end.x) +
                            (src.y - begin.y)*(begin.y - end.y);
                    u = u/((dx*dx)+(dy*dy));

                    pedal.x = (float) (begin.x + u*dx);
                    pedal.y = (float) (begin.y + u*dy);
                }
                dx = Math.abs(dx);
                dy = Math.abs(dy);
                if (dx > dy) {
                    for (int i=1; i<gPoints.length; i++) {
                        if ((xComparator.compare(gPoints[i-1], pedal) >=0 && xComparator.compare(gPoints[i], pedal) <=0) ||
                                (xComparator.compare(gPoints[i-1], pedal) <=0 && xComparator.compare(gPoints[i], pedal) >=0)) {
                            if (xComparator.compare(gPoints[i-1], pedal) == 0) {
                                return new GPoint[]{gPoints[i-1]};
                            } else if (xComparator.compare(gPoints[i], pedal) ==0) {
                                return new GPoint[]{gPoints[i]};
                            } else {
                                return new GPoint[]{gPoints[i-1], gPoints[i]};
                            }
                        } else {
                            Log.o("not found nearest in " + line + " for " + src);
                        }
                    }
                } else {
                    for (int i=1; i<gPoints.length; i++) {
                        if ((yComparator.compare(gPoints[i-1], pedal) >=0 && yComparator.compare(gPoints[i], pedal) <=0) ||
                                (yComparator.compare(gPoints[i-1], pedal) <=0 && yComparator.compare(gPoints[i], pedal) >=0)) {
                            if (yComparator.compare(gPoints[i-1], pedal) == 0) {
                                return new GPoint[]{gPoints[i-1]};
                            } else if (yComparator.compare(gPoints[i], pedal) ==0) {
                                return new GPoint[]{gPoints[i]};
                            } else {
                                return new GPoint[]{gPoints[i-1], gPoints[i]};
                            }
                        }
                    }
                }

            }
            Log.o(this + " not contains " + src);
            return new GPoint[0];
        }

        @Override
        public String toString() {
            return "PointsLine{" +
                    "line=" + line +
                    ", gPoints=" + Arrays.toString(gPoints) +
                    '}';
        }
    }

    public ARouterDataSource(List<? extends Line> lines) {
        zxyIndexer = new ZXYIndexer<>();
        if (lines != null) {
            sliceInitLines(lines);
        }
    }

    private void sliceInitLines(List<? extends Line> lines) {
        // organize by z
        Map<Integer, List<Line>> linesMap = new LinkedHashMap<>();
        List<Line> multiFloor = new LinkedList<>();
        for (Line line : lines) {
            if (line.getStart().z == line.getEnd().z) {
                addLine2Map(line.getStart().z, line, linesMap);
            } else {
                multiFloor.add(line);
            }
        }
        // add multiFloor line to horizontal lines
        for (Line line : multiFloor) {
            int maxZ = line.getStart().z < line.getEnd().z ? line.getEnd().z : line.getStart().z;
            int minZ = line.getStart().z > line.getEnd().z ? line.getEnd().z : line.getStart().z;
            for (Integer floor : linesMap.keySet()) {
                if (floor >= minZ && floor <= maxZ) {
                    linesMap.get(floor).add(line);
                }
            }
        }
        //slice multiFloors
        sliceLines(multiFloor);
        //slice horizontal lines
        for (List<Line> lineList : linesMap.values()) {
            if (lineList != null) {
                for (Line line : lineList) {
                    if (line.getStart().z != line.getEnd().z) continue;
                    Set<GPoint> gPoints = sliceLine(line, lineList);
                    commitLines(generateLines(gPoints));
                    if (line instanceof WayLine) {
                        WayLine wayLine = (WayLine) line;
                        if (wayLine.getStart().getWide() > 0 || wayLine.getEnd().getWide() > 0) {
                            // store
                            int z = wayLine.getStart().z;
                            List<PointsLine> pointsLines = horPointsLines.get(z);
                            if (pointsLines == null) {
                                pointsLines = new LinkedList<>();
                                horPointsLines.put(z, pointsLines);
                            }
                            pointsLines.add(new PointsLine(wayLine, gPoints));
                        }
                    }
                }
            }
        }
    }

    private void sliceLines(List<Line> lines) {
        if (lines != null) {
            for (Line line : lines) {
                commitLines(generateLines(sliceLine(line, lines)));
            }
        }
    }

    /**
     *
     * @param points    All GPoints in the set MUST on a same LINE.
     * @return
     */
    private List<Line>  generateLines(Set<GPoint> points) {
        List<Line> lines = new LinkedList<>();
        if (points != null && points.size() > 1) {
            GPoint[] gPoints = points.toArray(new GPoint[points.size()]);
            Arrays.sort(gPoints, gPointComparator);
            for (int i=1; i<gPoints.length; i++) {
                lines.add(new Line<>(gPoints[i-1], gPoints[i]));
            }
        }
        return lines;
    }

    /**
     * Slice the line use all line in lines.
     * @param slicedLine
     * @param lines
     * @return  a Set which contains ALL GPoint on this line(include start and end).
     */
    private Set<GPoint> sliceLine(Line slicedLine, List<Line> lines) {
        slicedLine = new Line(slicedLine.getStart(), slicedLine.getEnd());
        Set<GPoint> points = new LinkedHashSet<>();
        points.add(slicedLine.getStart());
        points.add(slicedLine.getEnd());
        if (lines != null) {
            for (Line line : lines) {
                if (slicedLine.contains(line.getStart()) && !slicedLine.isStartOrEnd(line.getStart())) {
                    points.add(line.getStart());
                } else if (slicedLine.contains(line.getEnd()) && !slicedLine.isStartOrEnd(line.getEnd())) {
                    points.add(line.getEnd());
                }
            }
        }
        return points;
    }

    private void addLine2Map(Integer floor, Line line, Map<Integer, List<Line>> linesMap) {
        List<Line> lineList = linesMap.get(floor);
        if (lineList == null) {
            lineList = new LinkedList<>();
            linesMap.put(floor, lineList);
        }
        lineList.add(line);
    }

    private void commitLines(Collection<Line> lines) {
        if (lines != null) {
            for (Line line : lines) {
                commitLine(line);
            }
        }
    }

    private void commitLine(Line line) {
        Wnode<GPoint> startWnode = getWnode(line.getStart());
        Wnode<GPoint> endWnode = getWnode(line.getEnd());
        if (startWnode == null) {
            startWnode = new Wnode<>(line.getStart());
            addWnode(startWnode);
        }
        if (endWnode == null) {
            endWnode = new Wnode<>(line.getEnd());
            addWnode(endWnode);
        }
        startWnode.addNext(endWnode);
        endWnode.addNext(startWnode);
    }

    public void addWnode(Wnode node) {
        zxyIndexer.addZXY(node, node.getItem());
    }

    public Wnode getWnode(GPoint gPoint) {
        return zxyIndexer.getZXY(gPoint);
    }

    @Override
    @NonNull
    public GPoint[] getNearestPoint(@NonNull GPoint src) {
        List<PointsLine> pointsLines = horPointsLines.get(src.z);
        if (pointsLines == null) return new GPoint[0];
        GPoint [] gPoints;
        for (PointsLine pointsLine : pointsLines) {
            gPoints = pointsLine.getNearestPoint(src);
            if (gPoints.length != 0) {
                return gPoints;
            }
        }
        return new GPoint[0];
    }

    public static class ZXYIndexer<T> {

        private static final Comparator<Float> floatComparator = new Comparator<Float>() {
            @Override
            public int compare(Float o1, Float o2) {
                if (MathUtils.isEqual(o1, o2)) {
                    return 0;
                }
                return o1-o2 < 0 ? -1 : 1;
            }
        };

        // z , x, y
        private TreeMap<Integer, TreeMap<Float, TreeMap<Float, T>>> ts;

        public ZXYIndexer() {
            ts = new TreeMap<>();
        }

        public void addZXY(T t, GPoint gPoint) {
            if (t == null || gPoint == null) {
                throw new NullPointerException();
            }
            TreeMap<Float, TreeMap<Float, T>> zn = ts.get(gPoint.z);
            if (zn == null) {
                zn = new TreeMap<>(floatComparator);
                ts.put(gPoint.z, zn);
            }
            TreeMap<Float, T> xn = zn.get(gPoint.x);
            if (xn == null) {
                xn = new TreeMap<>(floatComparator);
                zn.put(gPoint.x, xn);
            }
            xn.put(gPoint.y, t);
        }

        public T getZXY(GPoint gPoint) {
            if (gPoint == null) {
                return null;
            }
            TreeMap<Float, TreeMap<Float, T>> zn = ts.get(gPoint.z);
            if (zn == null) {
                return null;
            }
            TreeMap<Float, T> xn = zn.get(gPoint.x);
            if (xn == null) {
                return null;
            }
            return xn.get(gPoint.y);
        }

    }
}
