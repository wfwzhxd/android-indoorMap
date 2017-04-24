package com.hunter.indoormap.overlay;

import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.zxing.Result;
import com.hunter.indoormap.MapView;
import com.hunter.indoormap.R;
import com.hunter.indoormap.beans.GPoint;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by hunter on 4/23/17.
 */

public class QRScannerOverlay extends Overlay implements ZXingScannerView.ResultHandler{
    private static final String TAG = QRScannerOverlay.class.getSimpleName();

    MapView mapView;
    FragmentManager fragmentManager;
    ScannerDialogFragment scannerDialogFragment;

    private static final int WIDTH = 50;
    Bitmap bitmap;
    private Rect rect;
    private int width;

    private Paint paint;

    public QRScannerOverlay(MapView mapView, FragmentManager fragmentManager) {
        this.mapView = mapView;
        this.fragmentManager =fragmentManager;
        scannerDialogFragment = new ScannerDialogFragment();
        scannerDialogFragment.setResultHandler(this);

        bitmap = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.locate);
        final float density = mapView.getResources().getDisplayMetrics().density;
        width = (int) (WIDTH * density);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas c, MapView mv) {
        if (rect == null) {
            rect = new com.hunter.indoormap.beans.Rect(width/5, 0, width, width).offset(0, mapView.getHeight()-width*2).toRect();
        }
        c.drawBitmap(bitmap, null, rect, paint);
    }

    @Override
    public void handleResult(Result result) {
        try {
            GPoint location = parseGPoint(result.getText());
            if (mapView.getDataSource().getFloorMap(location.z) != null) {
                scannerDialogFragment.dismiss();
                mapView.getMyLocationController().setMyLocation(location);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(mapView.getContext(), "错误的二维码！", Toast.LENGTH_SHORT).show();
    }

    private GPoint parseGPoint(String line) {
        String[] ps = line.split(",");
        GPoint p = null;
        if (ps.length == 3) {
            p = new GPoint(Integer.parseInt(ps[0]), Integer.parseInt(ps[1]), Integer.parseInt(ps[2]));
        }
        else {
            Log.e(TAG, "Error formate gpoint{ " + line + " }");
        }
        return p;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        if (new com.hunter.indoormap.beans.Rect(rect).contains(e.getX(), e.getY())) {
            scannerDialogFragment.show(fragmentManager, ScannerDialogFragment.class.getSimpleName());
            return true;
        }
        return super.onSingleTapConfirmed(e, mapView);
    }

    @Override
    public boolean onLongPress(MotionEvent e, MapView mapView) {
        if (new com.hunter.indoormap.beans.Rect(rect).contains(e.getX(), e.getY())) {
            mapView.setMapCenter(mapView.getMyLocationController().getMyLocation());
            return true;
        }
        return super.onLongPress(e, mapView);
    }
}
