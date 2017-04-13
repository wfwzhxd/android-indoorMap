package com.hunter.indoormap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hunter.indoormap.beans.FloorMap;
import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.ShapeInfo;
import com.hunter.indoormap.beans.Way;

public class MainActivity extends AppCompatActivity {

    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.map);
//        fakeData();
    }
    /*
    private void fakeData() {
        Node home = new Node(1, "Home", new GPoint(16,37,1));
        Node school = new Node(2, "School", new GPoint(55,51,1));
        mapView.addNode(home);
        mapView.addNode(school);
        Node lt = new Node(4,GPoint.from(10, 10, 1));
        Node rt = new Node(5,GPoint.from(80, 10, 1));
        Node rb = new Node(6,GPoint.from(80, 100, 1));
        Node lb = new Node(7,GPoint.from(10, 100, 1));
        FloorMap floorMap = new FloorMap(1, new ShapeInfo(new ShapeInfo.Shape(3, new Point[]{new Point(10, 10), new Point(80, 10), new Point(80, 100), new Point(10, 100)})));
        mapView.setFloorMap(floorMap);

        Node n8 = new Node(8, GPoint.from(20, 30, 1));
        Node n9 = new Node(9, GPoint.from(20, 60, 1));
        Way oneWay = new Way(10, "OneWay", new Node[]{n8, n9});
        Node n10 = new Node(10, GPoint.from(70, 45, 1));
        Node n11 = new Node(11, GPoint.from(20, 45, 1));
        Way twoWay = new Way(10, "TwoWay", new Node[]{n10, n11});
        mapView.addWay(oneWay);
        mapView.addWay(twoWay);
    }*/

}
