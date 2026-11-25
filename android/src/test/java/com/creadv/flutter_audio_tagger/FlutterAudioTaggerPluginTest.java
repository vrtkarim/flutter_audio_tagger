package com.creadv.flutter_audio_tagger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import org.junit.Test;

/**
 * This demonstrates a simple unit test of the Java portion of this plugin's implementation.
 *
 * Once you have built the plugin's example app, you can run these tests from the command
 * line by running `./gradlew testDebugUnitTest` in the `example/android/` directory, or
 * you can run them directly from IDEs that support JUnit such as Android Studio.
 */

public class FlutterAudioTaggerPluginTest {
  @Test
  public void onMethodCall_getPlatformVersion_returnsExpectedValue() {
    FlutterAudioTaggerPlugin plugin = new FlutterAudioTaggerPlugin();

    final MethodCall call = new MethodCall("getPlatformVersion", null);
    MethodChannel.Result mockResult = mock(MethodChannel.Result.class);
    plugin.onMethodCall(call, mockResult);

    verify(mockResult).success("Android " + android.os.Build.VERSION.RELEASE);
  }

  @Test
  public void getArtworkMimeType_detectsCommonImageFormats() {
    FlutterAudioTaggerPlugin plugin = new FlutterAudioTaggerPlugin();

    byte[] jpeg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
    byte[] png = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
    byte[] gif = {'G', 'I', 'F', '8', '9', 'a'};
    byte[] bmp = {'B', 'M', 0x00, 0x00};
    byte[] webp = {'R', 'I', 'F', 'F', 0x00, 0x00, 0x00, 0x00, 'W', 'E', 'B', 'P'};

    assertEquals("image/jpeg", plugin.getArtworkMimeType(jpeg));
    assertEquals("image/png", plugin.getArtworkMimeType(png));
    assertEquals("image/gif", plugin.getArtworkMimeType(gif));
    assertEquals("image/bmp", plugin.getArtworkMimeType(bmp));
    assertEquals("image/webp", plugin.getArtworkMimeType(webp));
  }
}
