package com.cloud.util;

public class UserIdHolder {
    private static final ThreadLocal<Long> holder = new ThreadLocal<Long>();

    public static void set(Long userId) {
        holder.set(userId);
    }

    public static Long get() {
        return holder.get();
    }

    public static void remove() {
        holder.remove();
    }

    public static void clear() {
        holder.remove();
    }
}
