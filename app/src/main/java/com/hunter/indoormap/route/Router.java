package com.hunter.indoormap.route;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hunter.indoormap.beans.GPoint;

/**
 * Created by hunter on 5/6/17.
 */

public interface Router {

    @NonNull
    Road[] route(final GPoint start, final GPoint end, @Nullable final GPoint[] pass);
}
