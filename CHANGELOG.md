## 6.0.0

* Tag edits are now non-destructive: changes are applied to a temporary copy and returned as bytes, so the original audio file is no longer modified.
* `editTagsAndArtwork` no longer fails when `Tag.artwork` is `null`.
* `setArtWork` now throws an `ArgumentError` for `null` image data instead of leaving the returned future pending forever.
* `Tag.fromMap` no longer crashes when the `lyrics` key is missing.
* Embedded artwork now uses the correct MIME type for PNG, GIF, BMP and WebP images instead of always being labelled as JPEG.

## 5.1.3

* file are not saved using the plugin, the plugin returns the bytes of the edited file, and the saving should be handled in the application. Thanks.