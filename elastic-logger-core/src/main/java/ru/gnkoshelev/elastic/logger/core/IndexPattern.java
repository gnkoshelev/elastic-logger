package ru.gnkoshelev.elastic.logger.core;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Koshelev
 */
public class IndexPattern {
    private final Component[] components;
    private final int minLength;

    private IndexPattern(Component[] components) {
        this.components = components;

        int length = 0;
        for (Component component: components) {
            length += component.minLength();
        }
        this.minLength = length;
    }

    public String format() {
        StringBuilder sb = new StringBuilder(minLength);
        for (Component component : components) {
            sb = component.append(sb);
        }
        return sb.toString();
    }

    public static IndexPattern build(String pattern) {
        List<Component> components = new ArrayList<>();

        int left = 0;
        for (int i = 0; i < pattern.length() - 1; i++) {
            char c = pattern.charAt(i);
            if (c == '%') {
                if (left < i) {
                    components.add(new StringComponent(pattern.substring(left, i)));
                }
                switch (pattern.charAt(++i)) {
                    case 'd':
                        components.add(new DateComponent());
                        break;
                }
                left = i + 1;
            }
        }
        if (left < pattern.length() - 1) {
            components.add(new StringComponent(pattern.substring(left)));
        }

        return new IndexPattern(components.toArray(new Component[0]));
    }

    private static abstract class Component {
        abstract StringBuilder append(StringBuilder sb);
        abstract int minLength();
    }

    private static class StringComponent extends Component {
        private final String s;

        StringComponent(String s) {
            this.s = s;
        }

        @Override
        StringBuilder append(StringBuilder sb) {
            return sb.append(s);
        }

        @Override
        int minLength() {
            return s.length();
        }
    }

    private static class DateComponent extends Component {
        static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        @Override
        StringBuilder append(StringBuilder sb) {
            return sb.append(DATE_FORMATTER.format(ZonedDateTime.now()));
        }

        @Override
        int minLength() {
            return "yyyy.MM.dd".length();
        }
    }
}
