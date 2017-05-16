package com.hunter.indoormap.beans;

/**
 * Created by hunter on 5/15/17.
 */

public interface Shape<E extends Point> {

    /**
     * Just 2D bounds
     * @return
     */
    Rect getBounds();

    boolean contains(E e);
}
