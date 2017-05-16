package com.hunter.indoormap.route;

import android.support.annotation.NonNull;

import com.hunter.indoormap.beans.GPoint;

/**
 * Created by hunter on 5/15/17.
 */

public interface ArbitraryRouterDataSource extends RouterDataSource {

    /**
     *
     * @param src
     * @return not null anytime. but may length == 0
     */
    @NonNull
    GPoint[] getNearestPoint(@NonNull GPoint src);
}
