package com.cursor.ptt;

import java.util.Objects;

/**
 * Observable session event delivered to UI, service, or telemetry code.
 */
public final class PttEvent {
    public enum Type {
        CONNECTED,
        DISCONNECTED,
        STATE_CHANGED,
        FLOOR_GRANTED,
        FLOOR_DENIED,
        FLOOR_RELEASED,
        REMOTE_TRANSMISSION_STARTED,
        REMOTE_TRANSMISSION_STOPPED,
        ERROR
    }

    private final Type type;
    private final PttState state;
    private final String message;

    private PttEvent(Type type, PttState state, String message) {
        this.type = Objects.requireNonNull(type, "type");
        this.state = Objects.requireNonNull(state, "state");
        this.message = message == null ? "" : message;
    }

    public static PttEvent of(Type type, PttState state, String message) {
        return new PttEvent(type, state, message);
    }

    public Type getType() {
        return type;
    }

    public PttState getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }
}
