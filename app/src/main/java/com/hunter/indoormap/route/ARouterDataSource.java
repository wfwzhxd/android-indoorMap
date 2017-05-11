package com.hunter.indoormap.route;

import com.hunter.indoormap.MathUtils;
import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Way;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by hunter on 5/6/17.
 */

public class ARouterDataSource implements RouterDataSource{

    private final Comparator<Float> floatComparator = new Comparator<Float>() {
        @Override
        public int compare(Float o1, Float o2) {
            if (MathUtils.isEqual(o1, o2)) {
                return 0;
            }
            return o1-o2 < 0 ? -1 : 1;
        }
    };

    // z , x, y
    TreeMap<Integer, TreeMap<Float, TreeMap<Float, Wnode>>> wNodes;

    public ARouterDataSource(List<Way.WayLine> wayLines) {
        wNodes = new TreeMap<>();
    }

    private void sliceWayLines(List<Way.WayLine> wayLines) {

    }


    public void addWnode(Wnode<? extends GPoint> node) {
        if (node == null || node.getItem() == null) {
            throw new NullPointerException();
        }
        TreeMap<Float, TreeMap<Float, Wnode>> zn = wNodes.get(node.getItem().z);
        if (zn == null) {
            zn = new TreeMap<>(floatComparator);
            wNodes.put(Integer.valueOf(node.getItem().z), zn);
        }
        TreeMap<Float, Wnode> xn = zn.get(node.getItem().x);
        if (xn == null) {
            xn = new TreeMap<>(floatComparator);
            zn.put(new Float(node.getItem().x), xn);
        }
        xn.put(new Float(node.getItem().y), node);
    }

    public Wnode getWnode(GPoint gPoint) {
        if (gPoint == null) {
            return null;
        }
        TreeMap<Float, TreeMap<Float, Wnode>> zn = wNodes.get(gPoint.z);
        if (zn == null) {
            return null;
        }
        TreeMap<Float, Wnode> xn = zn.get(gPoint.x);
        if (xn == null) {
            return null;
        }
        return xn.get(gPoint.y);
    }
}
