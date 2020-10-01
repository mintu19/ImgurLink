package com.akshit.imgurlink.apiHelpers.models

data class GallerySearchResult(
    val data: List<GalleryResult>,
    val success: Boolean,
    val status: Int
)