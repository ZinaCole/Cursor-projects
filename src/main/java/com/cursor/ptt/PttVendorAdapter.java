package com.cursor.ptt;

/**
 * Boundary between the PTT state machine and a device/vendor implementation.
 *
 * <p>Qualcomm-specific implementations can wrap QMI/IMS/floor-control and
 * audio-routing SDK calls without leaking proprietary types into application
 * code.</p>
 */
public interface PttVendorAdapter {
    void connect(PttSessionConfig config) throws PttException;

    TransmitGrant requestTransmit(PttSessionConfig config) throws PttException;

    void openMicrophonePath(PttSessionConfig config) throws PttException;

    void closeMicrophonePath(PttSessionConfig config) throws PttException;

    void releaseTransmit(PttSessionConfig config) throws PttException;

    void openSpeakerPath(PttSessionConfig config, String remoteUserId) throws PttException;

    void closeSpeakerPath(PttSessionConfig config, String remoteUserId) throws PttException;

    void disconnect(PttSessionConfig config) throws PttException;
}
