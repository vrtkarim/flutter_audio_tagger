package com.example.audiotagger;

import io.flutter.embedding.android.FlutterActivity;

import io.flutter.embedding.android.FlutterActivity;
import androidx.annotation.NonNull;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.generic.AudioFileReader;
import org.jaudiotagger.audio.generic.AudioFileReader2;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "com.creadv.audiotagger/audiotagger";
    @Override
  public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
    super.configureFlutterEngine(flutterEngine);
    new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
        .setMethodCallHandler(
          (call, result) -> {
            if (call.method.equals("getTags")) {

                try{
                    File music = new File((String) call.arguments);
                    AudioFile audioFile = AudioFileIO.read(music);
                    Tag tag = audioFile.getTag();
                    Map<String, String> data = new HashMap<>();
                    String artist = tag.getFirst(FieldKey.ARTIST);
                    data.put("artist",artist);
                    data.put("title",tag.getFirst(FieldKey.TITLE));
                    data.put("album",tag.getFirst(FieldKey.ALBUM));
                    data.put("year",tag.getFirst(FieldKey.YEAR));
                    data.put("genre",tag.getFirst(FieldKey.GENRE));
                    data.put("language",tag.getFirst(FieldKey.LANGUAGE));
                    data.put("composer",tag.getFirst(FieldKey.COMPOSER));
                    data.put("country",tag.getFirst(FieldKey.COUNTRY) );
                    data.put("artwork",tag.getFirst(FieldKey.LYRICS) );
                    data.put("quality",tag.getFirst(FieldKey.QUALITY) );
                    System.out.println("artist: " +artist);
                    result.success(data);
                }catch (Exception e){
                    result.error("TagsError", e.getMessage(), e.getCause());
                    System.out.println("error : "+e);


                }

            } else if(call.method.equals("getArtWork")){
              
                try {
                    File music = new File((String) call.arguments);
                    AudioFile audioFile = AudioFileIO.read(music);
                    Tag tag = audioFile.getTag();
                    byte[] artwork = tag.getFirstArtwork().getBinaryData();
                    result.success(artwork);
                }catch (Exception e){
                    result.error("ArtworkError", e.getMessage(), e.getCause());
                    System.out.println("error : "+e);

                }
            }
          }
        );
  }

}

