package ru.gnkoshelev.elastic.logger.core;

/**
 * @author Gregory Koshelev
 */
public final class DefaultConfig {
    public static final long SHUTDOWN_TIMEOUT_MS = 2_000;

    public static final int BATCH_SIZE = 100;
    public static final long PERIOD_MS = 1_000;
    public static final int CAPACITY = 1_000_000;
    public static final int THREAD_COUNT = 1;
    public static final int RETRY_COUNT = 0;

    private DefaultConfig() {
        /* static class */
    }
}
