package com.hunter.indoormap.data;

import com.hunter.indoormap.beans.MObj;
import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Rect;
import com.hunter.indoormap.beans.Way;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by hunter on 4/15/17.
 */

public abstract class FileDataSource implements DataSource {
    /* Map<floor, List<Node>> */
    protected final Map<Integer, LinkedList<Node>> nodes;
    /* Map<floor, List<Way>> */
    protected final Map<Integer, LinkedList<Way>> ways;

    int[] floors;

    private final MobjGraper<Node> nodeGraper;
    private final MobjGraper<Way> wayGraper;

    private boolean dataLoaded = false;

    public FileDataSource() {
        nodes = new LinkedHashMap<>();
        ways = new LinkedHashMap<>();
//        floorMaps = new LinkedList<>();

        nodeGraper = new MobjGraper<>(nodes);
        wayGraper = new MobjGraper<>(ways);
    }

    public final synchronized void loadData() {
        if (!dataLoaded && actualLoadData()) {
            dataLoaded = true;
        }
    }

    protected abstract boolean actualLoadData();

    private void checkDataLoaded() {
        if (!dataLoaded) {
            throw new DataNotLoadedException();
        }
    }

    public boolean isDataLoaded() {
        return dataLoaded;
    }

    @Override
    public int[] getFloors() {
        //TODO implements
        /*
        checkDataLoaded();
        if (floors == null) {
            floors = new int[floorMaps.size()];
            int i = 0;
            Iterator<FloorMap> iterator = floorMaps.iterator();
            while (iterator.hasNext()) {
                floors[i++] = iterator.next().getZ();
            }
            Arrays.sort(floors);
        }
        return Arrays.copyOf(floors, floors.length);*/
        return null;
    }
    /*
    @Override
    public FloorMap getFloorMap(int floor) {
        checkDataLoaded();
        for (FloorMap f : floorMaps) {
            if (floor == f.getZ()) {
                return f;
            }
        }
        return null;
    }*/

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
        return null;
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
                results.addAll(filterMobjs(mObjs.get(floor), region));
            }
            else {
                Iterator<LinkedList<T>> iterator = mObjs.values().iterator();
                while (iterator.hasNext()) {
                    results.addAll(filterMobjs(iterator.next(), region));
                }
            }
            //cache results
            this.region = region==null ? null : new Rect(region);
            this.floor = floor==null ? null : new Integer(floor);
            cached = results;
            return results;
        }

        List<T> filterMobjs(List<T> mObjs, Rect region) {
            if (region == null) {
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
