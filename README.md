# flutter_audio_tagger

A Flutter plugin for reading and editing audio file metadata (ID3 tags) on Android. The plugin reads tag data from audio files and returns edited files as raw bytes (`Uint8List`), giving your app full control over where and how the resulting file is saved.

<p align="center">
  <a href="https://pub.dev/packages/flutter_audio_tagger">
    <img src="https://img.shields.io/pub/v/flutter_audio_tagger.svg" alt="pub package" height="58"/>
  </a>
</p>

## Platform Support

| Platform | Supported |
| -------- | --------- |
| Android  | Yes       |
| iOS      | No        |
| Web      | No        |
| Desktop  | No        |

## Features

- Read all metadata tags from an audio file, including artwork
- Read metadata tags only (without artwork)
- Read artwork only
- Edit metadata tags (without touching artwork)
- Edit artwork only
- Edit metadata tags and artwork together in a single call
- Returns the complete modified audio file as bytes (`Uint8List`) along with the file extension, so your app decides where to write it

## Supported Audio Formats

MP3, MP4/M4A/M4P, OGG Vorbis, FLAC, WAV, AIF, DSF, WMA.

## Supported Metadata Fields

| Field      | Description              |
| ---------- | ------------------------ |
| `artist`   | Artist name              |
| `title`    | Track title              |
| `album`    | Album name               |
| `year`     | Release year             |
| `genre`    | Music genre              |
| `language` | Language of the track    |
| `composer` | Composer name            |
| `country`  | Country of origin        |
| `quality`  | Audio quality descriptor |
| `lyrics`   | Song lyrics              |
| `artwork`  | Album cover image bytes  |

## Installation

Add the dependency to your `pubspec.yaml`:

```yaml
dependencies:
  flutter_audio_tagger: ^5.1.1
```

## Android Configuration

In your `android/app/build.gradle`, disable minification and resource shrinking for the release build:

```gradle
buildTypes {
    release {
        minifyEnabled false
        shrinkResources false
    }
}
```

## API Overview

```dart
import 'package:flutter_audio_tagger/flutter_audio_tagger.dart';
import 'package:flutter_audio_tagger/tag.dart';
import 'package:flutter_audio_tagger/audiofiledata.dart';

final tagger = FlutterAudioTagger();
```

### Reading

| Method                    | Returns              | Description                           |
| ------------------------- | -------------------- | ------------------------------------- |
| `getAllTags(String path)` | `Future<Tag?>`       | All metadata fields including artwork |
| `getTags(String path)`    | `Future<Map?>`       | Metadata fields only (no artwork)     |
| `getArtWork(String path)` | `Future<Uint8List?>` | Artwork bytes only                    |

### Editing

All editing methods return `Future<AudioFileData>`. The `AudioFileData` object contains:

- `musicData` (`Uint8List`) -- the full modified audio file as bytes
- `fileExtension` (`String`) -- the original file extension (e.g. `mp3`, `flac`)

| Method                                          | Description                     |
| ----------------------------------------------- | ------------------------------- |
| `editTags(Tag tag, String path)`                | Edit metadata only (no artwork) |
| `setArtWork(Uint8List? imageData, String path)` | Edit artwork only               |
| `editTagsAndArtwork(Tag tag, String path)`      | Edit both metadata and artwork  |

## Usage

### Reading metadata

```dart
// Read all tags including artwork
Tag? tag = await tagger.getAllTags('/path/to/file.mp3');
print(tag?.artist);
print(tag?.title);

// Read tags without artwork
Map<dynamic, dynamic>? tags = await tagger.getTags('/path/to/file.mp3');

// Read artwork only
Uint8List? artwork = await tagger.getArtWork('/path/to/file.mp3');
```

### Editing metadata

Always pass a `Tag` object, even when updating a single field.

- Set a field to `null` to leave it unchanged.
- Set a field to an empty string `""` to clear it.
- Set a field to a value to update it.

```dart
Tag updatedTag = Tag(
  artist: 'New Artist',
  title: 'New Title',
  album: null,           // unchanged
  year: '2025',
  genre: '',             // cleared
  language: null,
  composer: null,
  country: null,
  quality: null,
  lyrics: 'New lyrics',
  artwork: null,         // not used by editTags; pass artwork to editTagsAndArtwork
);

AudioFileData result = await tagger.editTags(updatedTag, '/path/to/file.mp3');
```

### Editing artwork

```dart
import 'dart:io';

File imageFile = File('/path/to/cover.jpg');
Uint8List imageData = await imageFile.readAsBytes();

// Set artwork only
AudioFileData result = await tagger.setArtWork(imageData, '/path/to/file.mp3');
```

### Editing tags and artwork together

```dart
Tag tagWithArtwork = Tag(
  artist: 'Artist',
  title: 'Title',
  album: 'Album',
  year: '2025',
  genre: 'Pop',
  language: null,
  composer: null,
  country: null,
  quality: null,
  lyrics: null,
  artwork: imageData,
);

AudioFileData result = await tagger.editTagsAndArtwork(tagWithArtwork, '/path/to/file.mp3');
```

## Saving the edited file

Every editing method returns an `AudioFileData` object. The plugin does **not** write the file to disk. Your app receives the raw bytes and is responsible for saving them. Use `dart:io` to write the bytes to any path your app has access to.

```dart
import 'dart:io';

// Edit tags
AudioFileData editedFile = await tagger.editTags(updatedTag, originalFilePath);

// Write the bytes to a file
String savePath = '/storage/emulated/0/Download/edited.${editedFile.fileExtension}';
File outputFile = File(savePath);
await outputFile.writeAsBytes(editedFile.musicData);
```

You can save to any writable location: the app's documents directory, the downloads folder, an external storage path, or overwrite the original file. The `fileExtension` field tells you the format of the audio data so you can construct the correct filename.

```dart
import 'package:path_provider/path_provider.dart';

// Save to app documents directory
Directory appDir = await getApplicationDocumentsDirectory();
String fileName = 'my_song.${editedFile.fileExtension}';
File file = File('${appDir.path}/$fileName');
await file.writeAsBytes(editedFile.musicData);

// Or overwrite the original file
File original = File(originalFilePath);
await original.writeAsBytes(editedFile.musicData);
```

## License

MIT License. See the [LICENSE](LICENSE) file for details.
