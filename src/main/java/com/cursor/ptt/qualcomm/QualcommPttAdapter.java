package com.cursor.ptt.qualcomm;

import com.cursor.ptt.PttException;
import com.cursor.ptt.PttSessionConfig;

/**
 * Qualcomm-facing contract to be implemented with the device's available SDK.
 */
public interface QualcommPttAdapter {
    void initialize(QualcommPttProfile profile, PttSessionConfig config) throws PttException;

    boolean requestFloor(QualcommPttProfile profile, PttSessionConfig config) throws PttException;

    void releaseFloor(QualcommPttProfile profile, PttSessionConfig config) throws PttException;

    void enableTransmitAudio(QualcommPttProfile profile, PttSessionConfig config) throws PttException;

    void disableTransmitAudio(QualcommPttProfile profile, PttSessionConfig config) throws PttException;

    void enableReceiveAudio(QualcommPttProfile profile, PttSessionConfig config, String remoteUserId)
            throws PttException;

    void disableReceiveAudio(QualcommPttProfile profile, PttSessionConfig config, String remoteUserId)
            throws PttException;

    void shutdown(QualcommPttProfile profile, PttSessionConfig config) throws PttException;
}
