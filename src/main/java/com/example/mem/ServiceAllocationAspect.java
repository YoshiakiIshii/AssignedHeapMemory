package com.example.mem;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Service に対する @Around アドバイスでメソッド単位の割り当て差分を記録する。
 */
@Aspect
@Component
public class ServiceAllocationAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceAllocationAspect.class);

    // 対象：@Service が付与されたクラス
    @Around("within(@org.springframework.stereotype.Service *)")
    public Object logServiceAllocation(ProceedingJoinPoint pjp) throws Throwable {
        if (!ThreadAllocation.isSupported()) {
            return pjp.proceed();
        }

        AllocationTracker.start();
        boolean success = false;
        try {
            Object ret = pjp.proceed();
            success = true;
            return ret;
        } finally {
            long delta = AllocationTracker.stopAndGetDelta();
            AllocationTracker.clear();
            String sig = pjp.getSignature().toString();

            if (delta >= 0) {
                log.info("SVC mem_alloc_bytes={} method={} success={}", delta, sig, success);
            } else {
                log.debug("ThreadAllocatedBytes unsupported during service call: {}", sig);
            }
        }
    }
}
