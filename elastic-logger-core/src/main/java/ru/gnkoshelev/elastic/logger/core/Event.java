package ru.gnkoshelev.elastic.logger.core;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Gregory Koshelev
 */
public final class Event implements JsonSerializable {
    public final String level;
    public final long timestamp;
    public final String logger;
    public final String thread;
    public final String message;
    public final ZonedDateTime dateTime;

    public Event(String level, long timestamp, String logger, String thread, String message) {
        this.level = level;
        this.timestamp = timestamp;
        this.logger = logger;
        this.thread = thread;
        this.message = message;
        this.dateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC);
    }

    @Override
    public String toJsonString() {
        return new StringBuilder(SOFT_UPPER_BOUND_EVENT_SIZE)
                .append("{\"level\":\"")
                .append(escape(level))
                .append("\",\"logger\":\"")
                .append(escape(logger))
                .append("\",\"@timestamp\":\"")
                .append(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dateTime))
                .append("\",\"message\":\"")
                .append(escape(message))
                .append("\",\"thread\":\"")
                .append(escape(thread))
                .append("\"}")
                .toString();
    }

    static final int SOFT_UPPER_BOUND_EVENT_SIZE = 256;

    private static String escape(String s) {
        if (s.indexOf('\\') != -1) {
            s = s.replace("\\", "\\\\");
        }
        if (s.indexOf('"') != -1) {
            s = s.replace("\"", "\\\"");
        }
        return s;
    }
}
