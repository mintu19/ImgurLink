package com.akshit.imgurlink.apiHelpers.apis

import com.akshit.imgurlink.apiHelpers.*
import com.akshit.imgurlink.apiHelpers.models.GallerySearchResult
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

enum class GallerySearchSort(val value: String) {
    time("time"), viral("viral"), top("top")
}

internal class GalleryApi : ApiClient() {

     suspend fun searchGallery(query: String, page: Int, sort: GallerySearchSort? = null): Success<GallerySearchResult?> {
        val path = if (sort != null) "3/gallery/search/${sort.value}" else "3/gallery/search/"
        val uri = basePath.toHttpUrl().newBuilder().addPathSegments(path).addQueryParameter("q", query).build()
        val request = Request.Builder().url(uri).build()
        val response = exec<GallerySearchResult>(request)

        return when (response.responseType) {
            ResponseType.Success -> response as Success
            ResponseType.ClientError -> {
                val localVarError = response as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, response)
            }
            ResponseType.ServerError -> {
                val localVarError = response as ClientError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, response)
            }

            else -> throw UnsupportedOperationException("Client does not support Informational responses.")
        }
    }
}