import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_audio_tagger/flutter_audio_tagger.dart';
import 'package:flutter_audio_tagger/flutter_audio_tagger_platform_interface.dart';
import 'package:flutter_audio_tagger/flutter_audio_tagger_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterAudioTaggerPlatform
    with MockPlatformInterfaceMixin
    implements FlutterAudioTaggerPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutterAudioTaggerPlatform initialPlatform = FlutterAudioTaggerPlatform.instance;

  test('$MethodChannelFlutterAudioTagger is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterAudioTagger>());
  });

  test('getPlatformVersion', () async {
    FlutterAudioTagger flutterAudioTaggerPlugin = FlutterAudioTagger();
    MockFlutterAudioTaggerPlatform fakePlatform = MockFlutterAudioTaggerPlatform();
    FlutterAudioTaggerPlatform.instance = fakePlatform;

    expect(await flutterAudioTaggerPlugin.getPlatformVersion(), '42');
  });
}
