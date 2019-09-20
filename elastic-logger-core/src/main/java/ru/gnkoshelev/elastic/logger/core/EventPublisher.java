package ru.gnkoshelev.elastic.logger.core;

import ru.gnkoshelev.elastic.logger.core.sender.HttpSender;
import ru.gnkoshelev.elastic.logger.core.sender.SyncHttpSender;
import ru.gnkoshelev.elastic.logger.core.time.TimeUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Gregory Koshelev
 */
public final class EventPublisher {
    private final int batchSize;
    private final long periodMs;

    private final Object monitor = new Object();
    private final AtomicInteger enqueuedBatchBasedProcesses = new AtomicInteger(0);
    private final ArrayBlockingQueue<Event> queue;
    private final ScheduledExecutorService executor;
    private final IndexPattern indexPattern;
    private final HttpSender httpSender;

    public EventPublisher(
            int batchSize,
            long periodMs,
            int capacity,
            int threadCount,
            String indexPattern,
            String url,
            String auth,
            int retryCount) {
        this.batchSize = batchSize;
        this.periodMs = periodMs;
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.executor = Executors.newScheduledThreadPool(threadCount, EventPublisherThreadFactory.getThreadFactory());
        this.indexPattern = IndexPattern.build(indexPattern);
        this.httpSender = new SyncHttpSender(url, auth, retryCount);
    }

    public void start() {
        executor.scheduleAtFixedRate(
                this::timeBasedProcess,
                periodMs,
                periodMs,
                TimeUnit.MILLISECONDS);


    }

    public void publish(Event event) {
        try {
            queue.add(event);

            int currentSize = queue.size();
            if (currentSize < batchSize) {
                return;
            }

            if (currentSize / batchSize <= enqueuedBatchBasedProcesses.get()) {
                return;
            }

            synchronized (monitor) {
                if (currentSize / batchSize > enqueuedBatchBasedProcesses.get()) {
                    enqueuedBatchBasedProcesses.incrementAndGet();
                    executor.execute(this::batchBasedProcess);
                }
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

    }

    private void timeBasedProcess() {
        try {
            process();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void batchBasedProcess() {
        try {
            process();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            enqueuedBatchBasedProcesses.decrementAndGet();
        }
    }

    private int process() {
        List<Event> events = new ArrayList<>(batchSize);
        int actualBatchSize = queue.drainTo(events, batchSize);

        if (actualBatchSize == 0) {
            return 0;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(Event.SOFT_UPPER_BOUND_EVENT_SIZE * actualBatchSize);
        try {
            for (Event event : events) {
                baos.write(INDEX_OPERATION);
                baos.write('\n');
                baos.write(event.toJsonString().getBytes(StandardCharsets.UTF_8));
                baos.write('\n');
            }
        } catch (IOException e) { /* It's impossible to catch IOEx here as BAOS do not throw IOEx in write method */}

        httpSender.send(indexPattern.format(), baos.toByteArray());

        return actualBatchSize;
    }

    public void stop(final long timeoutMs) {
        long startedAtNanos = System.nanoTime();
        long timeoutNanos = TimeUnit.MILLISECONDS.toNanos(timeoutMs);

        executor.shutdown();

        long elapsedTimeNanos = 0;
        long remainingTimeNanos = timeoutNanos;
        while (remainingTimeNanos > 0 && process() > 0) {
            elapsedTimeNanos = TimeUtil.elapsedTimeNanos(startedAtNanos);
            remainingTimeNanos = TimeUtil.remainingTimeOrZero(timeoutNanos, elapsedTimeNanos);
        }

        elapsedTimeNanos = TimeUtil.elapsedTimeNanos(startedAtNanos);
        remainingTimeNanos = TimeUtil.remainingTimeOrZero(timeoutNanos, elapsedTimeNanos);
        try {
            executor.awaitTermination(remainingTimeNanos, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        httpSender.close();
    }

    private static final byte[] INDEX_OPERATION = "{\"index\":{}}".getBytes(StandardCharsets.UTF_8);
}
