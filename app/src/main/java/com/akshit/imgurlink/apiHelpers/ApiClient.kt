package com.akshit.imgurlink.apiHelpers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.Response
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// In production read all defaulted value here from secured file or safe place
internal open class ApiClient(
    val basePath: String = BASE_PATH,
    val apiKey: String = API_KEY,
    val apiValue: String = API_VALUE
) {
    companion object {
        const val ContentType = "Content-Type"
        const val JsonMediaType = "application/json"

        const val BASE_PATH = "https://api.imgur.com"
        const val API_KEY = "Authorization"

        private const val API_VALUE = "Client-ID 137cda6b5008a7c"

        @JvmStatic
        val client: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()
        }
    }

    @Throws(UnsupportedOperationException::class)
    suspend inline fun <reified T: Any?> exec(req: Request) = withContext(Dispatchers.IO) {
        suspendCoroutine<ApiResponse<T?>> {
            val request = req.newBuilder().addHeader(apiKey, apiValue).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val accept = response.header(ContentType)?.substringBefore(";")
                        ?.toLowerCase(Locale.getDefault())
                    val r: ApiResponse<T?> = when {
                        response.isSuccessful -> Success(
                            responseBody(response.body, accept),
                            response.code,
                            response.headers.toMultimap()
                        )
                        response.isClientError -> ClientError(
                            response.message,
                            response.body?.string(),
                            response.code,
                            response.headers.toMultimap()
                        )
                        else -> ServerError(
                            response.message,
                            response.body?.string(),
                            response.code,
                            response.headers.toMultimap()
                        )
                    }
                    it.resume(r)
                }

            })
        }
    }

    inline fun <reified T: Any?> execSync(req: Request) : ApiResponse<T?> {
        val request = req.newBuilder().addHeader(apiKey, apiValue).build()
        val response = client.newCall(request).execute()
        val accept = response.header(ContentType)?.substringBefore(";")?.toLowerCase(Locale.getDefault())
        return when {
            response.isSuccessful -> Success(
                responseBody(response.body, accept),
                response.code,
                response.headers.toMultimap()
            )
            response.isClientError -> ClientError(
                response.message,
                response.body?.string(),
                response.code,
                response.headers.toMultimap()
            )
            else -> ServerError(
                response.message,
                response.body?.string(),
                response.code,
                response.headers.toMultimap()
            )
        }
    }

    @Throws(UnsupportedOperationException::class)
    inline fun <reified T: Any?> responseBody(body: ResponseBody?, mediaType: String? = JsonMediaType): T? {
        if(body == null) {
            return null
        }
        val bodyContent = body.string()
        if (bodyContent.isEmpty()) {
            return null
        }
        return when(mediaType) {
            JsonMediaType -> Serializer.moshi.adapter(T::class.java).fromJson(bodyContent)
            else ->  throw UnsupportedOperationException("responseBody currently only supports JSON body.")
        }
    }
}