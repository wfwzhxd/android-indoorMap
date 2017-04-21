package com.hunter.indoormap;

import android.content.Context;
import android.graphics.Matrix;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.*;


/**
 * Created by hunter on 4/20/17.
 */

@RunWith(AndroidJUnit4.class)
public class MatrixTest {
    private static final String TAG = "MatrixTest";

    @Test
    public void matrixTest() {
        Matrix matrix = new Matrix();
        matrix.setScale(3, 4, 5, 5);
        Log.d(TAG, matrix.toString());
        float[] p = new float[]{7, 4, 2, 1};
        float[] p2 = new float[4];
        Log.d(TAG, Arrays.toString(p));
        matrix.mapPoints(p2, p);
        Log.d(TAG, Arrays.toString(p2));

        Matrix matrix2 = new Matrix();
        matrix.invert(matrix2);

        matrix2.mapPoints(p, p2);
        Log.d(TAG, Arrays.toString(p));

    }
}
