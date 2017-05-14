package com.hunter.indoormap.overlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hunter.indoormap.CoordinateUtils;
import com.hunter.indoormap.IMyLocationController;
import com.hunter.indoormap.MapView;
import com.hunter.indoormap.MatrixUtils;
import com.hunter.indoormap.R;
import com.hunter.indoormap.beans.GPoint;
import static com.hunter.indoormap.beans.Way.WayNode;

import com.hunter.indoormap.beans.Line;
import com.hunter.indoormap.beans.Point;
import com.hunter.indoormap.route.Road;
import com.hunter.indoormap.route.Router;
import com.hunter.indoormap.route.RouterDataSource;
import com.hunter.indoormap.route.impl.AstarRouter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by hunter on 4/22/17.
 */

public class RouterOverlay extends Overlay implements Router{
    private static final String TAG = RouterOverlay.class.getSimpleName();
    private static final int WIDTH = 40;
    private static final RouterDataSource emptyDataSource = new RouterDataSource.EmptyDataSource();
    private RouterDataSource routerDataSource;
    private final MapView mapView;
    private Road road;
    private final com.hunter.indoormap.beans.Rect rect;

    Paint paint;
    Bitmap roadArrowBmp;
    Bitmap startBmp;
    Bitmap endBmp;
    Bitmap nextUpBmp;
    Bitmap nextDownBmp;
    Bitmap prevUpBmp;
    Bitmap prevDownBmp;

    View waitingView;
    View routerView;
    TextView distanceView;
    RelativeLayout.LayoutParams waitingViewlayoutParams;
    RelativeLayout.LayoutParams routerViewlayoutParams;

    public RouterOverlay(RouterDataSource routerDataSource, MapView mapView) {
        setRouterDataSource(routerDataSource);
        this.mapView = mapView;
        paint = new Paint();
        paint.setColor(Color.RED);
        final float density = mapView.getResources().getDisplayMetrics().density;
        int width = (int) (WIDTH * density);
        rect = new com.hunter.indoormap.beans.Rect(0, 0, width, width);

        roadArrowBmp = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.ic_road_arrow);
        startBmp = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.bubble_start);
        endBmp = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.bubble_end);

        nextUpBmp = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.bubble_next_up);
        nextDownBmp = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.bubble_next_down);

        prevUpBmp = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.bubble_prev_up);
        prevDownBmp = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.bubble_prev_down);

        waitingView = LayoutInflater.from(mapView.getContext()).inflate(R.layout.waiting_progress, (ViewGroup)(mapView.getParent()), false);
        routerView = LayoutInflater.from(mapView.getContext()).inflate(R.layout.overlay_router, (ViewGroup)(mapView.getParent()), false);
        routerView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitRoute();
            }
        });
        distanceView = (TextView) routerView.findViewById(R.id.text);
        routerViewlayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        routerViewlayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int margin = (int) (10 * density);
        routerViewlayoutParams.setMargins(margin, margin, margin, margin);

        waitingViewlayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        waitingViewlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
    }

    private void setSelectOverlayEnable(boolean enable) {
        Iterator<Overlay> overlayIterator = mapView.getOverlayManager().iterator();
        Overlay overlay;
        while (overlayIterator.hasNext()) {
            if ((overlay=overlayIterator.next()) instanceof SelectOverlay) {
                overlay.setEnabled(enable);
            }
        }
    }

    boolean routing;

    //UI
    private void startRoute(GPoint start, GPoint end) {
        routing = true;
        setSelectOverlayEnable(false);
        // show waiting view

        mapView.addView(waitingView, waitingViewlayoutParams);
        mapView.addView(routerView, this.routerViewlayoutParams);
        routeTask = new RouteTask();
        routeTask.executeOnExecutor(Executors.newFixedThreadPool(1), start, end);
    }

    RouteTask routeTask;
    //UI
    private void exitRoute() {
        routing = false;
        road = null;
        mapView.removeView(waitingView);
        mapView.removeView(routerView);
        setSelectOverlayEnable(true);
        if (routeTask != null && routeTask.getStatus() == AsyncTask.Status.RUNNING) {
            routeTask.cancel(true);
            routeTask = null;
        }
    }

    @Override
    public void draw(Canvas c, MapView mv) {
        if (routing && road != null) {
            distanceView.setText(mv.getContext().getString(R.string.distance_time, road.getLength(), road.getLength()/80));
            drawRoad(c, mv);
            drawBitmap(c, mv);
        }
    }

    private void drawBitmap(Canvas c, MapView mv) {
        WayNode[] wayNodes = road.getWayNodes();
        int level = mv.getLevel();
        for (int i=0; i<wayNodes.length; i++) {
            if (wayNodes[i].z == level) {
                if (i > 0) {
                    // prev
                    if (wayNodes[i-1].z < level) {
                        // GO DOWN
                        c.drawBitmap(prevDownBmp, null, getDownRect(wayNodes[i]), paint);
                    } else if (wayNodes[i-1].z > level) {
                        // GO UP
                        c.drawBitmap(prevUpBmp, null, getUpRect(wayNodes[i]), paint);
                    }
                }
                if (i+1 < wayNodes.length) {
                    // next
                    if (wayNodes[i+1].z > level) {
                        // GO UP
                        c.drawBitmap(nextUpBmp, null, getUpRect(wayNodes[i]), paint);
                    } else if (wayNodes[i+1].z < level) {
                        // GO DOWN
                        c.drawBitmap(nextDownBmp, null, getDownRect(wayNodes[i]), paint);
                    }
                }
                if (i == 0) {
                    // draw start
                    Point p = MatrixUtils.applyMatrix(wayNodes[0], mv.getMapMatrix()).offset(-rect.width()>>1, -rect.height());
                    c.drawBitmap(startBmp, null, new RectF(rect.left+p.x, rect.top+p.y, rect.right+p.x, rect.bottom+p.y), paint);
                }
                if (i+1 == wayNodes.length) {
                    // draw end
                    Point p = MatrixUtils.applyMatrix(wayNodes[wayNodes.length-1], mv.getMapMatrix()).offset(-rect.width()>>1, -rect.height());
                    c.drawBitmap(endBmp, null, new RectF(rect.left+p.x, rect.top+p.y, rect.right+p.x, rect.bottom+p.y), paint);
                }
            }
        }
    }

    private void drawRoad(Canvas c, MapView mv) {
        WayNode[] wayNodes = road.getWayNodes();
        int level = mv.getLevel();
        for (int i=0; i+1<wayNodes.length; i++) {
            if (wayNodes[i].z == level && wayNodes[i+1].z == level) {
                drawLine(wayNodes[i], wayNodes[i+1], c);
            }
        }
    }

    private void drawLine(WayNode start, WayNode end, Canvas c) {
        Point startPoint = MatrixUtils.applyMatrix(start, mapView.getMapMatrix());
        Point endPoint = MatrixUtils.applyMatrix(end, mapView.getMapMatrix());
        int width = (int) (start.getWide() * mapView.getScale());
        float degree = CoordinateUtils.calDegree(startPoint, endPoint);
        c.save();
        c.rotate(degree, startPoint.x, startPoint.y);
        endPoint = CoordinateUtils.rotateAtPoint(startPoint, endPoint, -degree, true);
        int halfWidth = width>>1;
        android.graphics.Rect rect = new Rect(-halfWidth, -halfWidth, halfWidth, halfWidth);
        rect.offset((int)(startPoint.x + halfWidth), (int)(startPoint.y));
        while (rect.right < endPoint.x) {
            c.drawBitmap(roadArrowBmp, null, rect, paint);
            rect.offset(width, 0);
        }

        rect.set(rect.left, rect.top, (int)endPoint.x, rect.bottom);
        if (rect.width() > halfWidth) {
            c.drawBitmap(roadArrowBmp, null, rect, paint);
        }
        c.restore();
    }

    private RectF getDownRect(GPoint gPoint) {
        Point p = MatrixUtils.applyMatrix(gPoint, mapView.getMapMatrix()).offset(-rect.width()>>1, 0);
        return new RectF(rect.left+p.x, rect.top+p.y, rect.right+p.x, rect.bottom+p.y);
    }

    private RectF getUpRect(GPoint gPoint) {
        Point p = MatrixUtils.applyMatrix(gPoint, mapView.getMapMatrix()).offset(-rect.width()>>1, -rect.height());
        return new RectF(rect.left+p.x, rect.top+p.y, rect.right+p.x, rect.bottom+p.y);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        if (road == null || road.getWayNodes() == null) {
            return false;
        }
        WayNode[] wayNodes = road.getWayNodes();
        int level = mapView.getLevel();
        RectF upRect;
        RectF downRect;
        for (int i=0; i<wayNodes.length; i++) {
            if (wayNodes[i].z == level) {
                upRect = getUpRect(wayNodes[i]);
                downRect = getDownRect(wayNodes[i]);
                // prev
                if (i > 0) {
                    if (wayNodes[i-1].z < level && downRect.contains(e.getX(), e.getY())) { // GO DOWN
                        mapView.setMapCenter(wayNodes[i-1]);
                        return true;

                    } else if (wayNodes[i-1].z > level && upRect.contains(e.getX(), e.getY())) { // GO UP
                        mapView.setMapCenter(wayNodes[i-1]);
                        return true;
                    }
                }
                // next
                if (i+1 < wayNodes.length) {
                    if (wayNodes[i+1].z > level && upRect.contains(e.getX(), e.getY())) { // GO UP
                        mapView.setMapCenter(wayNodes[i+1]);
                        return true;
                    } else if (wayNodes[i+1].z < level && downRect.contains(e.getX(), e.getY())){ // GO DOWN
                        mapView.setMapCenter(wayNodes[i+1]);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setRouterDataSource(RouterDataSource routerDataSource) {
        if (routerDataSource == null) {
            this.routerDataSource = emptyDataSource;
        } else {
            this.routerDataSource = routerDataSource;
        }
    }

    @Override
    public Road[] route(GPoint start, GPoint end, GPoint[] pass) {
        startRoute(start, end);
        return null;
    }

    class RouteTask extends AsyncTask<GPoint, Void, Road[]> {
        long MAX_TIME = 4000;

        @Override
        protected Road[] doInBackground(final GPoint... params) {
            if (params == null || params.length < 2) {
                return null;
            }
            Callable<Road[]> c = new Callable<Road[]>() {
                @Override
                public Road[] call() throws Exception {
                    Router router = new AstarRouter(routerDataSource);
                    return router.route(params[0], params[1], null);
                }
            };
            ExecutorService executor = Executors.newFixedThreadPool(1);
            Future<Road[]> futureTask = executor.submit(c);

            Road[] roads = null;
            try {
                roads = futureTask.get(MAX_TIME, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return roads;
        }

        @Override
        protected void onPostExecute(Road[] roads) {
            mapView.removeView(waitingView);
            if (roads != null && roads.length > 0 && roads[0] != null && roads[0].isValid()) {
                road = roads[0];
                mapView.setMapCenter(road.getWayNodes()[0]);
            } else {
                road = null;
                Toast.makeText(mapView.getContext(), R.string.no_road_found, Toast.LENGTH_SHORT).show();
                exitRoute();
            }
        }
    }
}
