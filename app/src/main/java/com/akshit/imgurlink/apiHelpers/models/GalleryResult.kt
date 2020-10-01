package com.akshit.imgurlink.apiHelpers.models

data class GalleryResult(
    val id: String,
    val title: String,
    val description: String? = null,
    val datetime: Long,
    val link: String,
    val images: List<Image>? = null
)