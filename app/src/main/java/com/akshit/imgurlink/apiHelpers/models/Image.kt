package com.akshit.imgurlink.apiHelpers.models

import java.io.Serializable

data class Image(
    val id: String,
    var title: String? = null,
    var description: String? = null,
    val datetime: Long,
    val animated: Boolean,
    val type: String,
    val link: String? = null,
    var thumbLink: String? = null
): Serializable