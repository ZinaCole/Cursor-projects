package com.cursor.ptt;

/**
 * High-level state for a push-to-talk session.
 */
public enum PttState {
    DISCONNECTED,
    IDLE,
    REQUESTING_TRANSMIT,
    TRANSMITTING,
    RECEIVING,
    ERROR
}
