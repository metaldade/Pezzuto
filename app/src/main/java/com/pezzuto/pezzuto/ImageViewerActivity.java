package com.pezzuto.pezzuto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageViewerActivity extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        Intent intent = getIntent();
        String image = intent.getStringExtra("image");
        getSupportActionBar().hide();
        imageView = (ImageView) findViewById(R.id.image);
        Statics.loadImage(this,image,imageView);
    }
}
