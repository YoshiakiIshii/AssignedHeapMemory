package com.example.mem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * ネストを考慮した開始・終了の差分計測用トラッカー。
 */
public class AllocationTracker {
    private static final Logger log = LoggerFactory.getLogger(AllocationTracker.class);

    private static final ThreadLocal<Deque<Long>> STACK = ThreadLocal.withInitial(ArrayDeque::new);

    /** 計測開始（スタックに現在バイト数を push） */
    public static void start() {
        long now = ThreadAllocation.currentThreadAllocatedBytes();
        STACK.get().push(now);
    }

    /**
     * 計測終了（スタックから pop して差分計算）。サポートされない場合は -1 を返す。
     */
    public static long stopAndGetDelta() {
        Deque<Long> stack = STACK.get();
        if (stack.isEmpty()) {
            log.debug("Allocation stack is empty; start() was not called?");
            return -1L;
        }
        long start = stack.pop();
        long end = ThreadAllocation.currentThreadAllocatedBytes();
        if (start < 0 || end < 0) {
            return -1L;
        }
        return Math.max(0, end - start);
    }

    /** クリーンアップ（例外や afterCompletion で確実に呼ぶ） */
    public static void clear() {
        STACK.remove();
    }
}
