package com.doraemon.monitor.util;

import java.util.Collection;
import java.util.Map;

/**
 * Created by zbs on 2017/8/9.
 */
public class Helpers {

    /**
     * 断言指定的对象不为空
     *
     * @param o   待检测对象
     * @param <T> 待检测对象类型
     * @return 如果对象不为空则返回
     */
    public static <T> T assertNotNull(T o) {
        if (o == null) {
            throw new MyException(MyEnum.EMPTY_OBJECT);
        }
        return o;
    }

    /**
     * 断言集合不为空
     *
     * @param o
     * @param <T>
     * @return
     */
    public static <T extends Collection<?>> T assertNotNullOrEmpty(T o) {
        if (o == null || o.isEmpty()) {
            throw new MyException(MyEnum.EMPTY_OBJECT);
        }
        return o;
    }

    /**
     * 断言Map不为空
     *
     * @param o
     * @param <T>
     * @return
     */
    public static <T extends Map<?, ?>> T assertNotNullOrEmpty(T o) {
        if (o == null || o.isEmpty()) {
            throw new MyException(MyEnum.EMPTY_OBJECT);
        }
        return o;
    }

    /**
     * 断言字符串不为空
     *
     * @param o
     * @return
     */
    public static String assertNotNullOrEmpty(String o) {
        if (o == null || o.length() == 0) {
            throw new MyException(MyEnum.EMPTY_OBJECT);
        }
        return o;
    }

    /**
     * 如果目标值为空， 则使用默认值
     *
     * @param value
     * @param defaultValue 默认值
     * @param <T>          类型
     * @return 目标值 或者 默认值
     */
    public static <T> T getDefaultValueIfNull(T value, T defaultValue) {
        assertNotNull(defaultValue);
        return value == null ? defaultValue : value;
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static boolean isNullOrEmpty(String o) {
        return o == null || o.length() == 0;
    }

    public static boolean isNullOrEmpty(Object... o) {
        return o == null || o.length == 0;
    }

    public static boolean isNullOrEmpty(Collection<?> o) {
        return o == null || o.isEmpty();
    }

    public static boolean isNullOrEmpty(Map<?, ?> o) {
        return o == null || o.isEmpty();
    }

    /**
     * 比较两对象
     *
     * @param o1
     * @param o2
     * @param <T>
     * @return
     */
    public static <T extends Comparable> int compare(T o1, T o2) {
        return o1.compareTo(o2);
    }

    /**
     * 取两对象中较小的
     *
     * @param o1
     * @param o2
     * @param <T>
     * @return
     */
    public static <T extends Comparable> T minOf(T o1, T o2) {
        return compare(o1, o2) < 0 ? o1 : o2;
    }

    /**
     * 取两对象中较大的
     *
     * @param o1
     * @param o2
     * @param <T>
     * @return
     */
    public static <T extends Comparable> T maxOf(T o1, T o2) {
        return compare(o1, o2) > 0 ? o1 : o2;
    }
}
