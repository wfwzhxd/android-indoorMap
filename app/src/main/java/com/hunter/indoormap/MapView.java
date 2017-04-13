package com.hunter.indoormap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.hunter.indoormap.beans.FloorMap;
import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Way;

import java.util.LinkedList;
import java.util.List;

public class MapView extends View implements View.OnTouchListener {
    private static final String TAG = MapView.class.getSimpleName();

    GestureDetector mGestureDetector;
    ScaleGestureDetector mScaleGestureDetector;

    Paint wayPaint;
    Paint nodePaint;
    Paint namePaint;

    private int scale = 8;

    List<Way> ways = new LinkedList<>();
    List<Node> nodes = new LinkedList<>();

    FloorMap floorMap;

    public MapView(Context context) {
        super(context);
        init(context);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        wayPaint = new Paint();
        wayPaint.setColor(Color.BLACK);

        nodePaint = new Paint();
        nodePaint.setColor(Color.RED);

        namePaint = new Paint();
        namePaint.setColor(Color.BLUE);

        setOnTouchListener(this);
        mGestureDetector = new GestureDetector(context, new MapViewGestureDetector());
        mScaleGestureDetector = new ScaleGestureDetector(context, new MapViewScaleGestureDetector());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*
        canvas.drawColor(Color.GRAY);
        if (floorMap != null) {
            Point[] points = floorMap.getBounds().getShape().getPoints();
            if (points.length > 1){
                Path path = new Path();
                path.moveTo(o2s(points[0].x), o2s(points[0].y));
                for (int i=1;i<points.length;i++) {
                    path.lineTo(o2s(points[i].x), o2s(points[i].y));
                }
                path.close();
                Paint floorPaint = new Paint();
                floorPaint.setColor(Color.WHITE);
                floorPaint.setStyle(Paint.Style.FILL);
                canvas.drawPath(path, floorPaint);
            }
        }
        drawWay(canvas);
        drawNode(canvas);
        */
    }


    private void drawNode(Canvas canvas) {
        for (Node node:nodes){
            float left = o2s(node.getXyz().x);
            float top = o2s(node.getXyz().y);
            canvas.drawRect(left, top, left+scale, top+scale, nodePaint);
        }
    }

    private void drawWay(Canvas canvas) {
        wayPaint.setStrokeWidth(scale);
        for (Way way:ways) {
            if(way.getNodes().length>1) {
                for (int i=0; i<way.getNodes().length-1; i++) {
                    canvas.drawLine(o2s(way.getNodes()[i].getXyz().x), o2s(way.getNodes()[i].getXyz().y), o2s(way.getNodes()[i+1].getXyz().x), o2s(way.getNodes()[i+1].getXyz().y), wayPaint);
                }
            }
        }
    }


    /**
     * original xy to scaled xy
     * @param x
     * @return
     */
    int o2s(int x){
        return x*scale;
    }

    int s2o(int x){
        return x/scale;
    }

    public void addNode(Node node){
        nodes.add(node);
        invalidate();
    }

    public void addWay(Way way){
        ways.add(way);
        invalidate();
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
        invalidate();
    }

    public FloorMap getFloorMap() {
        return floorMap;
    }

    public void setFloorMap(FloorMap floorMap) {
        this.floorMap = floorMap;
        invalidate();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mGestureDetector.onTouchEvent(motionEvent)){}else {
            mScaleGestureDetector.onTouchEvent(motionEvent);
        }
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                int x = (int) motionEvent.getX() + getScrollX();
                int y = (int) motionEvent.getY() + getScrollY();
                Log.i(TAG, "view: " + x + ":" + y + " ； original: " + s2o(x) + ":" + s2o(y));
        }
        return true;
    }

    class MapViewGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            MapView.this.scrollBy((int)distanceX, (int)distanceY);
            return true;
        }
    }

    class MapViewScaleGestureDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            int newScale = (int) (scale*detector.getScaleFactor());
            if (newScale != scale) {
                setScale(newScale);
                return true;
            }
            return false;
        }
    }
}
