package com.akshit.imgurlink.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.akshit.imgurlink.models.Comment;

import java.util.List;

@Dao
public interface CommentDao {

    // get image by imageId
    @Query("select * from comment where image_id=:imageId order by created_at desc")
    LiveData<List<Comment>> getByImageId(String imageId);

    // add comment
    @Insert
    void addComment(Comment comment);
}
