package com.hunter.indoormap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.hunter.indoormap.data.DxfDataSource;
import com.hunter.indoormap.overlay.FloorSelectOverlay;
import com.hunter.indoormap.overlay.MyLocationOverlay;
import com.hunter.indoormap.overlay.NodeOverlay;
import com.hunter.indoormap.overlay.QRScannerOverlay;
import com.hunter.indoormap.overlay.RotateIndicatorOverlay;
import com.hunter.indoormap.overlay.SelectOverlay;
import com.hunter.indoormap.overlay.WayOverlay;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.map_view);
        try {
            InputStream inputStream = getAssets().open("hospital.dxf");
            mapView.setDataSource(new DxfDataSource(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapView.setLevel(1);

        mapView.getOverlayManager().add(new WayOverlay());
        mapView.getOverlayManager().add(new NodeOverlay());
        MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mapView);
        mapView.getOverlayManager().add(myLocationOverlay);
        mapView.setMyLocationController(myLocationOverlay);
        mapView.getOverlayManager().add(new SelectOverlay(mapView));
        mapView.getOverlayManager().add(new RotateIndicatorOverlay(getResources()));
        mapView.getOverlayManager().add(new FloorSelectOverlay(mapView));
        mapView.getOverlayManager().add(new QRScannerOverlay(mapView, getFragmentManager()));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mapView.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
