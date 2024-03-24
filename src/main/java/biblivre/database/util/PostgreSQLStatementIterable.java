package biblivre.database.util;

import biblivre.database.util.exception.UncoveredStateException;
import jakarta.annotation.Nonnull;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PostgreSQLStatementIterable implements Iterable<String> {
    private static final char SPACE = ' ';
    private static final char DASH = '-';
    private static final char NEW_LINE = '\n';
    private static final char DOUBLE_QUOTE = '"';
    private static final char SINGLE_QUOTE = '\'';
    private static final char SEMICOLON = ';';
    private static final char DOLLAR_SIGN = '$';

    private final Iterator<Character> sourceIterator;

    private String dollarQuoteStringTag;

    private StringBuilder dollarQuoteStringTagSB;

    public PostgreSQLStatementIterable(Iterator<Character> sourceIterator) {
        this.sourceIterator = sourceIterator;

        this.dollarQuoteStringTagSB = new StringBuilder();
    }

    @Override
    @Nonnull
    public Iterator<String> iterator() {
        return new Iterator<>() {

            @Override
            public String next() {
                return PostgreSQLStatementIterable.this.next();
            }

            @Override
            public boolean hasNext() {
                return PostgreSQLStatementIterable.this.hasNext();
            }
        };
    }

    public Stream<String> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    private String next() {
        return loadNext();
    }

    private String loadNext() {
        State currentState = State.WAITING_SEMICOLON;

        StringBuilder sb = new StringBuilder();

        char lastChar = 0;

        while (sourceIterator.hasNext()) {
            char c = sourceIterator.next();

            if (c == SEMICOLON && currentState == State.WAITING_SEMICOLON) {
                sb.append(SEMICOLON);

                break;
            }

            State newState = nextState(c, currentState);

            if (newState == State.OPENING_DASH) {
                // don't add it yet
                lastChar = c;

                currentState = newState;

                continue;
            }

            if (currentState == State.OPENING_DASH && newState != State.COMMENT) {
                sb.append(lastChar);
            }

            if ((newState == State.COMMENT)
                    || ((newState == State.WHITESPACE && Character.isWhitespace(lastChar))
                            || (currentState == State.COMMENT))) {

                lastChar = c;

                currentState = newState;

                continue;
            }

            if (currentState == State.OPENING_DOLLAR_QUOTE_TAG) {
                if (newState == State.DOLLAR_QUOTE_STRING) {
                    dollarQuoteStringTag = dollarQuoteStringTagSB.toString();

                    dollarQuoteStringTagSB = new StringBuilder();
                } else {
                    dollarQuoteStringTagSB.append(c);
                }
            }

            if (currentState == State.CLOSING_DOLLAR_QUOTE_TAG) {
                if (newState == State.CLOSING_DOLLAR_QUOTE_TAG) {
                    dollarQuoteStringTagSB.append(c);
                } else {
                    dollarQuoteStringTagSB = new StringBuilder();
                }
            }

            currentState = newState;

            if (sb.isEmpty() && Character.isWhitespace(c)) {
                continue;
            }

            sb.append((Character.isWhitespace(c) && isDiscardWhitespace(currentState)) ? SPACE : c);

            lastChar = c;
        }

        return sb.toString();
    }

    private boolean isDiscardWhitespace(State state) {
        return state != State.DOLLAR_QUOTE_STRING;
    }

    private boolean hasNext() {
        return sourceIterator.hasNext();
    }

    private State nextState(char c, State currentState) {
        switch (currentState) {
            case WHITESPACE, WAITING_SEMICOLON -> {
                if (c == DASH) {
                    return State.OPENING_DASH;
                }
                if (c == SINGLE_QUOTE) {
                    return State.SINGLE_QUOTE_STRING;
                }
                if (c == DOUBLE_QUOTE) {
                    return State.DOUBLE_QUOTE_STRING;
                }
                if (c == DOLLAR_SIGN) {
                    return State.OPENING_DOLLAR_QUOTE_TAG;
                }
                if (Character.isWhitespace(c)) {
                    return State.WHITESPACE;
                }
                return State.WAITING_SEMICOLON;
            }
            case OPENING_DASH -> {
                if (c == DASH) {
                    return State.COMMENT;
                }
                if (c == SINGLE_QUOTE) {
                    return State.SINGLE_QUOTE_STRING;
                }
                if (c == DOUBLE_QUOTE) {
                    return State.DOUBLE_QUOTE_STRING;
                }
                if (c == DOLLAR_SIGN) {
                    return State.OPENING_DOLLAR_QUOTE_TAG;
                }
                if (Character.isWhitespace(c)) {
                    return State.WHITESPACE;
                }
                return State.WAITING_SEMICOLON;
            }
            case COMMENT -> {
                if (c == NEW_LINE) {
                    return State.WHITESPACE;
                }
                return State.COMMENT;
            }
            case OPENING_DOLLAR_QUOTE_TAG -> {
                if (c == DOLLAR_SIGN) {
                    return State.DOLLAR_QUOTE_STRING;
                }
                return State.OPENING_DOLLAR_QUOTE_TAG;
            }
            case DOLLAR_QUOTE_STRING -> {
                if (c == DOLLAR_SIGN) {
                    return State.CLOSING_DOLLAR_QUOTE_TAG;
                }
                return State.DOLLAR_QUOTE_STRING;
            }
            case CLOSING_DOLLAR_QUOTE_TAG -> {
                String provisionalDollaQuoteStringTag = dollarQuoteStringTagSB.toString();
                if (c == DOLLAR_SIGN
                        && Objects.equals(dollarQuoteStringTag, provisionalDollaQuoteStringTag)) {
                    return State.WAITING_SEMICOLON;
                }
                if (dollarQuoteStringTag != null
                        && dollarQuoteStringTag.startsWith(provisionalDollaQuoteStringTag + c)) {
                    return State.CLOSING_DOLLAR_QUOTE_TAG;
                }
                return State.DOLLAR_QUOTE_STRING;
            }
            case SINGLE_QUOTE_STRING -> {
                if (c != SINGLE_QUOTE) {
                    return State.SINGLE_QUOTE_STRING;
                }
                return State.WAITING_SEMICOLON;
            }
            case DOUBLE_QUOTE_STRING -> {
                if (c != DOUBLE_QUOTE) {
                    return State.DOUBLE_QUOTE_STRING;
                }
                return State.WAITING_SEMICOLON;
            }
            default ->
                    throw new UncoveredStateException(
                            "State from %s receving %c is not covered".formatted(currentState, c));
        }
    }

    enum State {
        WHITESPACE,

        OPENING_DASH,
        COMMENT,

        OPENING_DOLLAR_QUOTE_TAG,
        DOLLAR_QUOTE_STRING,
        CLOSING_DOLLAR_QUOTE_TAG,

        SINGLE_QUOTE_STRING,

        DOUBLE_QUOTE_STRING,

        WAITING_SEMICOLON,
    }
}
