import 'dart:typed_data';

class AudioFileData {
  Uint8List musicData;
  String filExtension;
  AudioFileData({required this.musicData, required this.filExtension});
}
