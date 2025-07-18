import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_audio_tagger_platform_interface.dart';

/// An implementation of [FlutterAudioTaggerPlatform] that uses method channels.
class MethodChannelFlutterAudioTagger extends FlutterAudioTaggerPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_audio_tagger');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
