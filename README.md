# Suplaunch project

Suplaunch is an open source project. Developed application can be used as Launcher on home automation devices with screen and Android OS (like Sonoff NSPanel PRO).

## Installation

Download the latest release app and use ADB to install it on the device

```
adb install downloaded-file.apk
```

If the app is already installed you can use `-r` option to override existing installation.
After file is installed you can force "home" button click with the following command

```
adb shell input keyevent 3
```

Android will ask you which Launcher you want to use. Select Suplaunch from the list.