import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_audio_tagger_method_channel.dart';

abstract class FlutterAudioTaggerPlatform extends PlatformInterface {
  /// Constructs a FlutterAudioTaggerPlatform.
  FlutterAudioTaggerPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterAudioTaggerPlatform _instance = MethodChannelFlutterAudioTagger();

  /// The default instance of [FlutterAudioTaggerPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterAudioTagger].
  static FlutterAudioTaggerPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterAudioTaggerPlatform] when
  /// they register themselves.
  static set instance(FlutterAudioTaggerPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
