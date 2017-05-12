package com.hunter.indoormap.route;

import com.hunter.indoormap.MathUtils;
import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Line;

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

public class ARouterDataSource implements RouterDataSource{

    private ZXYIndexer<Wnode> zxyIndexer;

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
            sliceLines(lineList);
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
            Arrays.sort(gPoints, new Comparator<GPoint>() {
                @Override
                public int compare(GPoint o1, GPoint o2) {
                    if (o1.x != o2.x) {
                        return MathUtils.isEqual(o1.x, o2.x)  ? 0 : (o1.x - o2.x > 0 ? 1 : -1);
                    } else if (o1.y != o2.y) {
                        return MathUtils.isEqual(o1.y, o2.y)  ? 0 : (o1.y - o2.y > 0 ? 1 : -1);
                    }
                    return o1.z - o2.z;
                }
            });
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
