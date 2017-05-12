package com.hunter.indoormap.route.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.hunter.indoormap.beans.GPoint;
import static com.hunter.indoormap.route.ARouterDataSource.ZXYIndexer;
import com.hunter.indoormap.route.AbsRouter;
import com.hunter.indoormap.route.Road;
import com.hunter.indoormap.route.RouterDataSource;
import static com.hunter.indoormap.route.RouterDataSource.Wnode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by hunter on 5/12/17.
 */

public class AstarRouter extends AbsRouter {
    private static final String TAG = AstarRouter.class.getSimpleName();

    private PriorityQueue<AstarItem> openList;
    private ZXYIndexer<AstarItem> closeList;


    public AstarRouter(RouterDataSource routerDataSource) {
        super(routerDataSource);
    }

    @Override
    protected Road[] route(final Wnode start, final Wnode end) {
//        Log.d(TAG, "route start=" + start + ", end=" + end);
        openList = new PriorityQueue<>();
        closeList = new ZXYIndexer<>();
        final AstarItem destination = new AstarItem(end, null, null);
        Road[] roads = null;
        // main logic start
        openList.add(new AstarItem(start, null, destination));
        AstarItem curAstar;
        boolean finded = false;
        while (!finded && (curAstar = openList.poll()) != null) {
            closeList.addZXY(curAstar, curAstar.wnode.getItem());
            List<Wnode> nexts = curAstar.wnode.getNexts();
            if (nexts == null) {
                continue;
            }
            AstarItem nextAstar;
            AstarItem newAstar;
            for (Wnode wnode : nexts) {
                if (end.equals(wnode)) {
                    // find destination
                    roads = new Road[]{buildRoad(new AstarItem(wnode, curAstar, destination))};
                    finded = true;
                    break;
                }
                if (closeList.getZXY(wnode.getItem()) != null) {
                    continue;
                }
                newAstar = new AstarItem(wnode, curAstar, destination);
                if ((nextAstar = findAstarItem(wnode.getItem())) != null) {
                    if (newAstar.g < nextAstar.g) {
                        openList.remove(nextAstar);
                        openList.add(newAstar);
                    }
                } else {
                    openList.add(newAstar);
                }
            }
        }

        // main logic end
        openList = null;
        closeList = null;
        return roads;
    }

    private Road buildRoad(AstarItem astarItem) {
        List<GPoint> gPointList = new ArrayList<>();
        while (astarItem != null) {
            gPointList.add(astarItem.wnode.getItem());
            astarItem = astarItem.parent;
        }
        Collections.reverse(gPointList);
        return new Road(gPointList);
    }

    private AstarItem findAstarItem(GPoint gPoint) {
        for (AstarItem astarItem : openList) {
            if (astarItem.wnode.getItem().equals(gPoint)) {
                return astarItem;
            }
        }
        return null;
    }

    private static class AstarItem implements Comparable<AstarItem> {
        final int f;
        final int g;
        final int h;

        final AstarItem parent;

        final Wnode wnode;

        public AstarItem(Wnode wnode, AstarItem parent, AstarItem destination) {
            this.wnode = wnode;
            this.parent = parent;
            if (parent != null) {
                this.g = parent.g + calG(parent, this);
            } else {
                this.g = 0;
            }
            if (destination != null) {
                this.h = calH(this, destination);
            } else {
                this.h = 0;
            }
            f = g + h;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AstarItem)) return false;

            AstarItem astarItem = (AstarItem) o;

            return wnode != null ? wnode.equals(astarItem.wnode) : astarItem.wnode == null;
        }

        @Override
        public int compareTo(@NonNull AstarItem o) {
            return f - o.f;
        }

        /*
         * For a simple calculation, use Int not Float to calculate distance.
         */
        private static int calDistance(AstarItem start, AstarItem end) {
            //TODO implement
            GPoint startPoint = start.wnode.getItem();
            GPoint endPoint = end.wnode.getItem();
            int dx = Math.round(startPoint.x-endPoint.x);
            int dy = Math.round(startPoint.y-endPoint.y);
            int dz = (startPoint.z - endPoint.z) * 3; // the height of one floor is about 3m.
            int dxy = (int) Math.sqrt(dx*dx + dy*dy);
            return dz == 0 ? dxy : (int) Math.sqrt(dxy * dxy + dz * dz);
        }


        public static int calG(AstarItem start, AstarItem end) {
            return calDistance(start, end);
        }

        public static int calH(AstarItem start, AstarItem end) {
            return calDistance(start, end);
        }
    }
}
