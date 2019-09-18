package ru.gnkoshelev.elastic.logger.log4j2;

import org.apache.logging.log4j.core.LogEvent;
import ru.gnkoshelev.elastic.logger.core.Event;

/**
 * @author Gregory Koshelev
 */
final class Log4jEventUtil {
    private Log4jEventUtil() { /* static class */ }

    static Event build(LogEvent logEvent) {
        return new Event(
                logEvent.getLevel().toString(),
                logEvent.getTimeMillis(),
                logEvent.getLoggerName(),
                logEvent.getThreadName(),
                logEvent.getMessage().getFormattedMessage(),
                logEvent.getContextData().toMap());
    }
}
