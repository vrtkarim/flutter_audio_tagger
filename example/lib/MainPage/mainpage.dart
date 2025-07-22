import 'dart:io';
import 'dart:typed_data';

import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_audio_tagger/flutter_audio_tagger.dart';
import 'package:flutter_audio_tagger/tag.dart';

class MainPage extends StatefulWidget {
  const MainPage({super.key});

  @override
  State<MainPage> createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  Tag? tag;
  String? currentFilePath;
  FlutterAudioTagger flutterAudioTagger = FlutterAudioTagger();

  // Text controllers for editing
  final TextEditingController _artistController = TextEditingController();
  final TextEditingController _titleController = TextEditingController();
  final TextEditingController _albumController = TextEditingController();
  final TextEditingController _yearController = TextEditingController();
  final TextEditingController _genreController = TextEditingController();
  final TextEditingController _languageController = TextEditingController();
  final TextEditingController _composerController = TextEditingController();
  final TextEditingController _countryController = TextEditingController();
  final TextEditingController _qualityController = TextEditingController();
  final TextEditingController _lyricsController =
      TextEditingController(); // Add this line

  bool _isEditing = false;

  @override
  void dispose() {
    _artistController.dispose();
    _titleController.dispose();
    _albumController.dispose();
    _yearController.dispose();
    _genreController.dispose();
    _languageController.dispose();
    _composerController.dispose();
    _countryController.dispose();
    _qualityController.dispose();
    _lyricsController.dispose(); // Add this line
    super.dispose();
  }

  void _populateControllers() {
    _artistController.text = tag?.artist ?? '';
    _titleController.text = tag?.title ?? '';
    _albumController.text = tag?.album ?? '';
    _yearController.text = tag?.year ?? '';
    _genreController.text = tag?.genre ?? '';
    _languageController.text = tag?.language ?? '';
    _composerController.text = tag?.composer ?? '';
    _countryController.text = tag?.country ?? '';
    _qualityController.text = tag?.quality ?? '';
    _lyricsController.text = tag?.lyrics ?? ''; 
  }

  Future<void> _saveChanges() async {
    if (currentFilePath == null) return;

    try {
      final updatedTag = Tag(
        artist: _artistController.text.isEmpty ? null : _artistController.text,
        title: _titleController.text.isEmpty ? null : _titleController.text,
        album: _albumController.text.isEmpty ? null : _albumController.text,
        year: _yearController.text.isEmpty ? null : _yearController.text,
        genre: _genreController.text.isEmpty ? null : _genreController.text,
        language: _languageController.text.isEmpty
            ? null
            : _languageController.text,
        composer: _composerController.text.isEmpty
            ? null
            : _composerController.text,
        country: _countryController.text.isEmpty
            ? null
            : _countryController.text,
        quality: _qualityController.text.isEmpty
            ? null
            : _qualityController.text,
        lyrics:
            _lyricsController
                .text
                .isEmpty // Add this line
            ? null
            : _lyricsController.text,
        artwork: tag?.artwork,
      );

      await flutterAudioTagger.editTags(updatedTag, currentFilePath!);

      // Refresh tags
      tag = await flutterAudioTagger.getAllTags(currentFilePath!);
      _populateControllers();

      setState(() {
        _isEditing = false;
      });

      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text('Tags saved successfully!')));
    } catch (e) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text('Error saving tags: $e')));
    }
  }

  Future<void> _editArtwork() async {
    FilePickerResult? result = await FilePicker.platform.pickFiles(
      allowMultiple: false,
      type: FileType.image,
    );

    if (result != null && currentFilePath != null) {
      try {
        File imageFile = File(result.files.single.path!);
        Uint8List imageData = await imageFile.readAsBytes();

        await flutterAudioTagger.setArtWork(imageData, currentFilePath!);

        // Refresh tags to get updated artwork
        tag = await flutterAudioTagger.getAllTags(currentFilePath!);
        setState(() {});

        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Artwork updated successfully!')),
        );
      } catch (e) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('Error updating artwork: $e')));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("AudioTagger example"),
        actions: [
          if (tag != null)
            IconButton(
              icon: Icon(_isEditing ? Icons.close : Icons.edit),
              onPressed: () {
                setState(() {
                  _isEditing = !_isEditing;
                  if (_isEditing) {
                    _populateControllers();
                  }
                });
              },
            ),
        ],
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Row(
              children: [
                Expanded(
                  child: TextButton(
                    onPressed: () async {
                      FilePickerResult? result = await FilePicker.platform
                          .pickFiles(
                            allowMultiple: false,
                            type: FileType.custom,
                            allowedExtensions: ['mp3'],
                          );

                      if (result != null) {
                        currentFilePath = result.files.single.path!;
                        tag = await flutterAudioTagger.getAllTags(
                          currentFilePath!,
                        );
                        _populateControllers();
                        setState(() {
                          _isEditing = false;
                        });
                      }
                    },
                    child: Text("Pick Music"),
                  ),
                ),
                if (_isEditing) ...[
                  SizedBox(width: 8),
                  ElevatedButton(onPressed: _saveChanges, child: Text("Save")),
                ],
              ],
            ),
          ),
          Expanded(
            child: tag != null
                ? _buildTagCards()
                : Center(child: Text("No music selected")),
          ),
        ],
      ),
    );
  }

  Widget _buildTagCards() {
    return SingleChildScrollView(
      padding: EdgeInsets.all(16),
      child: Column(
        children: [
          _buildArtworkCard(),
          _buildEditableInfoCard("Artist", _artistController),
          _buildEditableInfoCard("Title", _titleController),
          _buildEditableInfoCard("Album", _albumController),
          _buildEditableInfoCard("Year", _yearController),
          _buildEditableInfoCard("Genre", _genreController),
          _buildEditableInfoCard("Language", _languageController),
          _buildEditableInfoCard("Composer", _composerController),
          _buildEditableInfoCard("Country", _countryController),
          _buildEditableInfoCard("Quality", _qualityController),
          _buildEditableLyricsCard(), 
        ],
      ),
    );
  }

  Widget _buildArtworkCard() {
    return Card(
      margin: EdgeInsets.only(bottom: 16),
      child: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  "Artwork",
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
                if (_isEditing)
                  TextButton.icon(
                    onPressed: _editArtwork,
                    icon: Icon(Icons.edit),
                    label: Text("Edit"),
                  ),
              ],
            ),
            SizedBox(height: 8),
            Center(
              child: Container(
                width: 200,
                height: 200,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(8),
                  color: Colors.grey[300],
                  image: tag?.artwork != null
                      ? DecorationImage(
                          image: MemoryImage(tag!.artwork!),
                          fit: BoxFit.cover,
                        )
                      : null,
                ),
                child: tag?.artwork == null
                    ? Icon(Icons.music_note, size: 80, color: Colors.grey[600])
                    : null,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildEditableInfoCard(
    String title,
    TextEditingController controller,
  ) {
    return Card(
      margin: EdgeInsets.only(bottom: 8),
      child: ListTile(
        title: Text(title, style: TextStyle(fontWeight: FontWeight.bold)),
        subtitle: _isEditing
            ? TextField(
                controller: controller,
                decoration: InputDecoration(
                  hintText: "Enter $title",
                  border: UnderlineInputBorder(),
                ),
              )
            : Text(
                controller.text.isEmpty ? "Not set" : controller.text,
                style: TextStyle(
                  fontSize: 16,
                  color: controller.text.isEmpty ? Colors.grey : null,
                ),
              ),
      ),
    );
  }

  // Add this new method for lyrics (multiline text field)
  Widget _buildEditableLyricsCard() {
    return Card(
      margin: EdgeInsets.only(bottom: 8),
      child: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              "Lyrics",
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 8),
            _isEditing
                ? TextField(
                    controller: _lyricsController,
                    maxLines: 6,
                    decoration: InputDecoration(
                      hintText: "Enter lyrics",
                      border: OutlineInputBorder(),
                    ),
                  )
                : Container(
                    width: double.infinity,
                    padding: EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      border: Border.all(color: Colors.grey[300]!),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Text(
                      _lyricsController.text.isEmpty
                          ? "No lyrics available"
                          : _lyricsController.text,
                      style: TextStyle(
                        fontSize: 14,
                        color: _lyricsController.text.isEmpty
                            ? Colors.grey
                            : null,
                      ),
                    ),
                  ),
          ],
        ),
      ),
    );
  }
}
