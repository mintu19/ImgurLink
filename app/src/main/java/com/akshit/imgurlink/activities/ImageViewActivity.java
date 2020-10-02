package com.akshit.imgurlink.activities;

import android.os.AsyncTask;
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
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akshit.imgurlink.R;
import com.akshit.imgurlink.adapters.CommentsAdapter;
import com.akshit.imgurlink.apiHelpers.models.Image;
import com.akshit.imgurlink.models.Comment;
import com.akshit.imgurlink.models.CommentViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageViewActivity extends AppCompatActivity {

    static final String EXTRA_DATA = "data";

    private Image image;

    private RecyclerView commentsView;
    private AppCompatMultiAutoCompleteTextView commentEditText;

    private CommentViewModel commentViewModel;
    private List<Comment> comments = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        setSupportActionBar(findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ImageView imageView = findViewById(R.id.imageView);
        VideoView videoView = findViewById(R.id.videoView);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        commentsView = findViewById(R.id.comments);
        commentEditText = findViewById(R.id.commentBox);

        image = (Image) getIntent().getSerializableExtra(EXTRA_DATA);

        if (image == null) {
            onBackPressed();
        }

        setListView();

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
                // Todo: use String from resource
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

        commentViewModel = new ViewModelProvider(this).get(CommentViewModel.class);
        commentViewModel.getCommentsListLiveData(image.getId()).observe(this, comments -> {
            // Todo: Add inserts only
            this.comments.clear();
            this.comments.addAll(comments);
            commentsView.getAdapter().notifyDataSetChanged();
        });
    }

    public void sendComment(View view) {
        String text = commentEditText.getText().toString();
        if (text.isEmpty()) {
            return;
        }

        commentEditText.setText("");

        AsyncTask.execute(() -> {
            commentViewModel.addComment(image.getId(), text);
        });

    }

    void setListView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        commentsView.setLayoutManager(layoutManager);
        CommentsAdapter adapter = new CommentsAdapter(comments);
        commentsView.setAdapter(adapter);
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
