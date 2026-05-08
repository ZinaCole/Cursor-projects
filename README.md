# Qualcomm-ready Push-to-Talk starter

This repository contains a small, buildable Push-to-Talk (PTT) starter that
keeps Qualcomm-specific integration behind adapter interfaces. The checked-in
code is intentionally pure Java so it can be compiled and tested without
proprietary Qualcomm SDK artifacts, Android Gradle tooling, or device images.

## What is included

- A PTT session state machine for floor request, transmit, receive, and release
  flows.
- A vendor adapter boundary that can be implemented with Qualcomm modem,
  QMI/IMS, audio-routing, or device-management APIs.
- A Qualcomm gateway that maps the generic PTT session contract to a
  Qualcomm-facing interface.
- Focused tests that exercise the floor-grant and floor-denial paths.
- Integration notes for wiring the starter into an Android service and
  Qualcomm SDK/device-specific implementation.

## Repository layout

```text
src/main/java/com/cursor/ptt/              PTT core contracts and state machine
src/main/java/com/cursor/ptt/qualcomm/     Qualcomm integration seam
src/test/java/com/cursor/ptt/              Self-contained JVM tests
docs/qualcomm-ptt.md                       Android/Qualcomm integration guide
scripts/build.sh                           Compile production sources
scripts/test.sh                            Compile and run tests
```

## Build and test

```sh
./scripts/build.sh
./scripts/test.sh
```

The scripts only require a JDK and `rg` (ripgrep), which are available in the
Cursor Cloud image used for this repository.
