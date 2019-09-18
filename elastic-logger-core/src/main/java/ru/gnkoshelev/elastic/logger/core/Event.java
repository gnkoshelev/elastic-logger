package ru.gnkoshelev.elastic.logger.core;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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
    public final Map<String, String> properties;

    public Event(String level, long timestamp, String logger, String thread, String message, Map<String, String> properties) {
        this.level = level;
        this.timestamp = timestamp;
        this.logger = logger;
        this.thread = thread;
        this.message = message;
        this.dateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC);
        this.properties = properties;
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
                .append("\",\"properties\":")
                .append(propertiesAsJsonString(properties))
                .append('}')
                .toString();
    }

    static final int SOFT_UPPER_BOUND_EVENT_SIZE = 512;

    private static String escape(String s) {
        if (s.indexOf('\\') != -1) {
            s = s.replace("\\", "\\\\");
        }
        if (s.indexOf('"') != -1) {
            s = s.replace("\"", "\\\"");
        }
        return s;
    }

    private static String propertiesAsJsonString(Map<String, String> properties) {
        StringBuilder sb = new StringBuilder(256);
        sb.append('{');
        boolean comma = false;
        for (Map.Entry<String, String> e : properties.entrySet()) {
            if (!comma) {
                comma = true;
            } else {
                sb.append(',');
            }

            sb
                    .append('"')
                    .append(escape(e.getKey()))
                    .append("\":\"")
                    .append(escape(e.getValue()))
                    .append('"');
        }
        sb.append('}');
        return sb.toString();
    }
}
