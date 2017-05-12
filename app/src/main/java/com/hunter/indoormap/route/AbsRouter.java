package com.hunter.indoormap.route;

import com.hunter.indoormap.Log;
import com.hunter.indoormap.beans.GPoint;
import static com.hunter.indoormap.route.RouterDataSource.Wnode;

/**
 * Created by hunter on 5/12/17.
 */

public abstract class AbsRouter implements Router {
    private static final String TAG = AbsRouter.class.getSimpleName();

    private RouterDataSource routerDataSource;

    public AbsRouter(RouterDataSource routerDataSource) {
        this.routerDataSource = routerDataSource;
    }

    @Override
    public Road[] route(final GPoint start, final GPoint end, final GPoint[] pass) {
        if (start.equals(end)) {
            return null;
        }
        Wnode startWnode = routerDataSource.getWnode(start);
        Wnode endWnode = routerDataSource.getWnode(end);
        if (startWnode == null) {
            Log.e(TAG, "not found start=" + start + " in RouterDataSource=" + routerDataSource);
            return null;
        }
        if (endWnode == null) {
            Log.e(TAG, "not found end=" + end + " in RouterDataSource=" + routerDataSource);
            return null;
        }
        return route(startWnode, endWnode);
    }

    protected abstract Road[] route(final Wnode start, final Wnode end);

}
