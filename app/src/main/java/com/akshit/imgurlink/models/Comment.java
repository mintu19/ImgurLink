package com.akshit.imgurlink.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Comment {

    // not saving other info for demo and not using keys
    @PrimaryKey(autoGenerate = true)
    public Integer cid;

    @ColumnInfo(name = "image_id")
    public String imageId;

    @ColumnInfo(name = "comment")
    public String comment;

    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    public Date created;

    @Ignore
    public Comment(String imageId, String comment) {
        this.imageId = imageId;
        this.comment = comment;
        // Todo: use better time getter but fine for local
        this.created = new Date(System.currentTimeMillis());
    }

    public Comment() {}
}
