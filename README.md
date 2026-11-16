# SystemUpdaterApp-

Privileged Android app that downloads A/B OTA updates from [system-updater-backend](https://github.com/nishank23/system-updater-backend) and installs them through `update_engine`. Built for a client's AOSP-based devices.

It starts from AOSP's SystemUpdaterSample. The sample reads update configs from local files; this version gets them from a backend per device.

## What it does

- **Asks the backend for its build.** Sends the device serial number and current build number to `GET /api/v1/update`. The backend picks the device's channel and returns the newest build, or 404 if the device is up to date. This version checks over HTTP only; a later client build, not public, also listens on the backend's WebSocket and checks as soon as a build is published.
- **Downloads the OTA package** in the background with Fetch2, with pause, resume and retry.
- **Applies it with `update_engine`.** Streaming and non-streaming payloads, progress and engine status on screen, suspend, resume, cancel and reset.
- **Controls the A/B slot switch.** Switch to the new slot on the next reboot (the default), or install now and switch later.

## How it ships

The app calls `@SystemApi` methods, so it cannot run as a normal app. It is built with the platform tree using [`Android.bp`](Android.bp) as a `privileged` app signed with the `platform` certificate, with permissions granted in [`privapp-permissions-platform.xml`](privapp-permissions-platform.xml). The Gradle build is for editing in Android Studio.

| | |
|---|---|
| Language | Java |
| SDK | min 26, target 30 |
| Libraries | AndroidX WorkManager, Fetch2, Guava, Material |
| Needs | AOSP-based build with A/B partitions |

## Project structure

```text
app/src/main/java/com/lighko/updates/
  UpdateManager.java       wraps UpdateEngine: apply, suspend, resume, reset, slot switch
  services/                download worker, install service, backend client
  ui/                      main screen and settings
  util/                    payload specs, update_engine statuses and error codes
app/src/androidTest/       config and payload parsing tests with sample OTA files
Android.bp                 platform build: privileged, platform certificate
```

## License

Apache License 2.0, as the AOSP sample it is based on.
