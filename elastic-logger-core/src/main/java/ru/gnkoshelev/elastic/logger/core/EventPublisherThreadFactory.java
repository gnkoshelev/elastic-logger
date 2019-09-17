package ru.gnkoshelev.elastic.logger.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Gregory Koshelev
 */
public class EventPublisherThreadFactory implements ThreadFactory {
    private static final ThreadFactory THREAD_FACTORY = new EventPublisherThreadFactory();

    private final AtomicInteger threadIndex = new AtomicInteger(0);

    static ThreadFactory getThreadFactory() {
        return THREAD_FACTORY;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(
                Thread.currentThread().getThreadGroup(),
                r,
                "event-publisher-thread-" + threadIndex.getAndIncrement(),
                0);
        t.setDaemon(true);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }

    private EventPublisherThreadFactory() {
    }
}
