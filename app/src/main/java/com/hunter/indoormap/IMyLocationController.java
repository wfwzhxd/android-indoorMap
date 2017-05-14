package com.hunter.indoormap;

import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.util.Observer;


/**
 * Created by hunter on 4/22/17.
 */

public interface IMyLocationController {

    /**
     * Get the current location.
     * @return null if mylocation is unknown.
     */
    GPoint getMyLocation();

    void setMyLocation(GPoint myLocation);

    void addOnMyLocationChangedListener(OnMyLocationChangedListener listener);

    void removeOnMyLocationChangedListener(OnMyLocationChangedListener listener);

    interface OnMyLocationChangedListener extends Observer {
        void onMyLocationChanged(GPoint newLocation);
    }
}
