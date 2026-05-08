package com.cursor.ptt.qualcomm;

import com.cursor.ptt.PttException;
import com.cursor.ptt.PttSessionConfig;
import com.cursor.ptt.PttVendorAdapter;
import com.cursor.ptt.TransmitGrant;

import java.util.Objects;

/**
 * PTT vendor adapter backed by Qualcomm-specific floor and audio operations.
 */
public final class QualcommPttGateway implements PttVendorAdapter {
    private final QualcommPttProfile profile;
    private final QualcommPttAdapter qualcommAdapter;

    public QualcommPttGateway(QualcommPttProfile profile, QualcommPttAdapter qualcommAdapter) {
        this.profile = Objects.requireNonNull(profile, "profile");
        this.qualcommAdapter = Objects.requireNonNull(qualcommAdapter, "qualcommAdapter");
    }

    @Override
    public void connect(PttSessionConfig config) throws PttException {
        qualcommAdapter.initialize(profile, config);
    }

    @Override
    public TransmitGrant requestTransmit(PttSessionConfig config) throws PttException {
        if (qualcommAdapter.requestFloor(profile, config)) {
            return TransmitGrant.granted();
        }
        return TransmitGrant.denied("Qualcomm floor request denied");
    }

    @Override
    public void openMicrophonePath(PttSessionConfig config) throws PttException {
        qualcommAdapter.enableTransmitAudio(profile, config);
    }

    @Override
    public void closeMicrophonePath(PttSessionConfig config) throws PttException {
        qualcommAdapter.disableTransmitAudio(profile, config);
    }

    @Override
    public void releaseTransmit(PttSessionConfig config) throws PttException {
        qualcommAdapter.releaseFloor(profile, config);
    }

    @Override
    public void openSpeakerPath(PttSessionConfig config, String remoteUserId) throws PttException {
        qualcommAdapter.enableReceiveAudio(profile, config, remoteUserId);
    }

    @Override
    public void closeSpeakerPath(PttSessionConfig config, String remoteUserId) throws PttException {
        qualcommAdapter.disableReceiveAudio(profile, config, remoteUserId);
    }

    @Override
    public void disconnect(PttSessionConfig config) throws PttException {
        qualcommAdapter.shutdown(profile, config);
    }
}
