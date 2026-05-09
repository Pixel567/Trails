package com.poznantrails.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

data class WikipediaSummary(
    @SerializedName("extract") val extract: String = "",
    @SerializedName("thumbnail") val thumbnail: WikipediaImage? = null,
    @SerializedName("originalimage") val originalImage: WikipediaImage? = null
)

data class WikipediaMediaList(
    @SerializedName("items") val items: List<WikipediaMediaItem>? = null
)

data class WikipediaMediaItem(
    @SerializedName("type") val type: String,
    @SerializedName("srcset") val srcset: List<WikipediaSrcset>? = null
)

data class WikipediaSrcset(
    @SerializedName("src") val src: String
)

data class WikipediaImage(
    @SerializedName("source") val source: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int
)

data class WikipediaResult(
    val description: String,
    val imageUrl: String?
)

class WikipediaRepository {

    private val client = OkHttpClient()
    private val gson = Gson()
    private val cache = mutableMapOf<String, WikipediaResult>()
    private val userAgent = "PoznanTrails/1.0"

    suspend fun fetchDescription(wikipediaTitle: String): Result<WikipediaResult> =
        withContext(Dispatchers.IO) {
            cache[wikipediaTitle]?.let { return@withContext Result.success(it) }

            try {
                val encoded = URLEncoder.encode(wikipediaTitle, "UTF-8")
                
                val summaryUrl = "https://pl.wikipedia.org/api/rest_v1/page/summary/$encoded"
                val summaryRequest = Request.Builder()
                    .url(summaryUrl)
                    .header("User-Agent", userAgent)
                    .build()

                val summaryResponse = client.newCall(summaryRequest).execute()
                if (!summaryResponse.isSuccessful) {
                    return@withContext Result.failure(Exception("Błąd HTTP summary: ${summaryResponse.code}"))
                }

                val summaryBody = summaryResponse.body?.string() ?: ""
                val summary = gson.fromJson(summaryBody, WikipediaSummary::class.java)
                
                var imageUrl = summary.originalImage?.source ?: summary.thumbnail?.source

                if (imageUrl == null) {
                    val mediaUrl = "https://pl.wikipedia.org/api/rest_v1/page/media-list/$encoded"
                    val mediaRequest = Request.Builder()
                        .url(mediaUrl)
                        .header("User-Agent", userAgent)
                        .build()

                    val mediaResponse = client.newCall(mediaRequest).execute()
                    if (mediaResponse.isSuccessful) {
                        val mediaBody = mediaResponse.body?.string() ?: ""
                        val mediaList = gson.fromJson(mediaBody, WikipediaMediaList::class.java)
                        
                        imageUrl = mediaList.items?.firstOrNull { it.type == "image" }
                            ?.srcset?.lastOrNull()?.src
                            ?.let { src ->
                                if (src.startsWith("//")) "https:$src" else src
                            }
                    }
                }

                val text = summary.extract.ifBlank { "Brak opisu w Wikipedii." }
                val result = WikipediaResult(text, imageUrl)

                cache[wikipediaTitle] = result
                Result.success(result)
            } catch (e: Exception) {
                Result.failure(Exception("Brak dostępu do internetu"))
            }
        }
}
