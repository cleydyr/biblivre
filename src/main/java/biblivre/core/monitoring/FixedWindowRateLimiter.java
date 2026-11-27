package biblivre.core.monitoring;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Per-key counter that resets every minute. Good enough to stop one noisy browser from flooding the
 * tunnel; not a precise limiter.
 */
public class FixedWindowRateLimiter {

    private static final long WINDOW_MILLIS = 60_000;
    private static final int MAX_TRACKED_KEYS = 10_000;

    private final Clock clock;
    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();
    private volatile long windowStart;

    public FixedWindowRateLimiter(Clock clock) {
        this.clock = clock;
        this.windowStart = currentWindow();
    }

    public boolean tryAcquire(String key, int limitPerMinute) {
        rollWindow();

        if (counters.size() >= MAX_TRACKED_KEYS && !counters.containsKey(key)) {
            return false;
        }

        return counters.computeIfAbsent(key, k -> new AtomicInteger()).incrementAndGet()
                <= limitPerMinute;
    }

    private void rollWindow() {
        long window = currentWindow();

        if (window != windowStart) {
            synchronized (this) {
                if (window != windowStart) {
                    counters.clear();
                    windowStart = window;
                }
            }
        }
    }

    private long currentWindow() {
        return clock.millis() / WINDOW_MILLIS;
    }
}
