package com.hunter.indoormap.route;

import com.hunter.indoormap.beans.GPoint;

/**
 * Created by hunter on 5/6/17.
 */

public interface Router {

    Road[] route(GPoint start, GPoint end, GPoint[] pass);
}
