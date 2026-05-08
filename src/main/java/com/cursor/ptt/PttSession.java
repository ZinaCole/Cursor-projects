package com.cursor.ptt;

import java.util.Objects;

/**
 * Coordinates PTT floor control and audio routing through a vendor adapter.
 */
public final class PttSession {
    private final PttSessionConfig config;
    private final PttVendorAdapter adapter;
    private final PttSessionListener listener;

    private PttState state = PttState.DISCONNECTED;
    private String activeRemoteUserId = "";

    public PttSession(PttSessionConfig config, PttVendorAdapter adapter, PttSessionListener listener) {
        this.config = Objects.requireNonNull(config, "config");
        this.adapter = Objects.requireNonNull(adapter, "adapter");
        this.listener = listener == null ? event -> { } : listener;
    }

    public synchronized PttState getState() {
        return state;
    }

    public synchronized void connect() throws PttException {
        if (state != PttState.DISCONNECTED) {
            return;
        }
        adapter.connect(config);
        transitionTo(PttState.IDLE, PttEvent.Type.CONNECTED, "Connected to channel " + config.getChannelId());
    }

    public synchronized boolean startTalking() throws PttException {
        requireState(PttState.IDLE, "start talking");
        transitionTo(PttState.REQUESTING_TRANSMIT, PttEvent.Type.STATE_CHANGED, "Requesting transmit floor");

        TransmitGrant grant = adapter.requestTransmit(config);
        if (!grant.isGranted()) {
            transitionTo(PttState.IDLE, PttEvent.Type.FLOOR_DENIED, grant.getReason());
            return false;
        }

        try {
            adapter.openMicrophonePath(config);
        } catch (PttException exception) {
            try {
                adapter.releaseTransmit(config);
            } catch (PttException releaseException) {
                exception.addSuppressed(releaseException);
            }
            fail(exception);
            return false;
        }
        transitionTo(PttState.TRANSMITTING, PttEvent.Type.FLOOR_GRANTED, "Transmit floor granted");
        return true;
    }

    public synchronized void stopTalking() throws PttException {
        if (state != PttState.TRANSMITTING && state != PttState.REQUESTING_TRANSMIT) {
            return;
        }

        PttException failure = null;
        try {
            adapter.closeMicrophonePath(config);
        } catch (PttException exception) {
            failure = exception;
        }

        try {
            adapter.releaseTransmit(config);
        } catch (PttException exception) {
            failure = failure == null ? exception : failure;
        }

        transitionTo(PttState.IDLE, PttEvent.Type.FLOOR_RELEASED, "Transmit floor released");
        if (failure != null) {
            fail(failure);
        }
    }

    public synchronized void onRemoteTransmissionStarted(String remoteUserId) throws PttException {
        String speaker = requireText(remoteUserId, "remoteUserId");
        requireState(PttState.IDLE, "receive remote transmission");

        adapter.openSpeakerPath(config, speaker);
        activeRemoteUserId = speaker;
        transitionTo(PttState.RECEIVING, PttEvent.Type.REMOTE_TRANSMISSION_STARTED, speaker);
    }

    public synchronized void onRemoteTransmissionStopped() throws PttException {
        if (state != PttState.RECEIVING) {
            return;
        }

        String speaker = activeRemoteUserId;
        activeRemoteUserId = "";
        adapter.closeSpeakerPath(config, speaker);
        transitionTo(PttState.IDLE, PttEvent.Type.REMOTE_TRANSMISSION_STOPPED, speaker);
    }

    public synchronized void disconnect() throws PttException {
        if (state == PttState.DISCONNECTED) {
            return;
        }

        PttException failure = null;
        if (state == PttState.TRANSMITTING || state == PttState.REQUESTING_TRANSMIT) {
            try {
                stopTalking();
            } catch (PttException exception) {
                failure = exception;
            }
        } else if (state == PttState.RECEIVING) {
            try {
                onRemoteTransmissionStopped();
            } catch (PttException exception) {
                failure = exception;
            }
        }

        try {
            adapter.disconnect(config);
        } catch (PttException exception) {
            failure = failure == null ? exception : failure;
        }

        transitionTo(PttState.DISCONNECTED, PttEvent.Type.DISCONNECTED, "Disconnected");
        if (failure != null) {
            fail(failure);
        }
    }

    private void requireState(PttState requiredState, String action) throws PttException {
        if (state != requiredState) {
            throw new PttException("Cannot " + action + " while session is " + state);
        }
    }

    private void transitionTo(PttState nextState, PttEvent.Type eventType, String message) {
        state = nextState;
        listener.onPttEvent(PttEvent.of(eventType, state, message));
    }

    private void fail(PttException exception) throws PttException {
        state = PttState.ERROR;
        listener.onPttEvent(PttEvent.of(PttEvent.Type.ERROR, state, exception.getMessage()));
        throw exception;
    }

    private static String requireText(String value, String fieldName) {
        String text = Objects.requireNonNull(value, fieldName).trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return text;
    }
}
