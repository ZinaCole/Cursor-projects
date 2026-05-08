package com.cursor.ptt;

/**
 * Result returned by the underlying vendor layer after a floor request.
 */
public final class TransmitGrant {
    private static final TransmitGrant GRANTED = new TransmitGrant(true, "granted");

    private final boolean granted;
    private final String reason;

    private TransmitGrant(boolean granted, String reason) {
        this.granted = granted;
        this.reason = reason;
    }

    public static TransmitGrant granted() {
        return GRANTED;
    }

    public static TransmitGrant denied(String reason) {
        String detail = reason == null || reason.trim().isEmpty() ? "denied" : reason.trim();
        return new TransmitGrant(false, detail);
    }

    public boolean isGranted() {
        return granted;
    }

    public String getReason() {
        return reason;
    }
}
