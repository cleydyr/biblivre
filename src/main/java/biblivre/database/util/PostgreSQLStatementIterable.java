package biblivre.database.util;

import biblivre.database.util.exception.UncoveredStateException;
import java.util.Iterator;

public class PostgreSQLStatementIterable implements Iterator<String> {
    private static final char SPACE = ' ';
    private static final char DASH = '-';
    private static final char NEW_LINE = '\n';
    private static final char DOUBLE_QUOTE = '"';
    private static final char SINGLE_QUOTE = '\'';
    private static final char SEMICOLON = ';';
    private static final char DOLLAR_SIGN = '$';

    private Iterator<Character> sourceIterator;

    public PostgreSQLStatementIterable(Iterator<Character> sourceIterator) {
        this.sourceIterator = sourceIterator;
    }

    @Override
    public String next() {
        State currentState = State.WAITING_SEMICOLON;

        StringBuilder sb = new StringBuilder();

        char lastChar = 0;

        while (sourceIterator.hasNext()) {
            char c = sourceIterator.next();

            if (c == SEMICOLON && currentState == State.WAITING_SEMICOLON) {
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

            if (newState == State.COMMENT
                    || newState == State.WHITESPACE
                            && (Character.isWhitespace(lastChar)
                                    || currentState == State.COMMENT)) {

                lastChar = c;

                currentState = newState;

                continue;
            }

            currentState = newState;

            sb.append(
                    (Character.isWhitespace(c) && currentState != State.DOLLAR_QUOTE_STRING)
                            ? SPACE
                            : c);

            lastChar = c;
        }

        return sb.toString();
    }

    @Override
    public boolean hasNext() {
        return sourceIterator.hasNext();
    }

    enum State {
        WHITESPACE,

        OPENING_DASH,
        COMMENT,

        OPENING_DOLLAR_SIGN,
        DOLLAR_QUOTE_STRING,
        CLOSING_DOLLAR,

        SINGLE_QUOTE_STRING,

        DOUBLE_QUOTE_STRING,

        WAITING_SEMICOLON,
    }

    private static State nextState(char c, State currentState) {
        switch (currentState) {
            case WHITESPACE:
            case WAITING_SEMICOLON:
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
                    return State.OPENING_DOLLAR_SIGN;
                }

                if (Character.isWhitespace(c)) {
                    return State.WHITESPACE;
                }

                return State.WAITING_SEMICOLON;
            case OPENING_DASH:
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
                    return State.OPENING_DOLLAR_SIGN;
                }

                if (Character.isWhitespace(c)) {
                    return State.WHITESPACE;
                }

                return State.WAITING_SEMICOLON;
            case COMMENT:
                if (c == NEW_LINE) {
                    return State.WHITESPACE;
                }

                return State.COMMENT;
            case OPENING_DOLLAR_SIGN:
                if (c == DOLLAR_SIGN) {
                    return State.DOLLAR_QUOTE_STRING;
                }

                if (c == DASH) {
                    return State.OPENING_DASH;
                }

                if (c == SINGLE_QUOTE) {
                    return State.SINGLE_QUOTE_STRING;
                }

                if (c == DOUBLE_QUOTE) {
                    return State.DOUBLE_QUOTE_STRING;
                }

                if (Character.isWhitespace(c)) {
                    return State.WHITESPACE;
                }

                return State.WAITING_SEMICOLON;
            case DOLLAR_QUOTE_STRING:
                if (c == DOLLAR_SIGN) {
                    return State.CLOSING_DOLLAR;
                }

                return State.DOLLAR_QUOTE_STRING;
            case CLOSING_DOLLAR:
                if (c == DOLLAR_SIGN) {
                    return State.WAITING_SEMICOLON;
                }

                return State.DOLLAR_QUOTE_STRING;

            case SINGLE_QUOTE_STRING:
                if (c != SINGLE_QUOTE) {
                    return State.SINGLE_QUOTE_STRING;
                }

                return State.WAITING_SEMICOLON;

            case DOUBLE_QUOTE_STRING:
                if (c != DOUBLE_QUOTE) {
                    return State.DOUBLE_QUOTE_STRING;
                }

                return State.WAITING_SEMICOLON;

            default:
                throw new UncoveredStateException(
                        "State from %s receving %c is not covered".formatted(currentState, c));
        }
    }
}
