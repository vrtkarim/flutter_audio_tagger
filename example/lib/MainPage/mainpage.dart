import 'dart:io';

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
  FlutterAudioTagger flutterAudioTagger = FlutterAudioTagger();
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("AudioTagger example")),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: TextButton(
              onPressed: () async {
                FilePickerResult? result = await FilePicker.platform.pickFiles(
                  allowMultiple: false,
                  type: FileType.custom,
                  allowedExtensions: ['mp3'],
                );

                if (result != null) {
                  tag = await flutterAudioTagger.getAllTags(
                    result.files.single.path!,
                  );
                  setState(() {});
                } else {}
              },
              child: Text("Pick Music"),
            ),
          ),
          Expanded(
            child: tag != null ? _buildTagCards() : Center(child: Text("No music selected")),
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
          if (tag!.artwork != null) _buildArtworkCard(),
          _buildInfoCard("Artist", tag!.artist),
          _buildInfoCard("Title", tag!.title),
          _buildInfoCard("Album", tag!.album),
          _buildInfoCard("Year", tag!.year),
          _buildInfoCard("Genre", tag!.genre),
          _buildInfoCard("Language", tag!.language),
          _buildInfoCard("Composer", tag!.composer),
          _buildInfoCard("Country", tag!.country),
          _buildInfoCard("Quality", tag!.quality),
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
            Text(
              "Artwork",
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 8),
            Center(
              child: Container(
                width: 200,
                height: 200,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(8),
                  image: DecorationImage(
                    image: MemoryImage(tag!.artwork!),
                    fit: BoxFit.cover,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoCard(String title, String? value) {
    if (value == null || value.isEmpty) return SizedBox.shrink();
    
    return Card(
      margin: EdgeInsets.only(bottom: 8),
      child: ListTile(
        title: Text(
          title,
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        subtitle: Text(
          value,
          style: TextStyle(fontSize: 16),
        ),
      ),
    );
  }
}
