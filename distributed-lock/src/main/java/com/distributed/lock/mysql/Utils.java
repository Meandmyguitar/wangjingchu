package com.distributed.lock.mysql;

import java.util.Arrays;

public class Utils {
    public static void gracefulClose(AutoCloseable... closeables) {
        Arrays.asList(closeables).forEach(closeable -> {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                }
            }
        });
    }
}
