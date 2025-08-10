# flutter_audio_tagger

A Flutter plugin for reading and editing audio file metadata (tags) with support for multiple audio formats.

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.creadv.audiotagger&hl=en">
    <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" height="40"/>
  </a>
</p>

<p align="center">
  <a href="https://pub.dev/packages/flutter_audio_tagger">
    <img src="https://img.shields.io/pub/v/flutter_audio_tagger.svg" alt="pub package" height="40"/>
  </a>
</p>

## Features

- **Read metadata** from audio files
- **Edit metadata** fields including artwork
- **Support for multiple audio formats**
- **Easy to use** with a simple Tag data model

## Supported Audio Formats

- **MP3** (.mp3)
- **MP4 Audio** (.mp4, .m4a, .m4p)
- **Ogg Vorbis** (.ogg)
- **FLAC** (.flac)
- **WAV** (.wav)
- **AIF** (.aif)
- **DSF** (.dsf)
- **WMA** (.wma)


## Supported Metadata Fields

The plugin currently supports editing the following metadata fields:

- **Artist** - The artist name
- **Title** - The song title
- **Album** - The album name
- **Year** - Release year
- **Genre** - Music genre
- **Language** - Language of the track
- **Composer** - Composer name
- **Country** - Country of origin
- **Quality** - Audio quality information
- **Lyrics** - Song lyrics
- **Artwork** - Album cover image

## Installation

Add this to your package's `pubspec.yaml` file:

```yaml
dependencies:
  flutter_audio_tagger: ^1.1.2
```

## Usage

### Basic Example

```dart
import 'package:flutter_audio_tagger/flutter_audio_tagger.dart';
import 'package:flutter_audio_tagger/tag.dart';

final FlutterAudioTagger tagger = FlutterAudioTagger();

// Read all metadata from an audio file
Tag? tag = await tagger.getAllTags('/path/to/audio/file.mp3');
print('Artist: ${tag?.artist}');
print('Title: ${tag?.title}');

// Edit metadata - Always use the Tag data type
Tag updatedTag = Tag(
  artist: 'New Artist Name',
  title: 'New Song Title',
  album: 'New Album',
  year: '2024',
  genre: 'Rock',
  language: null,        // Set to null if you don't want to change
  composer: '',          // Set to empty string to clear the field
  country: tag?.country, // Keep existing value
  quality: tag?.quality, // Keep existing value
  lyrics: 'New lyrics here...',
  artwork: tag?.artwork, // Keep existing artwork
);

// Save changes (creates a new file with "_edited" suffix)
await tagger.editTags(updatedTag, '/path/to/audio/file.mp3');
```

### Important Usage Notes

**Always use the `Tag` data type when editing metadata, even if you only want to edit one field.**

```dart
// Use Tag object even for single field
Tag updatedTag = Tag(
  artist: 'New Artist',
  title: null,           // Don't change title
  album: null,           // Don't change album
  year: null,            // Don't change year
  genre: null,           // Don't change genre
  language: null,        // Don't change language
  composer: null,        // Don't change composer
  country: null,         // Don't change country
  quality: null,         // Don't change quality
  lyrics: null,          // Don't change lyrics
  artwork: existingTag?.artwork, // Keep existing artwork
);


```

### Reading Specific Data

```dart
// Get only metadata tags (without artwork)
Map<dynamic, dynamic>? tags = await tagger.getTags('/path/to/file.mp3');

// Get only artwork
Uint8List? artwork = await tagger.getArtWork('/path/to/file.mp3');

// Get complete tag information
Tag? completeTag = await tagger.getAllTags('/path/to/file.mp3');
```

### Editing Artwork

```dart
import 'dart:io';

// Load new artwork
File imageFile = File('/path/to/new/artwork.jpg');
Uint8List imageData = await imageFile.readAsBytes();

// Create tag with new artwork
Tag tagWithNewArtwork = Tag(
  artist: existingTag?.artist,
  title: existingTag?.title,
  album: existingTag?.album,
  year: existingTag?.year,
  genre: existingTag?.genre,
  language: existingTag?.language,
  composer: existingTag?.composer,
  country: existingTag?.country,
  quality: existingTag?.quality,
  lyrics: existingTag?.lyrics,
  artwork: imageData, // New artwork
);

await tagger.editTags(tagWithNewArtwork, '/path/to/file.mp3');
```

### Field Management

- **Set to `null`** - Field won't be changed
- **Set to empty string `""`** - Field will be cleared
- **Set to a value** - Field will be updated with the new value

```dart
Tag updateExample = Tag(
  artist: 'Update this field',
  title: null,              // Keep existing title
  album: '',                // Clear the album field
  year: '2024',             // Update year
  // ... other fields
);
```

## File Output

When editing metadata, the plugin creates a new audio file in the device's Downloads folder (`/storage/emulated/0/Download/`) with the same name plus an "_edited" suffix:

- Original: `/storage/music/song.mp3`
- Edited: `/storage/emulated/0/Download/song_edited.mp3`

This preserves your original files while providing the edited versions in an easily accessible location.

## Platform Support

| Platform | Support |
|----------|---------|
| Android  | ✅ Full support |
| iOS      | ❌ Not supported |
| Web      | ❌ Not supported |
| Desktop  | ❌ Not supported |



## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


