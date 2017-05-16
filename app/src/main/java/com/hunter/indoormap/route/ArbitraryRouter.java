package com.hunter.indoormap.route;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hunter.indoormap.Log;
import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.route.RouterDataSource.Wnode;

import java.util.Arrays;

/**
 * Created by hunter on 5/15/17.
 */

public class ArbitraryRouter implements Router {
    private static final String TAG = ArbitraryRouter.class.getSimpleName();
    private final Router wrappedRouter;
    private final ArbitraryRouterDataSource arbitraryRouterDataSource;

    public ArbitraryRouter(@NonNull Router wrappedRouter, @NonNull ArbitraryRouterDataSource arbitraryRouterDataSource) {
        this.wrappedRouter = wrappedRouter;
        this.arbitraryRouterDataSource = arbitraryRouterDataSource;
    }

    @Override
    @NonNull
    public Road[] route(GPoint start, GPoint end, @Nullable GPoint[] pass) {
        //TODO process end point with arbitrary points.
        Wnode<GPoint> startNode = arbitraryRouterDataSource.getWnode(start);
        if (startNode == null) {
            GPoint[] startNearestGpoints = arbitraryRouterDataSource.getNearestPoint(start);
            Log.o(start + " nearest: " + Arrays.toString(startNearestGpoints));
            switch (startNearestGpoints.length) {
                case 0:
                    return new Road[0];
                case 1:
                    Road[] roads = wrappedRouter.route(startNearestGpoints[0], end, pass);
                    if (roads.length == 0) {
                        return roads;
                    } else {
                        return new Road[]{roads[0].addWayNode2First(start)};
                    }
                case 2:
                    Road[] roadsA = wrappedRouter.route(startNearestGpoints[0], end, pass);
                    Road[] roadsB = wrappedRouter.route(startNearestGpoints[1], end, pass);
                    if (roadsA.length != 0 && roadsB.length != 0) {
                        Road roadA = roadsA[0].addWayNode2First(start);
                        Road roadB = roadsB[0].addWayNode2First(start);
                        if (roadA.getLength() < roadB.getLength()) {
                            return new Road[]{roadA};
                        } else {
                            return new Road[]{roadB};
                        }
                    } else if (roadsA.length == 0 && roadsB.length != 0){
                        return new Road[]{roadsB[0].addWayNode2First(start)};
                    } else if (roadsB.length == 0 && roadsA.length != 0){
                        return new Road[]{roadsA[0].addWayNode2First(start)};
                    } else {
                        return new Road[0];
                    }
                default:
                        return new Road[0];

            }
        } else return wrappedRouter.route(start, end, pass);
    }
}
