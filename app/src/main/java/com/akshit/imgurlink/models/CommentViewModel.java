package com.akshit.imgurlink.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.akshit.imgurlink.helpers.AppDatabase;

import java.util.List;

public class CommentViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    private LiveData<List<Comment>> commentsListLiveData;

    public CommentViewModel(@NonNull Application application) {
        super(application);

        appDatabase = AppDatabase.getInstance(application);
    }

    public LiveData<List<Comment>> getCommentsListLiveData(String imageId) {
        if (commentsListLiveData == null) {
            commentsListLiveData = appDatabase.commentDao().getByImageId(imageId);
        }
        return commentsListLiveData;
    }

    public void addComment(String imageId, String commentString) {
        Comment comment = new Comment(imageId, commentString);
        appDatabase.commentDao().addComment(comment);
    }
}
