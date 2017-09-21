package com.pezzuto.pezzuto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.jsibbold.zoomage.ZoomageView;

public class ImageViewerActivity extends AppCompatActivity {
    ZoomageView zoomageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        Intent intent = getIntent();
        String image = intent.getStringExtra("image");
        getSupportActionBar().hide();
        zoomageView = (ZoomageView) findViewById(R.id.image);
        Statics.loadImage(this,image,zoomageView);
    }
}
