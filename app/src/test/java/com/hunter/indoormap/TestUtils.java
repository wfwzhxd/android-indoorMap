package com.hunter.indoormap;

import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Point;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by hunter on 4/12/17.
 */

public class TestUtils {
    public static Point p(float x, float y) {
        return new Point(x, y);
    }

    public static GPoint gp(int x, int y) {
        return new GPoint(x, y);
    }

    public static GPoint gp(float x, float y, int z) {
        return new GPoint(x, y, z);
    }

    static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {}
        return null;
    }

    static Field getField(Class<?> clazz, String fieldName) {
        Field result = null;
        while (result == null || !clazz.equals(Object.class)) {
            try {
                result = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {}
            clazz = clazz.getSuperclass();
        }
        return result;
    }

    static Object invoke(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Object invoke(Method method, Object target, Object... objs) {
        try {
            return method.invoke(target, objs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
