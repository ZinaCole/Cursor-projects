package com.cursor.ptt.qualcomm;

import java.util.Objects;

/**
 * Device/profile values needed by a Qualcomm-backed PTT implementation.
 */
public final class QualcommPttProfile {
    private final String subscriptionId;
    private final String imsServiceName;
    private final String preferredAudioRoute;
    private final int qosClassIdentifier;

    public QualcommPttProfile(
            String subscriptionId,
            String imsServiceName,
            String preferredAudioRoute,
            int qosClassIdentifier) {
        this.subscriptionId = requireText(subscriptionId, "subscriptionId");
        this.imsServiceName = requireText(imsServiceName, "imsServiceName");
        this.preferredAudioRoute = requireText(preferredAudioRoute, "preferredAudioRoute");
        if (qosClassIdentifier <= 0) {
            throw new IllegalArgumentException("qosClassIdentifier must be positive");
        }
        this.qosClassIdentifier = qosClassIdentifier;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getImsServiceName() {
        return imsServiceName;
    }

    public String getPreferredAudioRoute() {
        return preferredAudioRoute;
    }

    public int getQosClassIdentifier() {
        return qosClassIdentifier;
    }

    private static String requireText(String value, String fieldName) {
        String text = Objects.requireNonNull(value, fieldName).trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return text;
    }
}
