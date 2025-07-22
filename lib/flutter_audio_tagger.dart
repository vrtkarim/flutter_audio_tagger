import 'dart:typed_data';

import 'package:flutter/services.dart';
import 'package:flutter_audio_tagger/tag.dart';

import 'flutter_audio_tagger_platform_interface.dart';

class FlutterAudioTagger {
  static const platform = MethodChannel('com.creadv.audiotagger/audiotagger');

  Future<String?> getPlatformVersion() {
    return FlutterAudioTaggerPlatform.instance.getPlatformVersion();
  }

  Future<Uint8List?> getArtWork(String path) async {
    try {
      final result = await platform.invokeMethod<Uint8List>('getArtWork', path);

      return result;
    } catch (e) {
      print(e);
    }
    return null;
  }

  Future<Map<dynamic, dynamic>?> getTags(String path) async {
    try {
      final result = await platform.invokeMethod<Map>('getTags', path);
      Map<dynamic, dynamic>? tags = result;
      return tags;
    } catch (e) {
      print(e);
    }
  }

  Future<Tag?> getAllTags(String path) async {
    try {
      Tag tag = Tag();
      late Map<dynamic, dynamic>? data;
      data = await getTags(path);
      Uint8List? artwork = await getArtWork(path);
      data!['artwork'] = artwork;
      tag = Tag.fromMap(data);
      return tag;
    } catch (e) {
      print(e);
    }
  }

  Future<void> editTags(Tag tag, String path) async {
    try {
      Map<String, String?> tags = Tag.createMapWithPath(tag, path);
      final result = await platform.invokeMethod<String>('setTags', tags);
      Map<String, dynamic> artwork = Map();
      artwork['artwork'] = tag.artwork;
      artwork['filePath'] = path;
      final artworkresult = await platform.invokeMethod<String>(
      "setArtWork",
      artwork,
    );
    } catch (e) {
      print(e);
    }
  }

  Future<void> setArtWork(Uint8List imagepath, String path) async {
    try {
      Map<String, dynamic> artwork = Map();
      artwork['artwork'] = imagepath;
      artwork['filePath'] = path;

      final artworkresult = await platform.invokeMethod<String>(
        "setArtWork",
        artwork,
      );
    } catch (e) {
      print(e);
    }
  }
  
}
