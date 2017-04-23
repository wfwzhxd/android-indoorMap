package com.hunter.indoormap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.hunter.indoormap.data.TxtFileDataSource;
import com.hunter.indoormap.overlay.FloorSelectOverlay;
import com.hunter.indoormap.overlay.MyLocationOverlay;
import com.hunter.indoormap.overlay.NodeOverlay;
import com.hunter.indoormap.overlay.RotateIndicatorOverlay;
import com.hunter.indoormap.overlay.SelectOverlay;
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
        MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mapView);
        mapView.getOverlayManager().add(myLocationOverlay);
        mapView.getOverlayManager().add(new SelectOverlay(mapView));
        mapView.getOverlayManager().add(new RotateIndicatorOverlay(getResources()));
        mapView.getOverlayManager().add(new FloorSelectOverlay(mapView));
        mapView.setDataSource(new TxtFileDataSource(getAssets(), "data_test"));
        mapView.setFloor(1);
        mapView.setMyLocationController(myLocationOverlay);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mapView.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
