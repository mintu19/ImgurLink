package com.akshit.imgurlink.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.akshit.imgurlink.R;
import com.akshit.imgurlink.apiHelpers.models.Image;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {

    static final String EXTRA_DATA = "data";

    private Image image;

    private ImageView imageView;
    private VideoView videoView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        setSupportActionBar(findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        imageView = findViewById(R.id.imageView);
        videoView = findViewById(R.id.videoView);
        progressBar = findViewById(R.id.progressBar);

        image = (Image) getIntent().getSerializableExtra(EXTRA_DATA);

        if (image == null) {
            onBackPressed();
        }

        String title = image.getTitle() != null ? image.getTitle() : image.getDescription();
        if (title != null) {
            this.setTitle(title);
        }

        if (image.getAnimated()) {
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);

            videoView.setVideoPath(image.getLink());
            videoView.start();
            videoView.setOnPreparedListener(mp -> progressBar.setVisibility(View.GONE));
            videoView.setOnErrorListener((mp, what, extra) -> {
                Snackbar.make(imageView, "Error Loading Image!!!", Snackbar.LENGTH_SHORT).show();
                return true;
            });
        } else {
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);

            // Todo: Add placeholder image
            Picasso.get().load(image.getLink()).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("ImageView", "Error Loading Image", e);
                    Snackbar.make(imageView, "Error Loading Image!!!", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                // Because direct handling back and not here will make previous activity empty because it will navigate there
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
