package com.hunter.indoormap.data;

import com.hunter.indoormap.beans.Floor;
import com.hunter.indoormap.beans.MObj;
import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Rect;
import com.hunter.indoormap.beans.Way;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hunter on 4/15/17.
 */

public abstract class FileDataSource implements DataSource {
    /* Map<floor, List<Node>> */
    protected final Map<Integer, LinkedList<Node>> nodes;
    /* Map<floor, List<Way>> */
    protected final Map<Integer, LinkedList<Way>> ways;

    private Floor[] floors;
    private final Comparator<Floor> floorComparator = new Comparator<Floor>() {
        @Override
        public int compare(Floor o1, Floor o2) {
            return o1.getZ() - o2.getZ();
        }
    };

    private final MobjGraper<Node> nodeGraper;
    private final MobjGraper<Way> wayGraper;

    private boolean dataLoaded = false;

    public FileDataSource() {
        nodes = new LinkedHashMap<>();
        ways = new LinkedHashMap<>();

        nodeGraper = new MobjGraper<>(nodes);
        wayGraper = new MobjGraper<>(ways);
    }

    public final synchronized void loadData() {
        if (!dataLoaded && actualLoadData()) {
            generateFloors();
            dataLoaded = true;
        }
    }

    protected abstract boolean actualLoadData();


    private void generateFloors() {
        Set<Integer> levels = new HashSet<>();
        levels.addAll(ways.keySet());
        levels.addAll(nodes.keySet());
        floors = new Floor[levels.size()];
        int index = 0;
        for (Integer level : levels) {
            floors[index++] = generateFloors(level);
        }
        Arrays.sort(floors, floorComparator);
    }

    private Floor generateFloors(Integer level) {
        if (!ways.containsKey(level) && !nodes.containsKey(level)) {
            //unreachable
            return null;
        }
        Rect bounds = new Rect(Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        // nodes
        for (Node node : nodes.get(level)) {
            bounds = Rect.mixMax(bounds, node.getBounds());
        }
        // ways
        for (Way way : ways.get(level)) {
            bounds = Rect.mixMax(bounds, way.getBounds());
        }
        return new Floor(level, bounds);
    }

    private void checkDataLoaded() {
        if (!dataLoaded) {
            throw new DataNotLoadedException();
        }
    }

    public boolean isDataLoaded() {
        return dataLoaded;
    }

    @Override
    public Floor[] getFloors(Integer level) {
        checkDataLoaded();
        if (level == null) {
            return Arrays.copyOf(floors, floors.length);
        }
        int index = Arrays.binarySearch(floors, new Floor(level, null), floorComparator);
        if (index>-1 && index<floors.length) {
            return new Floor[]{floors[index]};
        }
        return new Floor[0];
    }

    @Override
    public List<Node> getNodes(Rect region, Integer floor) {
        checkDataLoaded();
        return Collections.unmodifiableList(nodeGraper.getMobjs(region, floor));
    }

    @Override
    public List<Way> getWays(Rect region, Integer floor) {
        checkDataLoaded();
        return Collections.unmodifiableList(wayGraper.getMobjs(region, floor));
    }

    @Override
    public List<MObj> findMObjs(String name, Rect region, Integer floor) {
        checkDataLoaded();
        //TODO implement
        return Collections.emptyList();
    }

    private class MobjGraper<T extends MObj> {
        Map<Integer, LinkedList<T>> mObjs;

        Rect region;
        Integer floor;
        List<T> cached;

        MobjGraper(Map<Integer, LinkedList<T>> mObjs) {
            this.mObjs = mObjs;
        }

        List<T> getMobjs(Rect region, Integer floor) {
            if (equals(this.floor, floor) && equals(this.region, region) && cached != null){
                return cached;
            }
            List<T> results = new LinkedList<>();
            if (floor != null) {
                List<T> l = filterMobjs(mObjs.get(floor), region);
                if (l != null) results.addAll(l);
            }
            else {
                Iterator<LinkedList<T>> iterator = mObjs.values().iterator();
                while (iterator.hasNext()) {
                    List<T> l = filterMobjs(iterator.next(), region);
                    if (l != null) results.addAll(l);
                }
            }
            //cache results
            this.region = region==null ? null : new Rect(region);
            this.floor = floor==null ? null : new Integer(floor);
            cached = results;
            return results;
        }

        List<T> filterMobjs(List<T> mObjs, Rect region) {
            if (region == null || mObjs == null) {
                return mObjs;
            }
            List<T> results = new LinkedList<>();
            for (T mObj : mObjs) {
                if (Rect.intersects(mObj.getBounds(), region)) {
                    results.add(mObj);
                }
            }
            return results;
        }

        boolean equals(Object a, Object b) {
            return (a == b) || (a != null && a.equals(b));
        }
    }

    public static class DataNotLoadedException extends IllegalStateException {
        public DataNotLoadedException() {
        }

        public DataNotLoadedException(String message) {
            super(message);
        }

        public DataNotLoadedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
