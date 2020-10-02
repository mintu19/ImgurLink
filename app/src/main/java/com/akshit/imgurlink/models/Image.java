package com.akshit.imgurlink.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;

@Entity
public class Image {

    // not saving other info for demo
    @PrimaryKey
    public String imageId;

    @ColumnInfo(name = "link")
    public String link;

    @ColumnInfo(name = "created")
    public Date created;
}
