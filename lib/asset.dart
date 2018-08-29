import 'dart:async';

import 'package:flutter/services.dart';
import 'package:multi_image_picker/picker.dart';

class Asset {
  String _identifier;
  int _originalWidth, _originalHeight;
  ByteData _thumbData;
  ByteData _imageData;

  Asset(
    this._identifier,
    this._originalWidth,
    this._originalHeight,
  );

  String get _channel {
    return 'multi_image_picker/image/$_identifier';
  }

  ByteData get thumbData {
    return _thumbData;
  }

  int get originalWidth {
    return _originalWidth;
  }

  int get originalHeight {
    return _originalHeight;
  }

  bool get isLandscape {
    return _originalWidth > _originalHeight;
  }

  bool get isPortrait {
    return _originalWidth < _originalHeight;
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

  Future<dynamic> requestThumbnail(int width, int height) async {
    assert(width != null);
    assert(height != null);

    if (width != null && width < 0) {
      throw new ArgumentError.value(width, 'width cannot be negative');
    }

    if (height != null && height < 0) {
      throw new ArgumentError.value(height, 'height cannot be negative');
    }

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
