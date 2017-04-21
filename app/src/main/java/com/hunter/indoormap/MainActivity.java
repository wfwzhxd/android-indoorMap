package com.hunter.indoormap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hunter.indoormap.data.TxtFileDataSource;
import com.hunter.indoormap.overlay.NodeOverlay;
import com.hunter.indoormap.overlay.RotateIndicatorOverlay;
import com.hunter.indoormap.overlay.WayOverlay;

public class MainActivity extends AppCompatActivity {

    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.getOverlayManager().add(new WayOverlay());
        mapView.getOverlayManager().add(new NodeOverlay());
        mapView.getOverlayManager().add(new RotateIndicatorOverlay(getResources()));
        mapView.setDataSource(new TxtFileDataSource(getAssets(), "data_test"));
        mapView.setFloor(1);
    }

}
