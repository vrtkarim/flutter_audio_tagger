import 'package:flutter/services.dart';
import 'package:flutter_audio_tagger/flutter_audio_tagger.dart';
import 'package:flutter_audio_tagger/tag.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  const channel = MethodChannel('com.creadv.audiotagger/audiotagger');
  final messenger =
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger;

  tearDown(() {
    messenger.setMockMethodCallHandler(channel, null);
  });

  test('Tag.fromMap accepts a map without lyrics', () {
    final tag = Tag.fromMap({'title': 'Title', 'artist': 'Artist'});

    expect(tag.title, 'Title');
    expect(tag.artist, 'Artist');
    expect(tag.lyrics, isNull);
  });

  test('getAllTags merges artwork into the tag', () async {
    messenger.setMockMethodCallHandler(channel, (call) async {
      switch (call.method) {
        case 'getTags':
          return <String, Object?>{'title': 'Title', 'artist': 'Artist'};
        case 'getArtWork':
          return Uint8List.fromList([1, 2, 3]);
        default:
          return null;
      }
    });

    final tag = await FlutterAudioTagger().getAllTags('/tmp/song.mp3');

    expect(tag, isNotNull);
    expect(tag!.title, 'Title');
    expect(tag.lyrics, isNull);
    expect(tag.artwork, Uint8List.fromList([1, 2, 3]));
  });

  test(
    'getAllTags returns null when the native side returns no tags',
    () async {
      messenger.setMockMethodCallHandler(channel, (call) async => null);

      expect(await FlutterAudioTagger().getAllTags('/tmp/song.mp3'), isNull);
    },
  );

  test('setArtWork rejects null image data instead of hanging', () async {
    expect(
      FlutterAudioTagger().setArtWork(null, '/tmp/song.mp3'),
      throwsArgumentError,
    );
  });
}
