package com.hunter.indoormap.beans;

import android.graphics.RectF;

import java.io.PrintWriter;


public final class Rect {
    public int left;
    public int top;
    public int right;
    public int bottom;

    public Rect(RectF rectF) {
        this(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    public Rect(android.graphics.Rect rect) {
        this(rect.left, rect.top, rect.right, rect.bottom);
    }

    /**
     * Create a new empty Rect. All coordinates are initialized to 0.
     */
    public Rect() {}

    /**
     * Create a new rectangle with the specified coordinates. Note: no range
     * checking is performed, so the caller must ensure that left <= right and
     * top <= bottom.
     *
     * @param left   The X coordinate of the left side of the rectangle
     * @param top    The Y coordinate of the top of the rectangle
     * @param right  The X coordinate of the right side of the rectangle
     * @param bottom The Y coordinate of the bottom of the rectangle
     */
    public Rect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public Rect(float left, float top, float right, float bottom) {
        this.left = Math.round(left);
        this.top = Math.round(top);
        this.right = Math.round(right);
        this.bottom = Math.round(bottom);
    }

    /**
     * Create a new rectangle, initialized with the values in the specified
     * rectangle (which is left unmodified).
     *
     * @param r The rectangle whose coordinates are copied into the new
     *          rectangle.
     */
    public Rect(Rect r) {
        if (r == null) {
            left = top = right = bottom = 0;
        } else {
            left = r.left;
            top = r.top;
            right = r.right;
            bottom = r.bottom;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rect r = (Rect) o;
        return left == r.left && top == r.top && right == r.right && bottom == r.bottom;
    }

    @Override
    public int hashCode() {
        int result = left;
        result = 31 * result + top;
        result = 31 * result + right;
        result = 31 * result + bottom;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(32);
        sb.append("Rect("); sb.append(left); sb.append(", ");
        sb.append(top); sb.append(" - "); sb.append(right);
        sb.append(", "); sb.append(bottom); sb.append(")");
        return sb.toString();
    }

    /**
     * Return a string representation of the rectangle in a compact form.
     */
    public String toShortString() {
        return toShortString(new StringBuilder(32));
    }

    /**
     * Return a string representation of the rectangle in a compact form.
     * @hide
     */
    public String toShortString(StringBuilder sb) {
        sb.setLength(0);
        sb.append('['); sb.append(left); sb.append(',');
        sb.append(top); sb.append("]["); sb.append(right);
        sb.append(','); sb.append(bottom); sb.append(']');
        return sb.toString();
    }

    /**
     * Print short representation to given writer.
     * @hide
     */
    public void printShortString(PrintWriter pw) {
        pw.print('['); pw.print(left); pw.print(',');
        pw.print(top); pw.print("]["); pw.print(right);
        pw.print(','); pw.print(bottom); pw.print(']');
    }

    /**
     * Returns true if the rectangle is empty (left > right or top > bottom)
     */
    public final boolean isEmpty() {
        return left > right || top > bottom;
    }

    /**
     * @return the rectangle's width. This does not check for a valid rectangle
     * (i.e. left <= right) so the result may be negative.
     */
    public final int width() {
        return right - left;
    }

    /**
     * @return the rectangle's height. This does not check for a valid rectangle
     * (i.e. top <= bottom) so the result may be negative.
     */
    public final int height() {
        return bottom - top;
    }

    /**
     * @return the horizontal center of the rectangle. If the computed value
     *         is fractional, this method returns the largest integer that is
     *         less than the computed value.
     */
    public final int centerX() {
        return (left + right) >> 1;
    }

    /**
     * @return the vertical center of the rectangle. If the computed value
     *         is fractional, this method returns the largest integer that is
     *         less than the computed value.
     */
    public final int centerY() {
        return (top + bottom) >> 1;
    }

    /**
     * @return the exact horizontal center of the rectangle as a float.
     */
    public final float exactCenterX() {
        return (left + right) * 0.5f;
    }

    /**
     * @return the exact vertical center of the rectangle as a float.
     */
    public final float exactCenterY() {
        return (top + bottom) * 0.5f;
    }

    /**
     * Set the rectangle to (0,0,0,0)
     */
    public void setEmpty() {
        left = right = top = bottom = 0;
    }

    /**
     * Set the rectangle's coordinates to the specified values. Note: no range
     * checking is performed, so it is up to the caller to ensure that
     * left <= right and top <= bottom.
     *
     * @param left   The X coordinate of the left side of the rectangle
     * @param top    The Y coordinate of the top of the rectangle
     * @param right  The X coordinate of the right side of the rectangle
     * @param bottom The Y coordinate of the bottom of the rectangle
     */
    public void set(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /**
     * Copy the coordinates from src into this rectangle.
     *
     * @param src The rectangle whose coordinates are copied into this
     *           rectangle.
     */
    public void set(android.graphics.Rect src) {
        this.left = src.left;
        this.top = src.top;
        this.right = src.right;
        this.bottom = src.bottom;
    }

    /**
     * Offset the rectangle by adding dx to its left and right coordinates, and
     * adding dy to its top and bottom coordinates.
     *
     * @param dx The amount to add to the rectangle's left and right coordinates
     * @param dy The amount to add to the rectangle's top and bottom coordinates
     */
    public Rect offset(int dx, int dy) {
        left += dx;
        top += dy;
        right += dx;
        bottom += dy;
        return this;
    }

    public Rect offset(float dx, float dy) {
        return offset((int)dx, (int)dy);
    }

    /**
     * Offset the rectangle to a specific (left, top) position,
     * keeping its width and height the same.
     *
     * @param newLeft   The new "left" coordinate for the rectangle
     * @param newTop    The new "top" coordinate for the rectangle
     */
    public void offsetTo(int newLeft, int newTop) {
        right += newLeft - left;
        bottom += newTop - top;
        left = newLeft;
        top = newTop;
    }

    public boolean contains(int x, int y) {
        return left <= right && top <= bottom  // check for empty first
                && x >= left && x <= right && y >= top && y <= bottom;
    }

    public boolean contains(float x, float y) {
        return contains((int) x, (int)y);
    }

    public boolean contains(Point point) {
        return contains(point.x, point.y);
    }

    /**
     * Returns true iff the two specified rectangles intersect. In no event are
     * either of the rectangles modified.
     *
     * @param a The first rectangle being tested for intersection
     * @param b The second rectangle being tested for intersection
     * @return true iff the two specified rectangles intersect. In no event are
     *              either of the rectangles modified.
     */
    public static boolean intersects(Rect a, Rect b) {
        return a.left <= b.right && b.left <= a.right && a.top <= b.bottom && b.top <= a.bottom;
    }

    /**
     * Update this Rect to enclose itself and the specified rectangle. If the
     * specified rectangle is empty, nothing is done. If this rectangle is empty
     * it is set to the specified rectangle.
     *
     * @param left The left edge being unioned with this rectangle
     * @param top The top edge being unioned with this rectangle
     * @param right The right edge being unioned with this rectangle
     * @param bottom The bottom edge being unioned with this rectangle
     */
    public void union(int left, int top, int right, int bottom) {
        if ((left < right) && (top < bottom)) {
            if ((this.left < this.right) && (this.top < this.bottom)) {
                if (this.left > left) this.left = left;
                if (this.top > top) this.top = top;
                if (this.right < right) this.right = right;
                if (this.bottom < bottom) this.bottom = bottom;
            } else {
                this.left = left;
                this.top = top;
                this.right = right;
                this.bottom = bottom;
            }
        }
    }

    /**
     * Swap top/bottom or left/right if there are flipped (i.e. left > right
     * and/or top > bottom). This can be called if
     * the edges are computed separately, and may have crossed over each other.
     * If the edges are already correct (i.e. left <= right and top <= bottom)
     * then nothing is done.
     */
    public void sort() {
        if (left > right) {
            int temp = left;
            left = right;
            right = temp;
        }
        if (top > bottom) {
            int temp = top;
            top = bottom;
            bottom = temp;
        }
    }

    /**
     * Parcelable interface methods
     */
    public int describeContents() {
        return 0;
    }

    /**
     * Scales up the rect by the given scale.
     * @hide
     */
    public Rect scale(float scale) {
        if (scale != 1.0f) {
            left = (int) (left * scale + 0.5f);
            top = (int) (top * scale + 0.5f);
            right = (int) (right * scale + 0.5f);
            bottom = (int) (bottom * scale + 0.5f);
        }
        return this;
    }

    public Rect enlarge(float scale) {
        if (scale != 1.0f) {
            left += (int) (width() * (1-scale) + 0.5f);
            top += (int) (height() * (1-scale) + 0.5f);
            right += (int) (width() * (scale-1) + 0.5f);
            bottom += (int) (height() * (scale-1) + 0.5f);
        }
        return this;
    }

    public RectF toRectF() {
        return new RectF(left, top, right, bottom);
    }

    public android.graphics.Rect toRect() {
        return new android.graphics.Rect(left, top, right, bottom);
    }

    public static Rect mixMax(Rect rect1, Rect rect2) {
        if (rect1 == null && rect2 == null) {
            return null;
        } else if (rect1 == null) {
            return rect2;
        } else if (rect2 == null) {
            return rect1;
        }
        Rect r = new Rect();
        r.left = Math.min(rect1.left, rect2.left);
        r.top = Math.min(rect1.top, rect2.top);
        r.right = Math.max(rect1.right, rect2.right);
        r.bottom = Math.max(rect1.bottom, rect2.bottom);
        return r;
    }

}

