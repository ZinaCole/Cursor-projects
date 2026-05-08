package com.cursor.ptt;

import com.cursor.ptt.qualcomm.QualcommPttAdapter;
import com.cursor.ptt.qualcomm.QualcommPttGateway;
import com.cursor.ptt.qualcomm.QualcommPttProfile;

import java.util.ArrayList;
import java.util.List;

public final class PttSessionTest {
    public static void main(String[] args) throws Exception {
        floorGrantStartsTransmit();
        floorDenialReturnsToIdle();
        remoteReceiveBlocksLocalTransmitUntilStopped();
        System.out.println("PttSessionTest passed");
    }

    private static void floorGrantStartsTransmit() throws Exception {
        RecordingQualcommAdapter adapter = new RecordingQualcommAdapter(true);
        RecordingListener listener = new RecordingListener();
        PttSession session = newSession(adapter, listener);

        session.connect();
        boolean granted = session.startTalking();

        assertTrue(granted, "floor should be granted");
        assertEquals(PttState.TRANSMITTING, session.getState(), "session should transmit");
        assertTrue(adapter.calls.contains("enableTransmitAudio"), "TX audio should be enabled");
        assertEvent(listener, PttEvent.Type.FLOOR_GRANTED);

        session.stopTalking();

        assertEquals(PttState.IDLE, session.getState(), "session should return to idle");
        assertTrue(adapter.calls.contains("releaseFloor"), "floor should be released");
    }

    private static void floorDenialReturnsToIdle() throws Exception {
        RecordingQualcommAdapter adapter = new RecordingQualcommAdapter(false);
        RecordingListener listener = new RecordingListener();
        PttSession session = newSession(adapter, listener);

        session.connect();
        boolean granted = session.startTalking();

        assertFalse(granted, "floor should be denied");
        assertEquals(PttState.IDLE, session.getState(), "denial should return to idle");
        assertFalse(adapter.calls.contains("enableTransmitAudio"), "TX audio should remain disabled");
        assertEvent(listener, PttEvent.Type.FLOOR_DENIED);
    }

    private static void remoteReceiveBlocksLocalTransmitUntilStopped() throws Exception {
        RecordingQualcommAdapter adapter = new RecordingQualcommAdapter(true);
        PttSession session = newSession(adapter, new RecordingListener());

        session.connect();
        session.onRemoteTransmissionStarted("remote-user");

        assertEquals(PttState.RECEIVING, session.getState(), "remote audio should move to receiving");
        assertThrows(PttException.class, session::startTalking, "local transmit should wait for idle");

        session.onRemoteTransmissionStopped();

        assertEquals(PttState.IDLE, session.getState(), "stopping remote audio should return idle");
    }

    private static PttSession newSession(RecordingQualcommAdapter adapter, PttSessionListener listener) {
        PttSessionConfig config = new PttSessionConfig("ops", "local-user", "EVS", 16000);
        QualcommPttProfile profile = new QualcommPttProfile("sub-1", "ims", "speaker", 1);
        return new PttSession(config, new QualcommPttGateway(profile, adapter), listener);
    }

    private static void assertEvent(RecordingListener listener, PttEvent.Type type) {
        for (PttEvent event : listener.events) {
            if (event.getType() == type) {
                return;
            }
        }
        throw new AssertionError("Expected event " + type);
    }

    private static void assertTrue(boolean value, String message) {
        if (!value) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean value, String message) {
        if (value) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ": expected=" + expected + " actual=" + actual);
        }
    }

    private static void assertThrows(Class<? extends Exception> expected, ThrowingRunnable runnable, String message) {
        try {
            runnable.run();
        } catch (Exception exception) {
            if (expected.isInstance(exception)) {
                return;
            }
            throw new AssertionError(message + ": expected " + expected.getName() + " but got " + exception, exception);
        }
        throw new AssertionError(message + ": expected " + expected.getName());
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static final class RecordingListener implements PttSessionListener {
        private final List<PttEvent> events = new ArrayList<>();

        @Override
        public void onPttEvent(PttEvent event) {
            events.add(event);
        }
    }

    private static final class RecordingQualcommAdapter implements QualcommPttAdapter {
        private final boolean grantFloor;
        private final List<String> calls = new ArrayList<>();

        private RecordingQualcommAdapter(boolean grantFloor) {
            this.grantFloor = grantFloor;
        }

        @Override
        public void initialize(QualcommPttProfile profile, PttSessionConfig config) {
            calls.add("initialize");
        }

        @Override
        public boolean requestFloor(QualcommPttProfile profile, PttSessionConfig config) {
            calls.add("requestFloor");
            return grantFloor;
        }

        @Override
        public void releaseFloor(QualcommPttProfile profile, PttSessionConfig config) {
            calls.add("releaseFloor");
        }

        @Override
        public void enableTransmitAudio(QualcommPttProfile profile, PttSessionConfig config) {
            calls.add("enableTransmitAudio");
        }

        @Override
        public void disableTransmitAudio(QualcommPttProfile profile, PttSessionConfig config) {
            calls.add("disableTransmitAudio");
        }

        @Override
        public void enableReceiveAudio(QualcommPttProfile profile, PttSessionConfig config, String remoteUserId) {
            calls.add("enableReceiveAudio:" + remoteUserId);
        }

        @Override
        public void disableReceiveAudio(QualcommPttProfile profile, PttSessionConfig config, String remoteUserId) {
            calls.add("disableReceiveAudio:" + remoteUserId);
        }

        @Override
        public void shutdown(QualcommPttProfile profile, PttSessionConfig config) {
            calls.add("shutdown");
        }
    }
}
