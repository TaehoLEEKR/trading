package com.trade.md.kis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

// 초당 거래수 초과
@Component
public class KisRateGate {
    private long intervalNanos;
    private final AtomicLong nextAllowed = new AtomicLong(System.nanoTime());

    public KisRateGate(@Value("${trade.kis.rate.qps:3}") double qps) {
        if (qps <= 0){
            qps = 1;
        }
        this.intervalNanos = (long) (1_000_000_000L / qps);
    }

    public void acquire() {
        while (true) {
            long now = System.nanoTime();
            long prev = nextAllowed.get();
            long target = Math.max(now, prev);
            long next = target + intervalNanos;

            if (nextAllowed.compareAndSet(prev, next)) {
                long wait = target - now;
                if (wait > 0) {
                    LockSupport.parkNanos(wait);
                }
                return;
            }
        }
    }

}
