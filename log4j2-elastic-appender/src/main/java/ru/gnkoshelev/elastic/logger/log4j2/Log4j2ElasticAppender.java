package ru.gnkoshelev.elastic.logger.log4j2;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import ru.gnkoshelev.elastic.logger.core.DefaultConfig;
import ru.gnkoshelev.elastic.logger.core.EventPublisher;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author Gregory Koshelev
 */
@Plugin(name = "Elastic", category = "Core", elementType = "appender", printObject = false)
public final class Log4j2ElasticAppender extends AbstractAppender {
    private EventPublisher eventPublisher;
    private final long shutdownTimeoutMs;

    private Log4j2ElasticAppender(
            String name,
            Filter filter,
            Layout<? extends Serializable> layout,

            long shutdownTimeoutMs,

            int batchSize,
            long periodMs,
            int capacity,
            int threadCount,
            String indexPattern,
            String url,
            String apiKey,
            int retryCount) {
        super(name, filter, layout);
        this.shutdownTimeoutMs = shutdownTimeoutMs;

        eventPublisher =
                new EventPublisher(
                        batchSize,
                        periodMs,
                        capacity,
                        threadCount,
                        indexPattern,
                        url,
                        apiKey,
                        retryCount);
        eventPublisher.start();
    }

    @PluginFactory
    public static Log4j2ElasticAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") final Filter filter,
            @PluginElement("Layout") Layout<? extends Serializable> layout,

            @PluginAttribute("shutdownTimeoutMs") long shutdownTimeoutMs,

            @PluginAttribute("batchSize") final int batchSize,
            @PluginAttribute("periodMs") final long periodMs,
            @PluginAttribute("capacity") final int capacity,
            @PluginAttribute("threadCount") final int threadCount,
            @PluginAttribute("indexPattern") String indexPattern,
            @PluginAttribute("url") final String url,
            @PluginAttribute("apiKey") final String apiKey,
            @PluginAttribute("retryCount") final int retryCount) {
        if (name == null) {
            LOGGER.error("No name is set for Log4j2ElasticAppender");
            return null;
        }

        if (url == null) {
            LOGGER.error("No url is set for Log4j2ElasticAppender");
            return null;
        }

        if (indexPattern == null) {
            LOGGER.error("No indexPattern is set for Log4j2ElasticAppender");
            return null;
        }

        return new Log4j2ElasticAppender(
                name,
                filter,
                layout,

                shutdownTimeoutMs < 0 ? DefaultConfig.SHUTDOWN_TIMEOUT_MS : shutdownTimeoutMs,

                batchSize <= 0 ? DefaultConfig.BATCH_SIZE : batchSize,
                periodMs <= 0 ? DefaultConfig.PERIOD_MS : periodMs,
                capacity <= 0 ? DefaultConfig.CAPACITY : capacity,
                threadCount <= 0 ? DefaultConfig.THREAD_COUNT : threadCount,
                indexPattern,
                url,
                apiKey,
                retryCount < 0 ? DefaultConfig.RETRY_COUNT : retryCount);
    }

    @Override
    public void append(LogEvent logEvent) {
        eventPublisher.publish(Log4jEventUtil.build(logEvent));
    }

    @Override
    public void stop() {
        super.stop();
        eventPublisher.stop(shutdownTimeoutMs);
    }

    @Override
    public boolean stop(long timeout, TimeUnit timeUnit) {
        boolean stopped = super.stop(timeout, timeUnit);
        long timeoutMs = Math.max(timeUnit.toMillis(timeout), shutdownTimeoutMs);
        eventPublisher.stop(timeoutMs);
        return stopped;
    }
}
