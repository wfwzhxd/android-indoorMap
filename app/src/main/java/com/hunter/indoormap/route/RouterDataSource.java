package com.hunter.indoormap.route;

import com.hunter.indoormap.MathUtils;
import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Way;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by hunter on 5/6/17.
 */

public abstract class RouterDataSource {

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
    TreeMap<Integer, TreeMap<Float, TreeMap<Float, Wnode<? extends GPoint>>>> wNodes;

    public RouterDataSource(List<Way> ways) {
        wNodes = new TreeMap<>();
    }


    public void addWnode(Wnode<? extends GPoint> node) {
        if (node == null || node.item == null) {
            throw new NullPointerException();
        }
        TreeMap<Float, TreeMap<Float, Wnode<? extends GPoint>>> zn = wNodes.get(node.item.z);
        if (zn == null) {
            zn = new TreeMap<>(floatComparator);
            wNodes.put(Integer.valueOf(node.item.z), zn);
        }
        TreeMap<Float, Wnode<? extends GPoint>> xn = zn.get(node.item.x);
        if (xn == null) {
            xn = new TreeMap<>(floatComparator);
            zn.put(new Float(node.item.x), xn);
        }
        xn.put(new Float(node.item.y), node);
    }

    public Wnode<? extends GPoint> getWnode(GPoint gPoint) {
        if (gPoint == null) {
            return null;
        }
        TreeMap<Float, TreeMap<Float, Wnode<? extends GPoint>>> zn = wNodes.get(gPoint.z);
        if (zn == null) {
            return null;
        }
        TreeMap<Float, Wnode<? extends GPoint>> xn = zn.get(gPoint.x);
        if (xn == null) {
            return null;
        }
        return xn.get(gPoint.y);
    }

    public static class Wnode<E> {
        private E item;
        private List<Wnode<E>> next;

        public Wnode(E element) {
            this.item = element;
            this.next = new LinkedList<>();
        }

        public Wnode<E> addNext(Wnode<E> n) {
            next.add(n);
            return this;
        }

        public Wnode<E> addNext(List<Wnode<E>> n) {
            next.addAll(n);
            return this;
        }

        public Wnode<E> removeNext(Wnode<E> n) {
            next.remove(n);
            return this;
        }

        public Wnode<E> clearNext() {
            next.clear();
            return this;
        }

        public E getItem() {
            return item;
        }

        public void setItem(E item) {
            this.item = item;
        }

        public List<Wnode<E>> getNext() {
            return Collections.unmodifiableList(next);
        }

        @Override
        public String toString() {
            return "Wnode{" +
                    "item=" + item +
                    ", next=" + next +
                    '}';
        }
    }
}
