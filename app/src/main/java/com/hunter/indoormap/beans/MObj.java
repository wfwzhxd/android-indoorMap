package com.hunter.indoormap.beans;

/**
 * Created by hunter on 3/25/17.
 */

public abstract class MObj {
    int id;
    String name;
    boolean show;
    transient Rect bounds;

    public MObj(int id) {
        this(id, null);
    }

    public MObj(int id, String name) {
        this(id, name, true);
    }

    public MObj(int id, String name, boolean show) {
        this.id = id;
        this.name = name;
        this.show = show;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
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
     * @return a <code>Rect</code> that defines the bound of this
     * <code>MObj</code>.
     */
    public Rect getBounds() {
        if (bounds == null) {
            calculateBounds();
        }
        return bounds;
    }

    protected void calculateBounds() {}

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof MObj)) {
            return false;
        }
        MObj obj2 = (MObj) obj;
        return id == obj2.id;
    }
}
