package com.hunter.indoormap.overlay;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunter.indoormap.MapView;
import com.hunter.indoormap.R;
import com.hunter.indoormap.beans.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hunter on 4/22/17.
 */

public class FloorSelectOverlay extends Overlay {
    MapView mapView;
    View view;
    TextView textView;

    public FloorSelectOverlay(MapView mapView) {
        this.mapView = mapView;
        view = LayoutInflater.from(mapView.getContext()).inflate(R.layout.overlay_floor_select, (ViewGroup) (mapView.getParent()), false);
        textView = (TextView) view.findViewById(R.id.floorText);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        final float density = mapView.getResources().getDisplayMetrics().density;
        layoutParams.rightMargin = (int) (10 * density);
        layoutParams.topMargin = (int) (10 * density);
        mapView.addView(view, layoutParams);
    }

    @Override
    public void draw(Canvas c, MapView mv) {
        if (!new String(mv.getFloor()+"").equals(textView.getText())) {
            textView.setText(mv.getFloor()+"楼");
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        if (new Rect(textView.getLeft(), textView.getTop(), textView.getRight(), textView.getBottom()).offset(view.getX(), view.getY()).contains(e.getX(), e.getY())) {
            showPopWindow(mapView);
            return true;
        }
        return super.onSingleTapConfirmed(e, mapView);
    }

    private void showPopWindow(final MapView mapView) {
        final ListPopupWindow popupWindow = new ListPopupWindow(mapView.getContext());
        final List list = new ArrayList<String>();
        final int[] floors = mapView.getDataSource().getFloors();
        for (int i=0; i<floors.length; i++) {
            list.add(floors[i]+"楼");
        }
        popupWindow.setAdapter(new ArrayAdapter(mapView.getContext(), R.layout.simple_list_item_1, list));
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mapView.setFloor(floors[position]);
                popupWindow.dismiss();
            }
        });
        popupWindow.setAnchorView(view);
        popupWindow.show();
    }
}
