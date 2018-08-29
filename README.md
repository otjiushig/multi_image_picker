# multi_image_picker

[![build](https://img.shields.io/travis/Sh1d0w/multi_image_picker.svg)](https://pub.dartlang.org/packages/multi_image_picker)
[![pub package](https://img.shields.io/pub/v/multi_image_picker.svg)](https://pub.dartlang.org/packages/multi_image_picker)

> Flutter plugin that allows you to display multi image picker on iOS and Android.

## iOS

<img src="screenshots/ios-1.png" height="300em" /> <img src="screenshots/ios-2.png" height="300em" /> <img src="screenshots/ios-3.png" height="300em" />

For the iOS picker the plugin uses [BSImagePicker](https://github.com/mikaoj/BSImagePicker)

## Android

<img src="screenshots/android-1.png" height="300em" /> <img src="screenshots/android-2.png" height="300em" /> <img src="screenshots/android-3.png" height="300em" />

For the Android picker the plugin uses [Matisse](https://github.com/zhihu/Matisse)

## Usage

First you need to [add](https://pub.dartlang.org/packages/multi_image_picker#-installing-tab-) the plugin to your project.

### iOS

You need to add those strings to your Info.plist file in order the plugin to work:
```xml
<key>NSPhotoLibraryUsageDescription</key>
<string>Example usage description</string>
<key>NSCameraUsageDescription</key>
<string>Example usage description</string>
```

### Android

You need to request those permissions in AndroidManifest.xml in order the plugin to work:

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

```

For example code usage, please see [here](https://github.com/Sh1d0w/multi_image_picker/blob/master/example/lib/main.dart)

## Theming and localization

You can customize different parts of the gallery picker. For reference see below the available options for the different platforms:

Customization on [Android](https://github.com/Sh1d0w/multi_image_picker/tree/master/doc/android.md)

Customization on [iOS](https://github.com/Sh1d0w/multi_image_picker/tree/master/doc/ios.md)

## API

### pickImages(maxImages: int)

#### maxImages

Type: `int`

Maximum allowed images to be selected.

## TODO

- [x] Add support for more plugin specific options for iOS and Android, that allows more customization
- [ ] Improve docs
- [ ] Allow choosing videos

## Related

- [image_picker](https://pub.dartlang.org/packages/image_picker) - Official Flutter image picker plugin

## License

MIT © [Radoslav Vitanov](https://github.com/Sh1d0w)