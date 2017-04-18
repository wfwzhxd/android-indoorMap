package com.hunter.indoormap.data;

import android.content.res.AssetManager;

import com.hunter.indoormap.Log;

import com.hunter.indoormap.beans.FloorMap;
import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Point;
import com.hunter.indoormap.beans.ShapeInfo;
import com.hunter.indoormap.beans.Way;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by hunter on 4/15/17.
 */

public class TxtFileDataSource extends FileDataSource {

    public static final String TAG = TxtFileDataSource.class.getSimpleName();

    public static final String DATAFILE_SUFFIX = ".txt";

    public static final String DATAFILE_ENCODING = "UTF-8";

    public static final String SHAPE_PREFIX = "shape";

    public static final String NODE_PREFIX = "node";

    public static final String WAY_PREFIX = "way";

    public static final String FLOOR_PREFIX = "floor";

    public static final String FLOOR_SEPARATOR = "_";

    public static final String IGNORE_PREFIX = "#";

    public static final String ONE_LEVEL_SEPARATOR = "\\|";

    public static final String TWO_LEVEL_SEPARATOR = ";";

    public static final String THREE_LEVEL_SEPARATOR = ",";


    private File dataDir;

    private AssetManager assetManager;
    private String dataDir2;

    private Set idSet;
    private NBitSet zSet;

    public TxtFileDataSource(File dataDir) {
        if (dataDir == null || !dataDir.isDirectory()) {
            throw new IllegalArgumentException("dataDir MUST be a valid directory");
        }
        this.dataDir = dataDir;
        loadData();
    }

    public TxtFileDataSource(AssetManager assetManager, String dataDir) {
        if (assetManager == null) {
            throw new NullPointerException("AsserManager is Null");
        }
        this.assetManager = assetManager;
        this.dataDir2 = dataDir;
        loadData();
    }

    @Override
    protected boolean actualLoadData() {
        idSet = new LinkedHashSet();
        zSet = new NBitSet();
        boolean result =  loadShapes() && loadFloors() && loadWays() && loadNodes();
        Log.o("\nshapes :\n" + shapes.toString());
        Log.o("\nfloorMaps :\n" + floorMaps.toString());
        Log.o("\nnodes :\n" + nodes.toString());
        Log.o("\nways :\n" + ways.toString());
        idSet = null;
        zSet = null;
        if (result) {
            this.assetManager = null;
        }
        return result;
    }

    private Integer detectFloor(String filename) {
        int index = filename.lastIndexOf(FLOOR_SEPARATOR);
        Integer floor = null;
        try {
            floor = s2i(filename.substring(index+1, filename.length()-4));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (index == -1 || floor == null) {
            Log.e(TAG, "Not a valid file name{ " + filename + " }");
            return null;
        }
        if (!zSet.get(floor)) {
            Log.e(TAG, "Not found floor{ " + floor + "} file name{ " + filename + " }");
            return null;
        }
        return floor;
    }

    private boolean loadNodes() {
        for (File file : getFiles(NODE_PREFIX)) {
            Integer floor = detectFloor(file.getName());
            if (floor == null) {
                return false;
            }
            LineIterator lineIterator = null;
            Node node;
            try {
                lineIterator = createLineIterator(file);
                String line;
                while (lineIterator.hasNext()) {
                    line = lineIterator.next();
                    node = parseLine2Node(line);
                    if (node != null) {
                        if (!idSet.add(Integer.valueOf(node.getId()))) {
                            Log.e(TAG, "Multi Nodes have the same ID(" + node.getId() + ")");
                        }
                        nodes.get(floor).add(node);
                        continue;
                    }
                    Log.e(TAG, false + "\t" + line);
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (lineIterator != null) {
                    lineIterator.close();
                }
            }
        }
        return true;
    }

    private Node parseLine2Node(String line) {
        String[] oneLevel = line.split(ONE_LEVEL_SEPARATOR);
        if (oneLevel.length != 6) {
            return null;
        }
        int id = s2i(oneLevel[0]);
        String name = oneLevel[1].trim();
        boolean shown = isTrue(oneLevel[2]);
        GPoint gPoint = parseGPoint(oneLevel[3]);
        if (gPoint == null) {
            return null;
        }
        int shapeId = s2i(oneLevel[4]);
        ShapeInfo.Shape shape = shapes.get(Integer.valueOf(shapeId));
        if (shape == null) {
            Log.e(TAG, "The Shape with ID{ " + shapeId + " } not found");
            return null;
        }
        float degree = s2f(oneLevel[5]);
        return new Node(id, name, gPoint, new ShapeInfo(shape, degree));
    }

    private boolean loadWays() {
        for (File file : getFiles(WAY_PREFIX)) {
            Integer floor = detectFloor(file.getName());
            if (floor == null) {
                return false;
            }
            LineIterator lineIterator = null;
            Way way;
            try {
                lineIterator = createLineIterator(file);
                String line;
                while (lineIterator.hasNext()) {
                    line = lineIterator.next();
                    way = parseLine2Way(line);
                    if (way != null) {
                        if (!idSet.add(Integer.valueOf(way.getId()))) {
                            Log.e(TAG, "Multi Ways have the same ID(" + way.getId() + ")");
                        }
                        ways.get(floor).add(way);
                        continue;
                    }
                    Log.e(TAG, false + "\t" + line);
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (lineIterator != null) {
                    lineIterator.close();
                }
            }
        }
        return true;
    }

    private Way parseLine2Way(String line) {
        String[] oneLevel = line.split(ONE_LEVEL_SEPARATOR);
        if (oneLevel.length != 5) {
            return null;
        }
        int id = s2i(oneLevel[0]);
        String name = oneLevel[1].trim();
        boolean shown = isTrue(oneLevel[2]);
        boolean oneWay = isTrue(oneLevel[3]);
        Way.WayNode[] wayNodes = parseWayNodes(oneLevel[4]);
        if (wayNodes != null && wayNodes.length > 1) {
            return new Way(id, name, shown, oneWay, wayNodes);
        }
        return null;
    }

    private Way.WayNode[] parseWayNodes(String line){
        String[] twoLevel = line.split(TWO_LEVEL_SEPARATOR);
        ArrayList<Way.WayNode> wayNodes = new ArrayList<>(twoLevel.length);
        Way.WayNode wayNode = null;
        for (String l : twoLevel) {
            wayNode = parseWayNode(l);
            if (wayNode != null) {
                wayNodes.add(wayNode);
                continue;
            }
            return null;
        }
        return wayNodes.toArray(new Way.WayNode[wayNodes.size()]);
    }

    private Way.WayNode parseWayNode(String line){
        String[] threeLevel = line.split(THREE_LEVEL_SEPARATOR);
        if (threeLevel.length != 4) {
            return null;
        }
        return new Way.WayNode(s2i(threeLevel[0]), s2i(threeLevel[1]), s2i(threeLevel[2]), s2f(threeLevel[3]));
    }

    private boolean loadFloors() {
        for (File file : getFiles(FLOOR_PREFIX)) {
            LineIterator lineIterator = null;
            FloorMap floorMap;
            try {
                lineIterator = createLineIterator(file);
                String line;
                while (lineIterator.hasNext()) {
                    line = lineIterator.next();
                    floorMap = parseLine2FloorMap(line);
                    if (floorMap != null) {
                        if (!idSet.add(Integer.valueOf(floorMap.getId()))) {
                            Log.e(TAG, "Multi FloorMaps have the same ID(" + floorMap.getId() + ")");
                        }
                        if (zSet.get(floorMap.getZ())) {
                            Log.e(TAG, "Multi FloorMap have the same z(" + floorMap.getZ() + ")");
                        }
                        floorMaps.add(floorMap);
                        ways.put(Integer.valueOf(floorMap.getZ()), new LinkedList<Way>());
                        nodes.put(Integer.valueOf(floorMap.getZ()), new LinkedList<Node>());
                        zSet.set(floorMap.getZ());
                        continue;
                    }
                    Log.e(TAG, false + "\t" + line);
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (lineIterator != null) {
                    lineIterator.close();
                }
            }
        }
        return true;
    }

    private FloorMap parseLine2FloorMap(String line) {
        String[] oneLevel = line.split(ONE_LEVEL_SEPARATOR);
        if (oneLevel.length != 5) {
            return null;
        }
        int id = s2i(oneLevel[0]);
        String name = oneLevel[1].trim();
        int z = s2i(oneLevel[2]);
        int shapeId = s2i(oneLevel[3]);
        ShapeInfo.Shape shape = shapes.get(Integer.valueOf(shapeId));
        if (shape == null) {
            Log.e(TAG, "The Shape with ID{ " + shapeId + " } not found");
            return null;
        }
        float degree = s2f(oneLevel[4]);
        return new FloorMap(id, name, z, new ShapeInfo(shape, degree));
    }

    private boolean loadShapes() {
        for (File file : getFiles(SHAPE_PREFIX)) {
            LineIterator lineIterator = null;
            ShapeInfo.Shape shape;
            try {
                lineIterator = createLineIterator(file);
                String line;
                while (lineIterator.hasNext()) {
                    line = lineIterator.next();
                    shape = parseLine2Shape(line);
                    if (shape != null) {
                        if (!idSet.add(Integer.valueOf(shape.getId()))) {
                            Log.e(TAG, "Multi Shapes have the same ID(" + shape.getId() + ")");
                        }
                        shapes.put(shape.getId(), shape);
                        continue;
                    }
                    Log.e(TAG, false + "\t" + line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (lineIterator != null) {
                    lineIterator.close();
                }
            }
        }
        return true;
    }

    private ShapeInfo.Shape parseLine2Shape(String line) {
        String[] oneLevel = line.split(ONE_LEVEL_SEPARATOR);
        if (oneLevel.length != 2) {
            Log.e(TAG, "Error formate line{ " + line + " }, oneLevel.length=" + oneLevel.length);
            return null;
        }
        int id = s2i(oneLevel[0]);
        Point[] points = parsePoints(oneLevel[1]);
        if (points != null && points.length > 2) {
            return new ShapeInfo.Shape(id, points);
        }
        return null;
    }

    private Point[] parsePoints(String line) {
        String[] ps = line.split(TWO_LEVEL_SEPARATOR);
        ArrayList<Point> points = new ArrayList<>(ps.length);
        Point point;
        for (String p : ps) {
            point = parsePoint(p);
            if (point != null) {
                points.add(point);
            }
        }
        return points.toArray(new Point[points.size()]);
    }

    private Point parsePoint(String line) {
        String[] ps = line.split(THREE_LEVEL_SEPARATOR);
        Point p = null;
        if (ps.length == 2) {
            p = new Point(s2i(ps[0]), s2i(ps[1]));
        } else {
            Log.e(TAG, "Error formate point{ " + line + " }");
        }
        return p;
    }

    private GPoint[] parseGPoints(String line) {
        String[] ps = line.split(TWO_LEVEL_SEPARATOR);
        ArrayList<GPoint> gPoints = new ArrayList<>(ps.length);
        GPoint gPoint;
        for (String p : ps) {
            gPoint = parseGPoint(p);
            if (gPoint != null) {
                gPoints.add(gPoint);
            }
        }
        return gPoints.toArray(new GPoint[gPoints.size()]);
    }

    private GPoint parseGPoint(String line) {
        String[] ps = line.split(THREE_LEVEL_SEPARATOR);
        GPoint p = null;
        if (ps.length == 3) {
            p = new GPoint(s2i(ps[0]), s2i(ps[1]), s2i(ps[2]));
        }
        else {
            Log.e(TAG, "Error formate gpoint{ " + line + " }");
        }
        return p;
    }

    private int s2i(String s) {
        return Integer.parseInt(s.trim());
    }

    private float s2f(String s) {
        return Float.parseFloat(s.trim());
    }

    private boolean isTrue(String s) {
        return !(s2i(s) == 0);
    }

    private File[] getFiles(String prefix) {
        final String lowPrefix = prefix.toLowerCase();
        if (dataDir != null) {
            return dataDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    name = name.toLowerCase();
                    return name.endsWith(DATAFILE_SUFFIX) && name.startsWith(lowPrefix);
                }
            });
        }
        // From Assets
        try {
            List<File> files = new ArrayList<>();
            for (String filename : assetManager.list(dataDir2)) {
                filename = filename.toLowerCase();
                if (filename.endsWith(DATAFILE_SUFFIX) && filename.startsWith(lowPrefix)) {
                    files.add(new File(filename));
                }
            }
            return files.toArray(new File[files.size()]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File[0];
    }

    private LineIterator createLineIterator(File file) throws IOException {
        if (dataDir == null) {
            return new LineIterator(assetManager.open(dataDir2 + File.separator + file.getName(), AssetManager.ACCESS_BUFFER));
        }else {
            return new LineIterator(file);
        }
    }

    /**
     * Copy from Apache common-io project. http://commons.apache.org/proper/commons-io/javadocs/api-2.5/index.html
     */
    class LineIterator implements Iterator<String>, Closeable {

        // N.B. This class deliberately does not implement Iterable, see https://issues.apache.org/jira/browse/IO-181

        /** The reader that is being read. */
        private final BufferedReader bufferedReader;
        /** The current line. */
        private String cachedLine;
        /** A flag indicating if the iterator has been fully read. */
        private boolean finished = false;

        /**
         * Constructs an iterator of the lines for a <code>Reader</code>.
         *
         * @param reader the <code>Reader</code> to read from, not null
         * @throws IllegalArgumentException if the reader is null
         */
        public LineIterator(final Reader reader) throws IllegalArgumentException {
            if (reader == null) {
                throw new IllegalArgumentException("Reader must not be null");
            }
            if (reader instanceof BufferedReader) {
                bufferedReader = (BufferedReader) reader;
            } else {
                bufferedReader = new BufferedReader(reader);
            }
        }

        public LineIterator(File file) throws FileNotFoundException, UnsupportedEncodingException {
            this(new FileInputStream(file));
        }

        public LineIterator(InputStream inputStream) throws UnsupportedEncodingException {
            this(new InputStreamReader(inputStream, TxtFileDataSource.DATAFILE_ENCODING));
        }

        //-----------------------------------------------------------------------
        /**
         * Indicates whether the <code>Reader</code> has more lines.
         * If there is an <code>IOException</code> then {@link #close()} will
         * be called on this instance.
         *
         * @return {@code true} if the Reader has more lines
         * @throws IllegalStateException if an IO exception occurs
         */
        public boolean hasNext() {
            if (cachedLine != null) {
                return true;
            } else if (finished) {
                return false;
            } else {
                try {
                    while (true) {
                        final String line = bufferedReader.readLine();
                        if (line == null) {
                            finished = true;
                            return false;
                        } else if (isValidLine(line)) {
                            cachedLine = line;
                            return true;
                        }
                    }
                } catch(final IOException ioe) {
                    close();
                    throw new IllegalStateException(ioe);
                }
            }
        }


        protected boolean isValidLine(final String line) {
            return line.length() != 0 && !line.startsWith(TxtFileDataSource.IGNORE_PREFIX);
        }

        /**
         * Returns the next line in the wrapped <code>Reader</code>.
         *
         * @return the next line from the input
         * @throws NoSuchElementException if there is no line to return
         */
        public String next() {
            return nextLine();
        }

        /**
         * Returns the next line in the wrapped <code>Reader</code>.
         *
         * @return the next line from the input
         * @throws NoSuchElementException if there is no line to return
         */
        public String nextLine() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more lines");
            }
            final String currentLine = cachedLine;
            cachedLine = null;
            return currentLine;
        }

        /**
         * Closes the underlying <code>Reader</code> quietly.
         * This method is useful if you only want to process the first few
         * lines of a larger file. If you do not close the iterator
         * then the <code>Reader</code> remains open.
         * This method can safely be called multiple times.
         */
        public void close() {
            finished = true;
            if(bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cachedLine = null;
        }

        /**
         * Unsupported.
         *
         * @throws UnsupportedOperationException always
         */
        public void remove() {
            throw new UnsupportedOperationException("Remove unsupported on LineIterator");
        }

    }

    /**
     * Modified to adopt negative index.
     * Because we Just use set and get methods,
     * so just modify these two methods.
     */
    class NBitSet extends BitSet {
        final int ADJUE = 127;

        @Override
        public void set(int bitIndex) {
            super.set(bitIndex+ADJUE);
        }

        @Override
        public boolean get(int bitIndex) {
            return super.get(bitIndex+ADJUE);
        }
    }
}
