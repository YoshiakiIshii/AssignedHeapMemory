package com.example.mem;

import com.sun.management.ThreadMXBean;
import java.lang.management.ManagementFactory;

/**
 * Thread の割り当てバイト数を安全に取得するユーティリティ。
 */
public final class ThreadAllocation {
    private static final ThreadMXBean TMXB;

    static {
        ThreadMXBean tmp = null;
        try {
            tmp = ManagementFactory.getPlatformMXBean(ThreadMXBean.class);
            if (tmp != null && tmp.isThreadAllocatedMemorySupported()) {
                // 有効化されていない場合は有効にする
                tmp.setThreadAllocatedMemoryEnabled(true);
            }
        } catch (Throwable t) {
            // 別 JVM / セキュリティ設定等で取得できない場合に備える
        }
        TMXB = tmp;
    }

    private ThreadAllocation() {}

    public static boolean isSupported() {
        return TMXB != null && TMXB.isThreadAllocatedMemorySupported();
    }

    /**
     * 現在スレッドの累積割り当てバイト数を取得する（失敗時は -1）。
     */
    public static long currentThreadAllocatedBytes() {
        if (!isSupported()) return -1L;
        long tid = Thread.currentThread().getId();
        long bytes = TMXB.getThreadAllocatedBytes(tid);
        return bytes >= 0 ? bytes : -1L;
    }
}
