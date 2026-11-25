package com.creadv.flutter_audio_tagger;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;


import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.asf.AsfTag;
import org.jaudiotagger.tag.asf.AsfTagField;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.images.AndroidArtwork;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;
import org.jaudiotagger.tag.reference.PictureTypes;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * FlutterAudioTaggerPlugin
 */
public class FlutterAudioTaggerPlugin implements FlutterPlugin, MethodCallHandler {

    private MethodChannel channel;
    private Context context;


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "com.creadv.audiotagger/audiotagger");
        channel.setMethodCallHandler(this);
        context = flutterPluginBinding.getApplicationContext();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("getArtWork")) {
            try {

                File music = new File((String) call.arguments);

                AudioFile audioFile = AudioFileIO.read(music);
                Tag tag = audioFile.getTagOrCreateDefault();
                Artwork artwork = tag.getFirstArtwork();
                if (artwork == null) {
                    result.success(null);
                } else {
                    byte[] image = artwork.getBinaryData();
                    result.success(image);

                }


            } catch (Exception e) {
                result.error("ArtworkError", e.getMessage(), e.getCause());
                System.out.println("error : " + e);

            }
        } else if (call.method.equals("getTags")) {
            try {
                File music = new File((String) call.arguments);
                AudioFile audioFile = AudioFileIO.read(music);

                Tag tag = audioFile.getTagOrCreateDefault();
                Map<String, Object> data = new HashMap<>();
               
                data.put("artist", tag.getFirst(FieldKey.ARTIST).isEmpty() ? "" : tag.getFirst(FieldKey.ARTIST));
                data.put("title", tag.getFirst(FieldKey.TITLE).isEmpty() ? "" : tag.getFirst(FieldKey.TITLE));
                data.put("album", tag.getFirst(FieldKey.ALBUM).isEmpty() ? "" : tag.getFirst(FieldKey.ALBUM));
                data.put("year", tag.getFirst(FieldKey.YEAR).isEmpty() ? "" : tag.getFirst(FieldKey.YEAR));
                data.put("genre", tag.getFirst(FieldKey.GENRE).isEmpty() ? "" : tag.getFirst(FieldKey.GENRE));
                data.put("language", tag.getFirst(FieldKey.LANGUAGE).isEmpty() ? "" : tag.getFirst(FieldKey.LANGUAGE));
                data.put("composer", tag.getFirst(FieldKey.COMPOSER).isEmpty() ? "" : tag.getFirst(FieldKey.COMPOSER));
                data.put("country", tag.getFirst(FieldKey.COUNTRY).isEmpty() ? "" : tag.getFirst(FieldKey.COUNTRY));
                data.put("lyrics", tag.getFirst(FieldKey.LYRICS).isEmpty() ? "" : tag.getFirst(FieldKey.LYRICS));
                data.put("quality", tag.getFirst(FieldKey.QUALITY).isEmpty() ? "" : tag.getFirst(FieldKey.QUALITY));

                result.success(data);
            } catch (Exception e) {
                result.error("TagsError", e.getMessage(), e.getCause());
                System.out.println("error : " + e);


            }
        } else if (call.method.equals("setTags")) {
            File workingCopy = null;
            try {

                Map<String, Object> arguments = (Map<String, Object>) call.arguments;
                String filePath = (String) arguments.get("filePath");
                String artist = (String) arguments.get("artist");
                String title = (String) arguments.get("title");
                String album = (String) arguments.get("album");
                String year = (String) arguments.get("year");
                String genre = (String) arguments.get("genre");
                String language = (String) arguments.get("language");
                String composer = (String) arguments.get("composer");
                String country = (String) arguments.get("country");
                String quality = (String) arguments.get("quality");
                String lyrics = (String) arguments.get("lyrics");

                if (filePath == null || filePath.isEmpty()) {
                    result.error("No specified path", "please specify the music file path.", null);
                    return;
                }

                File originalFile = new File(filePath);
                AudioFile audioFile = AudioFileIO.read(originalFile);
                Tag tag = audioFile.getTagOrCreateDefault();
                if (artist != null) {

                        tag.deleteField(FieldKey.ARTIST);
                        tag.setField(FieldKey.ARTIST, artist);
                   
                }
                if (lyrics != null) {

                        tag.deleteField(FieldKey.LYRICS);
                        tag.setField(FieldKey.LYRICS, lyrics);
                   
                }
                if (title != null) {

                        tag.deleteField(FieldKey.TITLE);
                        tag.setField(FieldKey.TITLE, title);
                   
                }
                if (album != null) {
                                           tag.deleteField(FieldKey.ALBUM);
                        tag.setField(FieldKey.ALBUM, album);
                   
                }
                if (year != null) {

                        tag.deleteField(FieldKey.YEAR);
                        tag.setField(FieldKey.YEAR, year);
                   
                }
                if (genre != null) {

                        tag.deleteField(FieldKey.GENRE);
                        tag.setField(FieldKey.GENRE, genre);
                   
                }
                if (language != null) {
                   
                        tag.deleteField(FieldKey.LANGUAGE);
                        tag.setField(FieldKey.LANGUAGE, language);
                   
                }
                if (composer != null) {

                        tag.deleteField(FieldKey.COMPOSER);
                        tag.setField(FieldKey.COMPOSER, composer);
                   
                }
                if (country != null) {

                        tag.deleteField(FieldKey.COUNTRY);
                        tag.setField(FieldKey.COUNTRY, country);
                   
                }
                if (quality != null) {

                        tag.deleteField(FieldKey.QUALITY);
                        tag.setField(FieldKey.QUALITY, quality);
                   
                }
                audioFile.setTag(tag);

                // Write to a temporary copy so the user's original file is never modified.
                // The edited bytes are returned to the app, which decides where to save them.
                workingCopy = createWorkingCopy(originalFile);
                audioFile.setFile(workingCopy);
                AudioFileIO.write(audioFile);

                result.success(buildEditedFilePayload(
                        workingCopy, getExtensionUsingString(originalFile.getPath())));

            } catch (Exception e) {
                Log.e("FlutterAudioTagger", "SetTags error: " + e.getMessage());
                result.error("SetTagsError", e.getMessage(), e.getCause());

            } finally {
                deleteWorkingCopy(workingCopy);
            }
        } else if (call.method.equals("setArtWork")) {
            File workingCopy = null;
            try {
                Map<String, Object> arguments = (Map<String, Object>) call.arguments;
                // data
                String filePath = (String) arguments.get("filePath");
                byte[] artworkData = (byte[]) arguments.get("artwork");

                if (filePath == null || filePath.isEmpty()) {
                    result.error("No specified path", "please specify the music file path.", null);
                    return;
                }
                if (artworkData == null) {
                    result.error("SetArtworkError", "artwork data must not be null", null);
                    return;
                }

                File originalFile = new File(filePath);
                AudioFile f = AudioFileIO.read(originalFile);
                Tag tag = f.getTagOrCreateDefault();

                Artwork artwork = new AndroidArtwork();
                artwork.setBinaryData(artworkData);
                artwork.setMimeType(getArtworkMimeType(artworkData));
                artwork.setPictureType(PictureTypes.DEFAULT_ID);
                try {
                    tag.deleteArtworkField();
                    tag.setField(artwork);
                    f.setTag(tag);
                } catch (KeyNotFoundException e) {
                    // The tag type does not support artwork.
                }

                // Write to a temporary copy so the user's original file is never modified.
                workingCopy = createWorkingCopy(originalFile);
                f.setFile(workingCopy);
                AudioFileIO.write(f);

                result.success(buildEditedFilePayload(
                        workingCopy, getExtensionUsingString(originalFile.getPath())));

            } catch (Exception e) {
                result.error("SetArtworkError", e.getMessage(), e.getCause());
                Log.e("FlutterAudioTagger", "SetArtwork error: " + e.getMessage());
            } finally {
                deleteWorkingCopy(workingCopy);
            }
        }else if(call.method.equals("setTagsAndArtwork")){
            File workingCopy = null;
            try {

                Map<String, Object> arguments = (Map<String, Object>) call.arguments;
                String filePath = (String) arguments.get("filePath");
                String artist = (String) arguments.get("artist");
                String title = (String) arguments.get("title");
                String album = (String) arguments.get("album");
                String year = (String) arguments.get("year");
                String genre = (String) arguments.get("genre");
                String language = (String) arguments.get("language");
                String composer = (String) arguments.get("composer");
                String country = (String) arguments.get("country");
                String quality = (String) arguments.get("quality");
                String lyrics = (String) arguments.get("lyrics");
                byte[] artworkdata = (byte[]) arguments.get("artwork");

                if (filePath == null || filePath.isEmpty()) {
                    result.error("No specified path", "please specify the music file path.", null);
                    return;
                }

                File originalFile = new File(filePath);
                AudioFile audioFile = AudioFileIO.read(originalFile);
                Tag tag = audioFile.getTagOrCreateDefault();
                if (artist != null) {

                        tag.deleteField(FieldKey.ARTIST);
                        tag.setField(FieldKey.ARTIST, artist);
                   
                }
                if (lyrics != null) {

                        tag.deleteField(FieldKey.LYRICS);
                        tag.setField(FieldKey.LYRICS, lyrics);
                   
                }
                if (title != null) {

                        tag.deleteField(FieldKey.TITLE);
                        tag.setField(FieldKey.TITLE, title);
                   
                }
                if (album != null) {
                                           tag.deleteField(FieldKey.ALBUM);
                        tag.setField(FieldKey.ALBUM, album);
                   
                }
                if (year != null) {

                        tag.deleteField(FieldKey.YEAR);
                        tag.setField(FieldKey.YEAR, year);
                   
                }
                if (genre != null) {

                        tag.deleteField(FieldKey.GENRE);
                        tag.setField(FieldKey.GENRE, genre);
                   
                }
                if (language != null) {
                   
                        tag.deleteField(FieldKey.LANGUAGE);
                        tag.setField(FieldKey.LANGUAGE, language);
                   
                }
                if (composer != null) {

                        tag.deleteField(FieldKey.COMPOSER);
                        tag.setField(FieldKey.COMPOSER, composer);
                   
                }
                if (country != null) {

                        tag.deleteField(FieldKey.COUNTRY);
                        tag.setField(FieldKey.COUNTRY, country);
                   
                }
                if (quality != null) {

                        tag.deleteField(FieldKey.QUALITY);
                        tag.setField(FieldKey.QUALITY, quality);
                   
                }
                // Artwork is optional: when it is null the existing cover is left untouched.
                if (artworkdata != null) {
                    Artwork artwork = new AndroidArtwork();
                    artwork.setBinaryData(artworkdata);
                    artwork.setMimeType(getArtworkMimeType(artworkdata));
                    artwork.setPictureType(PictureTypes.DEFAULT_ID);
                    try {
                        tag.deleteArtworkField();
                        tag.setField(artwork);
                    } catch (KeyNotFoundException e) {
                        // The tag type does not support artwork.
                    }
                }
                audioFile.setTag(tag);

                // Write to a temporary copy so the user's original file is never modified.
                workingCopy = createWorkingCopy(originalFile);
                audioFile.setFile(workingCopy);
                AudioFileIO.write(audioFile);

                result.success(buildEditedFilePayload(
                        workingCopy, getExtensionUsingString(originalFile.getPath())));

            } catch (Exception e) {
                Log.e("FlutterAudioTagger", "SetTagsWithArtwork error: " + e.getMessage());
                result.error("SetTagsWithArtworkError", e.getMessage(), e.getCause());

            } finally {
                deleteWorkingCopy(workingCopy);
            }

        }
         else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    /**
     * Copies the source file into the app cache directory.
     *
     * <p>Tag edits are applied to this throw-away copy so the user's original file is never
     * modified: the plugin's contract is to return the edited bytes and let the app decide
     * where to save them.</p>
     */
    private File createWorkingCopy(File source) throws IOException {
        String name = source.getName();
        int lastDotIndex = name.lastIndexOf('.');
        String suffix = lastDotIndex > 0 ? name.substring(lastDotIndex) : ".tmp";
        File workingCopy = File.createTempFile("flutter_audio_tagger_", suffix, context.getCacheDir());
        Files.copy(source.toPath(), workingCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return workingCopy;
    }

    private Map<String, Object> buildEditedFilePayload(File file, String extension) throws IOException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("musicData", Files.readAllBytes(file.toPath()));
        payload.put("extension", extension);
        return payload;
    }

    private void deleteWorkingCopy(File file) {
        if (file != null && !file.delete()) {
            Log.w("FlutterAudioTagger", "Could not delete temporary file: " + file.getPath());
        }
    }

    /**
     * Detects the image type from its magic bytes so the embedded artwork is not always
     * labelled as JPEG (which breaks PNG/GIF covers in some players).
     */
    String getArtworkMimeType(byte[] data) {
        if (data.length >= 3
                && (data[0] & 0xFF) == 0xFF
                && (data[1] & 0xFF) == 0xD8
                && (data[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if (data.length >= 8
                && (data[0] & 0xFF) == 0x89
                && data[1] == 'P' && data[2] == 'N' && data[3] == 'G'
                && data[4] == 0x0D && data[5] == 0x0A
                && data[6] == 0x1A && data[7] == 0x0A) {
            return "image/png";
        }
        if (data.length >= 4
                && data[0] == 'G' && data[1] == 'I' && data[2] == 'F' && data[3] == '8') {
            return "image/gif";
        }
        if (data.length >= 2 && data[0] == 'B' && data[1] == 'M') {
            return "image/bmp";
        }
        if (data.length >= 12
                && data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F'
                && data[8] == 'W' && data[9] == 'E' && data[10] == 'B' && data[11] == 'P') {
            return "image/webp";
        }
        return "image/jpeg";
    }

    private String addEditedSuffix(String filePath) {
        File file = new File(filePath);
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');

        if (lastDotIndex == -1) {
            return fileName + System.currentTimeMillis();
        } else {

            String nameWithoutExtension = fileName.substring(0, lastDotIndex);
            String extension = fileName.substring(lastDotIndex);
            return nameWithoutExtension + System.currentTimeMillis() + extension;
        }
    }
    String getExtensionUsingString(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }

        // Get just the filename from the path
        String fileName = new File(filePath).getName();

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return ""; // No extension found
    }


}
