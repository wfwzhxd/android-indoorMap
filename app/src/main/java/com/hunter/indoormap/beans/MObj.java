package com.hunter.indoormap.beans;

/**
 * Created by hunter on 3/25/17.
 */

public class MObj extends Tagger{
    int id;
    String name;
    transient Rect bounds;

    public MObj() {
        this(ID.NONE_ID, null);
    }

    public MObj(int id) {
        this(id, null);
    }

    public MObj(int id, String name) {
        this.id = id;
        this.name = name;
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

    /**
     * Test whether the given {@link Point} is in the range of this Object.
     * @param point
     * @return
     */
    public boolean contains(Point point) {
        if (getBounds().contains(point.x, point.y)) {
            return ifContains(point);
        }
        return false;
    }

    protected boolean ifContains(Point point) {
        return false;
    };

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
