import 'dart:async';
import 'dart:typed_data';
import 'dart:ui' as UI;

import 'package:flutter/services.dart';
import 'package:flutter/painting.dart';

typedef void ImageLoaded(String url, UI.Image img);

class ImageLoader {
  Future<UI.Image> loadImage(String imageAssetPath) async {
    final ByteData data = await rootBundle.load(imageAssetPath);
    final Completer<UI.Image> completer = Completer();
    UI.decodeImageFromList(Uint8List.view(data.buffer), (UI.Image img) {
      return completer.complete(img);
    });
    return completer.future;
  }

  final ImageLoaded listener;
  Map<String, UI.Image> images = Map();
  Map<String, String> headers;
  ImageLoader(this.listener, this.headers);

  UI.Image getImage(String url) {
    if (images.containsKey(url)) {
      return images[url];
    }
    NetworkImage provider = NetworkImage(
      url,
      headers: headers,
      scale: 1,
    );
    ImageStream stream = provider.resolve(ImageConfiguration.empty);
    stream.addListener(ImageStreamListener((info, isSync) {
      images[url] = info.image;
      listener(url, info.image);
    }, onError: (e, s) {}));
    return null;
  }
}
