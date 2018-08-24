import 'dart:async';

import 'package:flutter/services.dart';
import 'package:multi_image_picker/picker.dart';

class Asset {
  String _identifier;
  ByteData _thumbData;
  ByteData _imageData;

  Asset(this._identifier);

  String get _channel {
    return 'multi_image_picker/image/$_identifier';
  }

  ByteData get thumbData {
    return _thumbData;
  }

  ByteData get imageData {
    return _imageData;
  }

  String get identifier {
    return _identifier;
  }

  void releaseThumb() {
    _thumbData = null;
  }

  void releaseOriginal() {
    _imageData = null;
  }

  void release() {
    releaseThumb();
    releaseOriginal();
  }

  Future<dynamic> requestThumbnail(int width, int height) {
    Completer completer = new Completer();
    BinaryMessages.setMessageHandler(_channel, (ByteData message) {
      _thumbData = message;
      completer.complete(message);
      BinaryMessages.setMessageHandler(_channel, null);
    });

    MultiImagePicker.requestThumbnail(_identifier, width, height);
    return completer.future;
  }

  Future<dynamic> requestOriginal() {
    Completer completer = new Completer();
    BinaryMessages.setMessageHandler(_channel, (ByteData message) {
      _imageData = message;
      completer.complete(message);
      BinaryMessages.setMessageHandler(_channel, null);
    });

    MultiImagePicker.requestOriginal(_identifier);
    return completer.future;
  }
}
