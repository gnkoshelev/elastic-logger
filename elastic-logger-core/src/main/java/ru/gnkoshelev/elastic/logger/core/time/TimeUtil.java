package ru.gnkoshelev.elastic.logger.core.time;

/**
 * @author Gregory Koshelev
 */
public class TimeUtil {
    public static long elapsedTimeNanos(long startedAtNanos) {
        return System.nanoTime() - startedAtNanos;
    }

    public static long remainingTimeOrZero(long timeout, long elapsedTime) {
        return Math.max(timeout - elapsedTime, 0L);
    }
}
