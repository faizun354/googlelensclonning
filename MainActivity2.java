package com.example.googlelensapplication;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class MainActivity2 extends AppCompatActivity {

    ImageView clear, getImage, copy;
    EditText recgText;
    Uri imageUri;
    TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        clear = findViewById(R.id.clear);
        getImage = findViewById(R.id.getimage);
        copy = findViewById(R.id.copy);
        recgText = findViewById(R.id.recgText);

        // Initialize TextRecognizer with default options
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        getImage.setOnClickListener(view -> {
            ImagePicker.with(MainActivity2.this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start();
        });

        copy.setOnClickListener(view -> {
            String text = recgText.getText().toString();
            if (text.isEmpty()) {
                Toast.makeText(MainActivity2.this, "There is no text to copy", Toast.LENGTH_SHORT).show();
            } else {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Data", recgText.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(MainActivity2.this, "Text copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        clear.setOnClickListener(view -> {
            String text = recgText.getText().toString();
            if (text.isEmpty()) {
                Toast.makeText(MainActivity2.this, "There is no text to clear", Toast.LENGTH_SHORT).show();
            } else {
                recgText.setText("");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            recognizetext();
        } else {
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void recognizetext() {
        if (imageUri != null) {
            try {
                InputImage inputImage = InputImage.fromFilePath(MainActivity2.this, imageUri);

                textRecognizer.process(inputImage)
                        .addOnSuccessListener(text -> recgText.setText(text.getText()))
                        .addOnFailureListener(e -> Toast.makeText(MainActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
