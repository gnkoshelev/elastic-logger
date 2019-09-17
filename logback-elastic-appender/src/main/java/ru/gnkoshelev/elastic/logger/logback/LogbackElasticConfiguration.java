package ru.gnkoshelev.elastic.logger.logback;

import ru.gnkoshelev.elastic.logger.core.DefaultConfig;

/**
 * @author Gregory Koshelev
 */
public class LogbackElasticConfiguration {
    private long shutdownTimeoutMs = DefaultConfig.SHUTDOWN_TIMEOUT_MS;

    private int batchSize = DefaultConfig.BATCH_SIZE;
    private long periodMs = DefaultConfig.PERIOD_MS;
    private int capacity = DefaultConfig.CAPACITY;
    private int threadCount = DefaultConfig.THREAD_COUNT;

    private int retryCount = DefaultConfig.RETRY_COUNT;

    private String indexPattern;
    private String url;
    private String apiKey;

    public long getShutdownTimeoutMs() {
        return shutdownTimeoutMs;
    }
    public void setShutdownTimeoutMs(long shutdownTimeoutMs) {
        this.shutdownTimeoutMs = shutdownTimeoutMs;
    }

    public int getBatchSize() {
        return batchSize;
    }
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public long getPeriodMs() {
        return periodMs;
    }
    public void setPeriodMs(long periodMs) {
        this.periodMs = periodMs;
    }

    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getThreadCount() {
        return threadCount;
    }
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getRetryCount() {
        return retryCount;
    }
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getIndexPattern() {
        return indexPattern;
    }
    public void setIndexPattern(String indexPattern) {
        this.indexPattern = indexPattern;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getApiKey() {
        return apiKey;
    }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String toString() {
        return "LogbackElasticConfiguration{" +
                "shutdownTimeoutMs=" + shutdownTimeoutMs +
                ", batchSize=" + batchSize +
                ", periodMs=" + periodMs +
                ", capacity=" + capacity +
                ", threadCount=" + threadCount +
                ", retryCount=" + retryCount +
                ", indexPattern='" + indexPattern + '\'' +
                ", url='" + url + '\'' +
                ", apiKey='" + apiKey + '\'' +
                '}';
    }
}
