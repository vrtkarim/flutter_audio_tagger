package com.creadv.flutter_audio_tagger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Verifies that editing methods return the edited bytes without touching the source file.
 */
public class NonDestructiveEditTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void setTags_writesToTemporaryCopyAndLeavesOriginalUntouched() throws Exception {
        File source = copyFixture("song.mp3");
        File cacheDir = temporaryFolder.newFolder("cache");
        byte[] original = Files.readAllBytes(source.toPath());

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("filePath", source.getAbsolutePath());
        arguments.put("artist", "New Artist");
        arguments.put("title", "New Title");

        MethodChannel.Result result = mock(MethodChannel.Result.class);
        createPlugin(cacheDir).onMethodCall(new MethodCall("setTags", arguments), result);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(result).success(captor.capture());

        assertArrayEquals("the original file must not be modified",
                original, Files.readAllBytes(source.toPath()));

        Map<?, ?> payload = (Map<?, ?>) captor.getValue();
        assertEquals("mp3", payload.get("extension"));
        byte[] edited = (byte[]) payload.get("musicData");
        assertNotNull(edited);

        File editedFile = temporaryFolder.newFile("edited.mp3");
        Files.write(editedFile.toPath(), edited);
        AudioFile audioFile = AudioFileIO.read(editedFile);
        assertEquals("New Artist", audioFile.getTag().getFirst(FieldKey.ARTIST));
        assertEquals("New Title", audioFile.getTag().getFirst(FieldKey.TITLE));

        File[] leftovers = cacheDir.listFiles(
                (dir, name) -> name.startsWith("flutter_audio_tagger_"));
        assertNotNull(leftovers);
        assertEquals("the temporary copy must be deleted", 0, leftovers.length);
    }

    @Test
    public void setTagsAndArtwork_acceptsNullArtwork() throws Exception {
        File source = copyFixture("song-without-artwork.mp3");
        File cacheDir = temporaryFolder.newFolder("cache-artwork");
        byte[] original = Files.readAllBytes(source.toPath());

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("filePath", source.getAbsolutePath());
        arguments.put("album", "New Album");
        arguments.put("artwork", null);

        MethodChannel.Result result = mock(MethodChannel.Result.class);
        createPlugin(cacheDir).onMethodCall(new MethodCall("setTagsAndArtwork", arguments), result);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(result).success(captor.capture());

        assertArrayEquals("the original file must not be modified",
                original, Files.readAllBytes(source.toPath()));

        Map<?, ?> payload = (Map<?, ?>) captor.getValue();
        byte[] edited = (byte[]) payload.get("musicData");
        File editedFile = temporaryFolder.newFile("edited-without-artwork.mp3");
        Files.write(editedFile.toPath(), edited);
        assertEquals("New Album", AudioFileIO.read(editedFile).getTag().getFirst(FieldKey.ALBUM));
    }

    private File copyFixture(String name) throws Exception {
        File file = temporaryFolder.newFile(name);
        try (InputStream in = getClass().getResourceAsStream("/silence.mp3")) {
            assertNotNull("test fixture is missing", in);
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return file;
    }

    private FlutterAudioTaggerPlugin createPlugin(File cacheDir) throws Exception {
        FlutterAudioTaggerPlugin plugin = new FlutterAudioTaggerPlugin();
        Context context = mock(Context.class);
        when(context.getCacheDir()).thenReturn(cacheDir);

        Field contextField = FlutterAudioTaggerPlugin.class.getDeclaredField("context");
        contextField.setAccessible(true);
        contextField.set(plugin, context);
        return plugin;
    }
}
