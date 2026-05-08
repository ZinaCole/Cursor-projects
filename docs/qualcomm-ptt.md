# Building PTT with Qualcomm

This starter keeps application PTT behavior separate from Qualcomm-specific
device APIs. The core package owns state transitions and user-visible events;
the Qualcomm package is the seam where a device team can plug in the available
SDK, modem, IMS, audio-policy, or carrier push-to-talk implementation.

## Architecture

```text
PTT UI / Android foreground service
        |
        v
PttSession
  - floor request / release state machine
  - transmit and receive coordination
  - listener events for UI, telemetry, and service notifications
        |
        v
PttVendorAdapter
        |
        v
QualcommPttGateway
        |
        v
QualcommPttAdapter implementation
  - QMI/IMS floor-control calls
  - audio route setup for microphone and speaker
  - QoS / subscription configuration
```

## Android integration checklist

1. Run `PttSession` from a foreground service so active transmit and receive
   sessions survive activity lifecycle changes.
2. Bind the hardware PTT key or on-screen hold-to-talk control to
   `startTalking()` on press and `stopTalking()` on release.
3. Translate incoming floor/audio callbacks from the Qualcomm layer into
   `onRemoteTransmissionStarted(remoteUserId)` and
   `onRemoteTransmissionStopped()`.
4. Keep microphone and speaker permissions in the Android layer. The core
   assumes permission checks happen before calling into the session.
5. Surface `PttEvent` callbacks to the UI for granted, denied, remote speaking,
   disconnected, and error states.

## Qualcomm implementation points

Implement `QualcommPttAdapter` with the device-specific APIs available to the
target:

- `initialize(...)`: attach to the right subscription, IMS service, modem
  service, or carrier PTT stack.
- `requestFloor(...)` / `releaseFloor(...)`: perform the floor-control
  operation and return the authoritative grant/deny result.
- `enableTransmitAudio(...)` / `disableTransmitAudio(...)`: route microphone
  input to the Qualcomm voice path, including codec/sample-rate configuration
  from `PttSessionConfig`.
- `enableReceiveAudio(...)` / `disableReceiveAudio(...)`: route remote talker
  audio to the requested output route.
- `shutdown(...)`: detach callbacks and release modem/audio resources.

The checked-in gateway deliberately avoids importing proprietary Qualcomm
classes. In a product tree, put those imports in the concrete adapter module
only, leaving the PTT state machine portable and testable on the JVM.

## Example wiring

```java
PttSessionConfig config = new PttSessionConfig("ops", "radio-123", "EVS", 16000);
QualcommPttProfile profile = new QualcommPttProfile("sub-1", "ims", "speaker", 1);
QualcommPttAdapter deviceAdapter = new DeviceQualcommPttAdapter(context);

PttSession session = new PttSession(
    config,
    new QualcommPttGateway(profile, deviceAdapter),
    event -> updateNotificationAndUi(event)
);

session.connect();
```

## Verification

Use the pure-Java scripts before adding Android-specific dependencies:

```sh
./scripts/build.sh
./scripts/test.sh
```

After wiring a real Qualcomm adapter, add device or emulator coverage for:

- denied floor requests while another talker owns the floor,
- loss of service during transmit,
- hardware key release while the floor request is still pending,
- Bluetooth, speaker, and earpiece route changes,
- lifecycle cleanup when the foreground service is stopped.
