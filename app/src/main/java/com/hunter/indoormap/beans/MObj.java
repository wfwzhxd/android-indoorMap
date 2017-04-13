package com.hunter.indoormap.beans;

/**
 * Created by hunter on 3/25/17.
 */

public abstract class MObj {
    int id;
    String name;
    boolean show = true;
    Rect bounds;

    public MObj(int id) {
        this.id = id;
    }

    public MObj(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Test whether the given {@link Point} is in the range of this Object.
     * @param point
     * @return
     */
    public abstract boolean contains(Point point);

    /**
     * Gets the bounding box of this <code>MObj</code>.
     * The bounding box is the smallest {@link Rect} whose
     * sides are parallel to the x and y axes of the
     * coordinate space, and can completely contain the <code>MObj</code>.
     * @return a <code>Rect</code> that defines the bounds of this
     * <code>MObj</code>.
     */
    public Rect getBounds() {
        if (bounds == null) {
            calculateBounds();
        }
        return bounds;
    }

    protected abstract void calculateBounds();
}
