package ru.gnkoshelev.elastic.logger.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ru.gnkoshelev.elastic.logger.core.DefaultConfig;
import ru.gnkoshelev.elastic.logger.core.EventPublisher;

/**
 * @author Gregory Koshelev
 */
public class LogbackElasticAppender<E> extends UnsynchronizedAppenderBase<E> {
    private volatile LogbackElasticConfiguration configuration;
    private volatile EventPublisher eventPublisher;
    private volatile long shutdownTimeoutMs;

    @Override
    protected void append(E eventObject) {
        if (eventObject instanceof ILoggingEvent) {
            eventPublisher.publish(LogbackEventUtil.build((ILoggingEvent) eventObject));
        }
    }

    @Override
    public void start() {
        if (configuration == null) {
            throw new IllegalStateException("LogbackElasticAppender cannot run without configuration");
        }
        if (configuration.getUrl() == null) {
            throw new IllegalStateException("LogbackElasticAppender cannot run without configured URL");
        }
        if (configuration.getIndexPattern() == null) {
            throw new IllegalStateException("LogbackElasticAppender cannot run without configured Index Pattern");
        }

        shutdownTimeoutMs =
                configuration.getShutdownTimeoutMs() < 0
                        ? DefaultConfig.SHUTDOWN_TIMEOUT_MS
                        : configuration.getShutdownTimeoutMs();

        eventPublisher =
                new EventPublisher(
                        configuration.getBatchSize() <= 0 ? DefaultConfig.BATCH_SIZE : configuration.getBatchSize(),
                        configuration.getPeriodMs() <= 0 ? DefaultConfig.PERIOD_MS : configuration.getPeriodMs(),
                        configuration.getCapacity() <= 0 ? DefaultConfig.CAPACITY : configuration.getCapacity(),
                        configuration.getThreadCount() <= 0 ? DefaultConfig.THREAD_COUNT : configuration.getThreadCount(),
                        configuration.getIndexPattern(),
                        configuration.getUrl(),
                        configuration.getApiKey(),
                        configuration.getRetryCount() < 0 ? DefaultConfig.RETRY_COUNT : configuration.getRetryCount());
        eventPublisher.start();

        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        eventPublisher.stop(shutdownTimeoutMs);
    }

    public void setConfiguration(LogbackElasticConfiguration configuration) {
        this.configuration = configuration;
    }
}
