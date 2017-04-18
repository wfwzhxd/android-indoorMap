package com.hunter.indoormap;

/**
 * Created by hunter on 4/17/17.
 */

public class MathUtils {

    public static boolean isEqual(float a, float b) {
        return Math.abs(a - b) < 0.001;
    }
}
