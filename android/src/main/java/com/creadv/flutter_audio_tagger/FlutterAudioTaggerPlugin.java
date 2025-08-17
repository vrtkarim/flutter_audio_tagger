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
            try {

                //File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

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

                File originalFile = new File(filePath);
                AudioFile audioFile = AudioFileIO.read(originalFile);

                Tag tag = audioFile.getTagOrCreateDefault();


                if (filePath == null) {
                    result.error("No specified path", "please specify the music file path.", "");
                    return;
                }
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
                AudioFileIO.write(audioFile);
//                File musicfile = new File(downloadsDir, addEditedSuffix(filePath));
//
//                try (FileOutputStream fos = new FileOutputStream(musicfile)){
//                    byte[] music_data = Files.readAllBytes(Paths.get(audioFile.getFile().getPath()));
//                    fos.write(music_data);
//                    fos.flush();
//
//                } catch (Exception e) {
//                    Log.e("FlutterAudioTagger", "SetTags error: " + e.getMessage());
//                    result.error("SetTagsError", e.getMessage(), e.getCause());
//
//                }
//                Log.e("filesaved", "file saved: " + "file saved a zbi using tags");
//                MediaScannerConnection.scanFile(context, new String[]{musicfile.getPath()}, null, null);
                byte[] music_data = Files.readAllBytes(Paths.get(audioFile.getFile().getPath()));
                String extensionOfTheFile = getExtensionUsingString(audioFile.getFile().getPath());
                Map<String, Object> payload = new HashMap<>();
                payload.put("musicData", music_data);
                payload.put("extension", extensionOfTheFile);
                result.success(payload);

            } catch (Exception e) {
                Log.e("FlutterAudioTagger", "SetTags error: " + e.getMessage());
                result.error("SetTagsError", e.getMessage(), e.getCause());

            }
        } else if (call.method.equals("setArtWork")) {
            try {
                Map<String, Object> arguments = (Map<String, Object>) call.arguments;
                // data
                String filePath = (String) arguments.get("filePath");
                byte[] artworkData = (byte[]) arguments.get("artwork");
                // process
                if (filePath != null && artworkData != null) {
                    //File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File audioFile = new File(filePath);
                    AudioFile f = AudioFileIO.read(audioFile);
                    Tag tag = f.getTagOrCreateDefault();

                    Artwork artwork = new AndroidArtwork();
                    artwork.setBinaryData(artworkData);
                    artwork.setMimeType("image/jpeg");
                    artwork.setPictureType(PictureTypes.DEFAULT_ID);
                    try {
                        tag.deleteArtworkField();
                        tag.setField(artwork);
                        f.setTag(tag);
                    } catch (KeyNotFoundException e) {
                        //throw new RuntimeException(e);
                    }
                    AudioFileIO.write(f);
//                    File musicfile = new File(downloadsDir, addEditedSuffix(filePath));
//                    try(FileOutputStream fos = new FileOutputStream(musicfile)) {
//
//                        byte[] music_data = Files.readAllBytes(Paths.get(f.getFile().getPath()));
//                        fos.write(music_data);
//                        fos.flush();
//
//                    } catch (Exception e) {
//                        result.error("SetArtworkError", e.getMessage(), e.getCause());
//                    }
//                    MediaScannerConnection.scanFile(context, new String[]{musicfile.getPath()}, null, null);
                    byte[] music_data = Files.readAllBytes(Paths.get(f.getFile().getPath()));
                    String extensionOfTheFile = getExtensionUsingString(f.getFile().getPath());
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("musicData", music_data);
                    payload.put("extension", extensionOfTheFile);
                    result.success(payload);

                }else{
//                    result.error("SetArtworkError","no file specified", "");
//                    Log.e("setartwork error", "no file specified " );
                    return;
                }



            } catch (Exception e) {
                result.error("SetArtworkError", e.getMessage(), e.getCause());
                Log.e("FlutterAudioTagger", "SetArtwork error: " + e.getMessage());
            }
        }else if(call.method.equals("setTagsAndArtwork")){
            try {

                //File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

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

                File originalFile = new File(filePath);
                AudioFile audioFile = AudioFileIO.read(originalFile);
                Tag tag = audioFile.getTagOrCreateDefault();


                if (filePath == null) {
                    result.error("No specified path", "please specify the music file path.", "");
                    return;
                }
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
                Artwork artwork = new AndroidArtwork();
                    artwork.setBinaryData(artworkdata);
                    artwork.setMimeType("image/jpeg");
                    artwork.setPictureType(PictureTypes.DEFAULT_ID);
                    try {
                        tag.deleteArtworkField();
                        tag.setField(artwork);
                       
                    } catch (KeyNotFoundException e) {
                        //throw new RuntimeException(e);
                    }
                audioFile.setTag(tag);
                AudioFileIO.write(audioFile);
//              
                byte[] music_data = Files.readAllBytes(Paths.get(audioFile.getFile().getPath()));
                String extensionOfTheFile = getExtensionUsingString(audioFile.getFile().getPath());
                Map<String, Object> payload = new HashMap<>();
                payload.put("musicData", music_data);
                payload.put("extension", extensionOfTheFile);
                result.success(payload);

            } catch (Exception e) {
                Log.e("FlutterAudioTagger", "SetTagsWithArtwork error: " + e.getMessage());
                result.error("SetTagsWithArtworkError", e.getMessage(), e.getCause());

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
