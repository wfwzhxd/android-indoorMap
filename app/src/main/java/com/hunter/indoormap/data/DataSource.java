package com.hunter.indoormap.data;

import com.hunter.indoormap.beans.Floor;
import com.hunter.indoormap.beans.MObj;
import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Rect;
import com.hunter.indoormap.beans.Way;

import java.util.List;

/**
 * Created by hunter on 4/14/17.
 */

public interface DataSource {

    Floor[] getFloors(Integer floor);

    List<Node> getNodes(Rect region, Integer floor);

    List<Way> getWays(Rect region, Integer floor);

    List<MObj> findMObjs(String name, Rect region, Integer floor);

    class EmptyDataSource implements DataSource {

        @Override
        public Floor[] getFloors(Integer floor) {
            return null;
        }

        @Override
        public List<Node> getNodes(Rect region, Integer floor) {
            return null;
        }

        @Override
        public List<Way> getWays(Rect region, Integer floor) {
            return null;
        }

        @Override
        public List<MObj> findMObjs(String name, Rect region, Integer floor) {
            return null;
        }
    }

}
