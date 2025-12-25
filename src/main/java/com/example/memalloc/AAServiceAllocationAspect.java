package com.example.memalloc;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Service に対する @Around アドバイスでメソッド単位の割り当て差分を記録する。
 */
@Aspect
@Component
public class AAServiceAllocationAspect {

    private static final Logger log = LoggerFactory.getLogger(AAServiceAllocationAspect.class);

    @Value("${aa.memalloc.monitor-threshold:0}")
    private long thresholdBytes;

    // 対象：@Service が付与されたクラス
    @Around("within(@org.springframework.stereotype.Service *)")
    public Object logServiceAllocation(ProceedingJoinPoint pjp) throws Throwable {
        if (!AAThreadAllocation.isSupported()) {
            return pjp.proceed();
        }

        AAAllocationTracker.start();
        boolean success = false;
        try {
            Object ret = pjp.proceed();
            success = true;
            return ret;
        } finally {
            long delta = AAAllocationTracker.stopAndGetDelta();
            AAAllocationTracker.clear();
            String sig = pjp.getSignature().toString().replace(',', '+'); // ログの区切り文字対策

            if (delta > thresholdBytes) {
                log.info("mem_alloc_bytes={};method={};success={}", delta, sig, success);

                // JFR イベントの記録
                AAServiceMethodMemoryAllocEvent event = new AAServiceMethodMemoryAllocEvent();
                event.allocatedBytes = delta;
                event.methodSignature = sig;
                event.success = success;
                event.commit();
            }
        }
    }
}
