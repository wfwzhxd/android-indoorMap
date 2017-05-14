package com.hunter.indoormap.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.hunter.indoormap.CoordinateUtils;
import com.hunter.indoormap.MapView;
import com.hunter.indoormap.MatrixUtils;
import com.hunter.indoormap.beans.Line;
import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Point;
import static com.hunter.indoormap.beans.Way.*;

import com.hunter.indoormap.beans.Way;

import java.util.List;

/**
 * Created by hunter on 5/11/17.
 */

public class NameOverlay extends Overlay {
    private static final String TAG = NameOverlay.class.getSimpleName();

    private static final int DEFAULT_SMALLEST_FONTSIZE = 10;
    private static final int DEFAULT_BIGGEST_FONTSIZE = 20;

    final float fontScale;

    TextPaint smallestPaint;
    TextPaint biggestPaint;

    int smallestFontSize = DEFAULT_SMALLEST_FONTSIZE;
    int biggestFontSize = DEFAULT_BIGGEST_FONTSIZE;

    public NameOverlay(Context context) {
        fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        smallestPaint = new TextPaint();
        smallestPaint.bgColor = Color.WHITE;
        smallestPaint.setColor(Color.BLACK);
        biggestPaint = new TextPaint(smallestPaint);
        setSmallestFontSize(DEFAULT_SMALLEST_FONTSIZE);
        setBiggestFontSize(DEFAULT_BIGGEST_FONTSIZE);
    }

    @Override
    public void draw(Canvas c, MapView mv) {
        List<Node> nodes = mv.getDataSource().getNodes(mv.getMapRect().enlarge(1.1f), mv.getLevel());
        if (nodes != null) {
            for (Node node : nodes) {
                if (!TextUtils.isEmpty(node.getName()) && !node.getBounds().isEmpty()) {
                    drawNodeName(node, mv.getMapMatrix(), c);
                }
            }
        }

        List<Way> ways = mv.getDataSource().getWays(mv.getMapRect().enlarge(1.1f), mv.getLevel());
        if (ways != null) {
            for (Way way : ways) {
                if (!TextUtils.isEmpty(way.getName())) {
                    drawWayName(way, mv, c);
                }
            }
        }
    }

    private void drawWayName(Way way, MapView mv, Canvas c) {
        WayLine[] wayLines = way.getWayLines();
        if (wayLines == null || wayLines.length == 0) {
            return;
        }
        for (WayLine wayLine : wayLines) {
            drawWayLineName(wayLine, way.getName(), mv, c);
        }
    }

    private void drawWayLineName(WayLine wayLine, CharSequence name, MapView mv, Canvas canvas) {
        if (wayLine.getEdges() == null) {
            return;
        }

        float maxHeight = (wayLine.getStart().getWide() + wayLine.getEnd().getWide())*mv.getScale()/2;
        float maxLength = CoordinateUtils.calDistance(wayLine.getStart(), wayLine.getEnd())*mv.getScale();
        if (maxHeight > getTextSize(smallestPaint) && maxLength > getTextSize(smallestPaint)*name.length()) {
            Paint paint = smallestPaint;
            if (maxHeight > getTextSize(biggestPaint) && maxLength > getTextSize(biggestPaint)*name.length()) {
                paint = biggestPaint;
            }
            Paint.Align prevAlign = paint.getTextAlign();
            paint.setTextAlign(Paint.Align.CENTER);
            PositionGet positionGet = new PositionGet(MatrixUtils.applyMatrix(wayLine, mv.getMapMatrix()), paint);
            Point point;
            for (int i=0; i<name.length(); i++) {
                point = positionGet.getPosition(i, name.length());
                canvas.drawText(String.valueOf(name.charAt(i)), point.x, point.y, paint);
            }
            paint.setTextAlign(prevAlign);
        }

    }

    private float getTextSize(Paint paint) {
        return (float) (1.4*paint.getTextSize());
    }

    private class PositionGet {
        Line line;
        Paint paint;
        float degree;
        float threshold = 30f;
        double cosDegree;
        double sinDegree;
        Point midPoint;

        public PositionGet(Line line, Paint paint) {
            if (line.getStart().y > line.getEnd().y) {
                this.line = new Line(line.getEnd(), line.getStart());
            } else {
                this.line = line;
            }
            this.paint = paint;
            degree = CoordinateUtils.calDegree(line.getStart(), line.getEnd());
            while (degree>180) {
                degree -= 180;
            }
            cosDegree = Math.cos(CoordinateUtils.degree2radians(degree));
            sinDegree = Math.sin(CoordinateUtils.degree2radians(degree));
            midPoint = new Point((line.getStart().x+line.getEnd().x)/2, (line.getStart().y+line.getEnd().y)/2);
        }

        public Point getPosition(int index, int length) {
            int midIndex = length%2 == 0 ? length>>1 : (length + 1)>>1;
            midIndex -= 1;
            float secLength = (midIndex-index) * getTextSize(paint);
            float dx = (float) (cosDegree * secLength);
            float dy = (float) (sinDegree * secLength);
            if (180 - degree < threshold) {
                return new Point(midPoint.x + dx, midPoint.y + dy);
            } else {
                return new Point(midPoint.x - dx, midPoint.y - dy);
            }
        }
    }


    private void drawNodeName(Node node, Matrix matrix, Canvas c) {
        RectF rectF = new RectF();
        matrix.mapRect(rectF, node.getBounds().toRectF());
        StaticLayout smallStaticLayout;
        StaticLayout bigStaticLayout;
        StaticLayout staticLayout;
        if ((smallStaticLayout = canShow(node.getName(), smallestPaint, rectF)) != null) {
            if ((bigStaticLayout = canShow(node.getName(), biggestPaint, rectF)) != null) {
                staticLayout = bigStaticLayout;
            } else {
                staticLayout = smallStaticLayout;
            }
        } else {
            return;
        }
        Point point = MatrixUtils.applyMatrix(node.getXyz(), matrix);
        c.save();
        c.translate(point.x - (staticLayout.getWidth()>>1), point.y - (staticLayout.getHeight()>>1));
        staticLayout.draw(c);
        c.restore();
    }

    private StaticLayout canShow(CharSequence str, TextPaint textPaint, RectF bounds) {
        StaticLayout staticLayout = new StaticLayout(str, 0, str.length(), textPaint, (int) bounds.width(), Layout.Alignment.ALIGN_CENTER, 1f, 0f, true);
        if (staticLayout.getHeight() <= bounds.height()) {
            return staticLayout;
        }
        return null;
    }

    public int getSmallestFontSize() {
        return smallestFontSize;
    }

    public void setSmallestFontSize(int smallestFontSize) {
        this.smallestFontSize = smallestFontSize;
        smallestPaint.setTextSize((int) (smallestFontSize * fontScale + 0.5f));
    }

    public int getBiggestFontSize() {
        return biggestFontSize;
    }

    public void setBiggestFontSize(int biggestFontSize) {
        this.biggestFontSize = biggestFontSize;
        biggestPaint.setTextSize((int) (biggestFontSize * fontScale + 0.5f));
    }
}
