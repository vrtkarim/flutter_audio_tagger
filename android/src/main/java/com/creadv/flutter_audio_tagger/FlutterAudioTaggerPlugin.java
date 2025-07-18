package com.creadv.flutter_audio_tagger;

import androidx.annotation.NonNull;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FlutterAudioTaggerPlugin */
public class FlutterAudioTaggerPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(),"com.creadv.audiotagger/audiotagger");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
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
            }else if(call.method.equals("getTags")){
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
            }
    
    
    
    else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
