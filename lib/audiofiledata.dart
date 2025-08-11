import 'dart:typed_data';

class AudioFileData {
  Uint8List musicData;
  String fileExtension;
  AudioFileData({required this.musicData, required this.fileExtension});
}
