package com.hunter.indoormap.data;

import com.hunter.indoormap.beans.MObj;
import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Rect;
import com.hunter.indoormap.beans.Way;

import java.util.List;

/**
 * Created by hunter on 4/14/17.
 */

public interface DataSource {

    int[] getFloors();

    //FloorMap getFloorMap(int floor);

    List<Node> getNodes(Rect region, Integer floor);

    List<Way> getWays(Rect region, Integer floor);

    List<MObj> findMObjs(String name, Rect region, Integer floor);

}
