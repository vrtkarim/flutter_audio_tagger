import 'dart:typed_data';

class Tag {
  final String? artist;
  final String? title;
  final String? album;
  final String? year;
  final String? genre;
  final String? language;
  final String? composer;
  final String? country;
  final String? lyrics;
  final String? quality;
  final Uint8List? artwork;

  Tag({
    this.artwork,
    this.artist,
    this.title,
    this.album,
    this.year,
    this.genre,
    this.language,
    this.composer,
    this.country,
    this.lyrics,
    this.quality,
  });

  factory Tag.fromMap(Map<dynamic, dynamic> map) {
    return Tag(
      artist: map['artist'] as String?,
      title: map['title'] as String?,
      album: map['album'] as String?,
      year: map['year'] as String?,
      genre: map['genre'] as String?,
      lyrics: map['lyrics'] as String,
      language: map['language'] as String?,
      composer: map['composer'] as String?,
      country: map['country'] as String?,
      artwork: map['artwork'] as Uint8List?,
      quality: map['quality'] as String?,
    );
  }

  

  @override
  String toString() {
    return 'Tag(artist: $artist, title: $title, album: $album, year: $year, genre: $genre, language: $language, composer: $composer, country: $country, quality: $quality)';
  }

  static Map<String, String> createMapWithPath(Tag tag, String filePath) {
    return {
      'filePath': filePath,
      'artist': tag.artist ?? "",
      'title': tag.title ?? "",
      'album': tag.album ?? "",
      'year': tag.year ?? "",
      'genre': tag.genre?? "",
      /* 'lyrics': tag.lyrics ?? "",  */
      'language': tag.language?? "",
      'composer': tag.composer?? "",
      'country': tag.country?? "",
      'quality': tag.quality?? "",
      'lyrics' :tag.lyrics?? ""
    };
  }
}
