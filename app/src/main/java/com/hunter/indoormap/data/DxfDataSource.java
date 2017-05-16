package com.hunter.indoormap.data;

import android.support.annotation.NonNull;

import com.hunter.indoormap.CoordinateUtils;
import com.hunter.indoormap.Log;
import com.hunter.indoormap.beans.Edges;
import com.hunter.indoormap.beans.Floor;
import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.ID;
import com.hunter.indoormap.beans.Line;
import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Point;
import com.hunter.indoormap.beans.Way;
import com.hunter.indoormap.route.ARouterDataSource;
import com.hunter.indoormap.route.ArbitraryRouterDataSource;
import com.hunter.indoormap.route.RouterDataSource;

import static com.hunter.indoormap.beans.Way.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hunter on 5/10/17.
 */

public class DxfDataSource extends FileDataSource implements ArbitraryRouterDataSource{
    public static final String TAG = DxfDataSource.class.getSimpleName();

    public static final String DATAFILE_ENCODING = "UTF-8";

    private File dataFile;
    private InputStream inputStream;
    private ARouterDataSource aRouterDataSource;
    private boolean switchAxes = true;  // switch between "left-handed set of axes" and "right-handed set of axes"

    public DxfDataSource(File dataFile) {
        if (dataFile == null || dataFile.isDirectory() || !dataFile.canRead()) {
//            throw new IllegalArgumentException("Invalid file: " + dataFile);
            Log.o("Invalid file: " + dataFile);
        }
        this.dataFile = dataFile;
    }

    public DxfDataSource(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream Mustn't be NULL");
        }
        this.inputStream = inputStream;
    }

    private LineIterator lineIterator;

    List<Node> nodes;
    Map<Integer, Edges> edges;
    Map<Integer, Way> ways;
    int wayID = Integer.MIN_VALUE;

    public boolean parseDxf() {
        boolean ok = false;
        try {
            if (inputStream == null) {
                lineIterator = new LineIterator(dataFile);
            } else {
                lineIterator = new LineIterator(inputStream);
            }
            nodes = new LinkedList<>();
            edges = new LinkedHashMap<>();
            ways = new LinkedHashMap<>();

            doParseDxf();
            lineIterator.close();
            packageNode();
            packageWay();
            if (!nodes.isEmpty() || !edges.isEmpty() || !ways.isEmpty()) {
                ok = true;
            }
            // clear unused data
            nodes = null;
            edges = null;
            ways = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ok;
    }

    private static final String DXF_CODE_0 = "0";
    private static final String DXF_CODE_X = "10";
    private static final String DXF_CODE_Y = "20";
    private static final String DXF_CODE_Z = "30";
    private static final String DXF_CODE_X2 = "11";
    private static final String DXF_CODE_Y2 = "21";
    private static final String DXF_CODE_Z2 = "31";
    private static final String DXF_CODE_PROPERTY = "1000";

    private static final String DXF_PROPERTY_DELIMITER = ":";

    private static final String DXF_CNT_POINT = "POINT";
    private static final String DXF_CNT_LINE = "LINE";

    private final DxfUnit UNIT_POINT = new DxfUnit(DXF_CODE_0, DXF_CNT_POINT);
    private final DxfUnit UNIT_LINE = new DxfUnit(DXF_CODE_0, DXF_CNT_LINE);

    private final String PROPERTY_ID = "ID";
    private final String PROPERTY_NAME = "NAME";
    private final String PROPERTY_POINT_ID = "POINTID";
    private final String PROPERTY_START_WIDTH = "STARTWIDTH";
    private final String PROPERTY_END_WIDTH = "ENDWIDTH";

    private void doParseDxf() {
        DxfUnit dxfUnit;
        while ((dxfUnit = nextDxfUnit()) != null) {
//            Log.o(dxfUnit.toString());
            if (UNIT_LINE.equals(dxfUnit)) {
                parseLine();
            } else if (UNIT_POINT.equals(dxfUnit)) {
                parsePoint();
            }
        }
    }

    private void packageWay() {
        List<WayLine> allWayLines = new LinkedList<>();
        for (Way way : ways.values()) {
            List<WayLine> wayLines = (List<WayLine>) way.getTag();
            if (wayLines == null || wayLines.size() == 0){
                //unreachable
                continue;
            }
            allWayLines.addAll(wayLines);
            // add wayline into way
            way.setWayLines(wayLines.toArray(new WayLine[wayLines.size()]));
            way.setTag(null);
            //commit way
            Set<Integer> floors = new HashSet<>();
            for (WayLine wayLine : wayLines) {
                if (wayLine.getStart().z == wayLine.getEnd().z) {
                    floors.add(wayLine.getStart().z);
                }
            }
            for (Integer floor : floors) {
                commitWay(floor, way);
            }
        }
        // commit allWayLines to ARouterDataSource
        aRouterDataSource = new ARouterDataSource(allWayLines);
    }

    private void commitWay(Integer floor, Way way) {
        LinkedList<Way> waylist = super.ways.get(floor);
        if (waylist == null) {
            waylist = new LinkedList<>();
            super.ways.put(floor, waylist);
        }
        waylist.add(way);
    }

    private void packageNode() {
        for (Node node : nodes) {
            if (node.getId() != ID.NONE_ID) {
                Edges edge = edges.get(node.getId());
                if (edge != null) {
                    if (packageEdges(edge)) {
                        node.setEdges(edge);
                        edge.setTag(null);
                    } else {
                        Log.e(TAG, "packageEdges failed: " + edge);
                    }
                } else {
                    Log.e(TAG, node + " doesn't have edges");
                }
            }
            // commit node
            commitNode(node);
        }
    }

    private void commitNode(Node node) {
        LinkedList<Node> nodeList = super.nodes.get(node.getXyz().z);
        if (nodeList == null) {
            nodeList = new LinkedList<>();
            super.nodes.put(node.getXyz().z, nodeList);
        }
        nodeList.add(node);
    }

    private boolean packageEdges(Edges edge) {
        List<WayLine> wayLines = (LinkedList<WayLine>) edge.getTag();
        if (wayLines == null || wayLines.size() < 3) {
            return false;
        }
        wayLines = Collections.unmodifiableList(wayLines);
        List<GPoint> gPoints = new ArrayList<>(wayLines.size());
        GPoint prev = wayLines.get(0).getStart();
        GPoint cur = wayLines.get(0).getEnd();
        GPoint next;
        final GPoint first = prev;
        gPoints.add(prev);
        while ((next = findNextPoint(prev, cur, wayLines)) != null) {
            gPoints.add(cur);
            if (next.equals(first)) {
                Point[] points = new Point[gPoints.size()];
                int i = 0;
                for (GPoint gPoint : gPoints) {
                    points[i++] = new Point(gPoint.x, gPoint.y);
                }
                edge.setPoints(points);
                return true;
            }
            prev = cur;
            cur = next;
        }
        return false; // next is null, means wayLines is not closed.
    }

    private GPoint findNextPoint(GPoint prev, GPoint cur, List<WayLine> wayLines) {
        WayNode next = null;
        for (WayLine wayLine : wayLines) {
            if (wayLine.getStart().equals(cur) && !wayLine.getEnd().equals(prev)) {
                next = wayLine.getEnd();
                break;
            } else if (wayLine.getEnd().equals(cur) && !wayLine.getStart().equals(prev)) {
                next =  wayLine.getStart();
                break;
            }
        }
//        Log.o("findNextPoint " + prev + " " + cur + " " + next);
        return next;
    }


    /**
     *
     * @param property
     * @return a string array with length = 2 or null
     */
    private String[] parseProperty(String property) {
        int index = property.indexOf(DXF_PROPERTY_DELIMITER);
        if (index <= 0 || index == property.length()-1) {
            return null;
        }
        String[] ss = new String[2];
        ss[0] = property.substring(0, index);
        ss[1] = property.substring(index+DXF_PROPERTY_DELIMITER.length());
        return ss;
    }

    private void parsePoint() {
        Node node = new Node(ID.NONE_ID, new GPoint(0f, 0f, 0));
        DxfUnit dxfUnit;
        while (!isEnd() && (dxfUnit = nextDxfUnit()) != null) {
            if (DXF_CODE_PROPERTY.equals(dxfUnit.code)) {
                String[] ss = parseProperty(dxfUnit.content);
                if (ss != null) {
                    if (PROPERTY_ID.equals(ss[0])) {
                        node.setId(Integer.parseInt(ss[1]));
                    } else if (PROPERTY_NAME.equals(ss[0])) {
                        node.setName(ss[1]);
                    }
                }
            } else if (DXF_CODE_X.equals(dxfUnit.code)) {
                node.getXyz().x = Float.parseFloat(dxfUnit.content);
            } else if (DXF_CODE_Y.equals(dxfUnit.code)) {
                node.getXyz().y = switchAxes ? -Float.parseFloat(dxfUnit.content) : Float.parseFloat(dxfUnit.content);
            } else if (DXF_CODE_Z.equals(dxfUnit.code)) {
                node.getXyz().z = Math.round(Float.parseFloat(dxfUnit.content));
            }
        }
        nodes.add(node);
    }

    private Edges getEdges(Integer id) {
        Edges edge = edges.get(id);
        if (edge == null) {
            edge = new Edges(null);
            edges.put(id, edge);
        }
        return edge;
    }

    private void addWayLine2Edge(Edges edge, WayLine wayLine) {
        List<WayLine> wayLines = (List<WayLine>) edge.getTag();
        if (wayLines == null) {
            wayLines = new LinkedList<>();
            edge.setTag(wayLines);
        }
        wayLines.add(wayLine);
    }

    private Way getWay(Integer id) {
        Way way = ways.get(id);
        if (way == null) {
            way = new Way(null);
            ways.put(id, way);
        }
        return way;
    }

    private void addWayLine2Way(Way way, WayLine wayLine) {
        List<WayLine> wayLines = (List<WayLine>) way.getTag();
        if (wayLines == null) {
            wayLines = new LinkedList<>();
            way.setTag(wayLines);
        }
        wayLines.add(wayLine);
    }

    private void parseLine() {
        WayLine wayLine = new WayLine(new WayNode(0f, 0f, 0), new WayNode(0f, 0f, 0));
        String wayName = null;
        int pointId = ID.NONE_ID;
        int id = ID.NONE_ID;
        DxfUnit dxfUnit;
        while (!isEnd() && (dxfUnit = nextDxfUnit()) != null) {
            if (DXF_CODE_PROPERTY.equals(dxfUnit.code)) {
                String[] ss = parseProperty(dxfUnit.content);
                if (ss != null) {
                    if (PROPERTY_ID.equals(ss[0])) {
                        id = Integer.parseInt(ss[1]);
                    } else if (PROPERTY_NAME.equals(ss[0])) {
                        wayName = ss[1];
                    } else if(PROPERTY_POINT_ID.equals(ss[0])) {
                        pointId = Integer.parseInt(ss[1]);
                    } else if (PROPERTY_START_WIDTH.equals(ss[0])) {
                        wayLine.getStart().setWide(Float.parseFloat(ss[1]));
                    } else if (PROPERTY_END_WIDTH.equals(ss[0])) {
                        wayLine.getEnd().setWide(Float.parseFloat(ss[1]));
                    }
                }
            } else if (DXF_CODE_X.equals(dxfUnit.code)) {
                wayLine.getStart().x = Float.parseFloat(dxfUnit.content);
            } else if (DXF_CODE_Y.equals(dxfUnit.code)) {
                wayLine.getStart().y = switchAxes ? -Float.parseFloat(dxfUnit.content) : Float.parseFloat(dxfUnit.content);
            } else if (DXF_CODE_Z.equals(dxfUnit.code)) {
                wayLine.getStart().z = Math.round(Float.parseFloat(dxfUnit.content));
            } else if (DXF_CODE_X2.equals(dxfUnit.code)) {
                wayLine.getEnd().x = Float.parseFloat(dxfUnit.content);
            } else if (DXF_CODE_Y2.equals(dxfUnit.code)) {
                wayLine.getEnd().y = switchAxes ? -Float.parseFloat(dxfUnit.content) : Float.parseFloat(dxfUnit.content);
            } else if (DXF_CODE_Z2.equals(dxfUnit.code)) {
                wayLine.getEnd().z = Math.round(Float.parseFloat(dxfUnit.content));
            }
        }
        if (new GPoint(wayLine.getStart()).equals(wayLine.getEnd())) {
            Log.e(TAG, "wayLine have the same Start and End, Ignored: " + wayLine);
            return;
        }
        if (pointId == ID.NONE_ID) {    //Way
            Way way;
            if (id == ID.NONE_ID) {     // doesn't have id
                way = new Way(null);
                while (ways.containsKey(wayID)) {
                    wayID++;
                }
                ways.put(wayID++, way);
            } else {
                way = getWay(id);
            }
            way.setName(wayName);
            addWayLine2Way(way, wayLine);
        } else {    //Edges
            addWayLine2Edge(getEdges(pointId), wayLine);
        }
//        Log.o("parseLine " + wayLine);
    }

    private boolean isEnd() {
        return lineIterator.hasNext() ? DXF_CODE_0.equals(lineIterator.peepNextLine().trim()) : true;
    }

    public boolean isSwitchAxes() {
        return switchAxes;
    }

    public void setSwitchAxes(boolean switchAxes) {
        this.switchAxes = switchAxes;
    }

    DxfUnit nextDxfUnit() {
        DxfUnit dxfUnit = new DxfUnit();
        if (lineIterator.hasNext()) {
            dxfUnit.code = lineIterator.next().trim();
            if (lineIterator.hasNext()) {
                dxfUnit.content = lineIterator.next().trim();
            } else {
                return null;
            }
            return dxfUnit;
        } else {
            return null;
        }
    }

    @Override
    protected boolean actualLoadData() {
        return parseDxf();
    }

    @Override
    public Wnode getWnode(GPoint gPoint) {
        return aRouterDataSource.getWnode(gPoint);
    }

    @Override
    @NonNull
    public GPoint[] getNearestPoint(@NonNull GPoint src) {
        Floor[] floors = getFloors(src.z);
        if (floors.length == 0 || !floors[0].getBounds().contains(src)) return new GPoint[0];
        GPoint[] gPoints = aRouterDataSource.getNearestPoint(src);
        if (gPoints.length == 0) {
            // find nearest node
            List<Node> zNodes = super.nodes.get(src.z);
            if (zNodes != null) {
                for (Node node : zNodes) {
                    //TODO May multi nodes contains a same src
                    if (node.contains(src)) {
                        return new GPoint[]{node.getXyz()};
                    }
                }
            }
        }
        return gPoints;
    }

    class DxfUnit {
        String code;
        String content;

        public DxfUnit() {
        }

        public DxfUnit(String code, String content) {
            this.code = code;
            this.content = content;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof DxfUnit)) {
                return false;
            }
            DxfUnit other = (DxfUnit) obj;
            return code.equals(other.code) && content.equals(other.content);
        }

        @Override
        public String toString() {
            return "DxfUnit{" +
                    "code='" + code + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

}
