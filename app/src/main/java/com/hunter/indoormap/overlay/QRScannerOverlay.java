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

import com.hunter.indoormap.MapView;
import com.hunter.indoormap.R;
import com.hunter.indoormap.beans.GPoint;

import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by hunter on 4/23/17.
 */

public class QRScannerOverlay extends Overlay implements ZBarScannerView.ResultHandler{
    private static final String TAG = QRScannerOverlay.class.getSimpleName();

    MapView mapView;
    FragmentManager fragmentManager;
    ScannerDialogFragment scannerDialogFragment;

    private static final int WIDTH = 40;
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
            int left = (mv.getWidth()-width)>>1;
            int top = width/5;
            rect = new com.hunter.indoormap.beans.Rect(left, top, left+width, top+width).toRect();
        }
        c.drawBitmap(bitmap, null, rect, paint);
    }

    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
        try {
            GPoint location = parseGPoint(result.getContents());
            if (mapView.getDataSource().getFloors(location.z) != null) {
                scannerDialogFragment.dismiss();
                mapView.getMyLocationController().setMyLocation(location);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(mapView.getContext(), R.string.wrong_qrcode, Toast.LENGTH_SHORT).show();
    }

    private GPoint parseGPoint(String line) {
        if (line == null) return null;
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
