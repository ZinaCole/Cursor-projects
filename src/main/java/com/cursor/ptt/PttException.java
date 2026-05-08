package com.cursor.ptt;

/**
 * Checked exception used when the PTT core cannot complete a vendor operation.
 */
public class PttException extends Exception {
    private static final long serialVersionUID = 1L;

    public PttException(String message) {
        super(message);
    }

    public PttException(String message, Throwable cause) {
        super(message, cause);
    }
}
