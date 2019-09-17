package ru.gnkoshelev.elastic.logger.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ru.gnkoshelev.elastic.logger.core.Event;

/**
 * @author Gregory Koshelev
 */
final class LogbackEventUtil {
    private LogbackEventUtil() { /* static class */ }

    static Event build(ILoggingEvent logEvent) {
        return new Event(
                logEvent.getLevel().toString(),
                logEvent.getTimeStamp(),
                logEvent.getLoggerName(),
                logEvent.getThreadName(),
                logEvent.getFormattedMessage());
    }
}
