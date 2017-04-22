package com.hunter.indoormap;

import com.hunter.indoormap.beans.GPoint;

/**
 * Created by hunter on 4/22/17.
 */

public interface IMyLocationController {

    GPoint getMyLocation();

    void setMyLocation(GPoint myLocation);
}
