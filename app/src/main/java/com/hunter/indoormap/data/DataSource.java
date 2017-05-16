package com.hunter.indoormap.data;

import android.support.annotation.NonNull;

import com.hunter.indoormap.beans.Floor;
import com.hunter.indoormap.beans.MObj;
import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Rect;
import com.hunter.indoormap.beans.Way;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hunter on 4/14/17.
 */

public interface DataSource {

    /**
     *
     * @param floor
     * @return Never return null
     */
    @NonNull
    Floor[] getFloors(Integer floor);

    @NonNull
    List<Node> getNodes(Rect region, Integer floor);

    @NonNull
    List<Way> getWays(Rect region, Integer floor);

    @NonNull
    List<MObj> findMObjs(String name, Rect region, Integer floor);

    class EmptyDataSource implements DataSource {

        @Override
        public Floor[] getFloors(Integer floor) {
            return new Floor[0];
        }

        @Override
        public List<Node> getNodes(Rect region, Integer floor) {
            return Collections.emptyList();
        }

        @Override
        public List<Way> getWays(Rect region, Integer floor) {
            return Collections.emptyList();
        }

        @Override
        public List<MObj> findMObjs(String name, Rect region, Integer floor) {
            return Collections.emptyList();
        }
    }

}
