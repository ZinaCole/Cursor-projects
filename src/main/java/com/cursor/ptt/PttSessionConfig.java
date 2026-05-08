package com.cursor.ptt;

import java.util.Objects;

/**
 * Immutable configuration required to join a PTT channel.
 */
public final class PttSessionConfig {
    private final String channelId;
    private final String localUserId;
    private final String codec;
    private final int sampleRateHz;

    public PttSessionConfig(String channelId, String localUserId, String codec, int sampleRateHz) {
        this.channelId = requireText(channelId, "channelId");
        this.localUserId = requireText(localUserId, "localUserId");
        this.codec = requireText(codec, "codec");
        if (sampleRateHz <= 0) {
            throw new IllegalArgumentException("sampleRateHz must be positive");
        }
        this.sampleRateHz = sampleRateHz;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getLocalUserId() {
        return localUserId;
    }

    public String getCodec() {
        return codec;
    }

    public int getSampleRateHz() {
        return sampleRateHz;
    }

    private static String requireText(String value, String fieldName) {
        String text = Objects.requireNonNull(value, fieldName).trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return text;
    }
}
