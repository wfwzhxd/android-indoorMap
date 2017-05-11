package com.hunter.indoormap;

import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Rect;
import com.hunter.indoormap.beans.Way;
import com.hunter.indoormap.data.FileDataSource;
//import com.hunter.indoormap.data.TxtFileDataSource;
import com.hunter.indoormap.Log;

import org.junit.*;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by hunter on 4/17/17.
 */

public class FileDataSourceTest {
    private static final String TESTDATA_PATH = "./app/src/main/assets/data_test";

    @org.junit.Test
    public void getNodesTest() {
        /*
        FileDataSource fileDataSource = new TxtFileDataSource(new File(TESTDATA_PATH));
        List<Node> nodes = fileDataSource.getNodes(new Rect(7, 1, 9, 2), Integer.valueOf(1));
        Log.o(nodes.toString());*/
    }

    @Test
    public void getWaysTest() {
        /*
        FileDataSource fileDataSource = new TxtFileDataSource(new File(TESTDATA_PATH));
        List<Way> ways = fileDataSource.getWays(new Rect(7, 1, 9, 2), Integer.valueOf(1));
        Log.o(ways.toString());*/
    }
}
