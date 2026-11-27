
package com.example.amerly.data.remote

import android.text.Html
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object MyMemoryTranslationService {

    suspend fun translate(text: String): String =
        withContext(Dispatchers.IO) {

            require(text.isNotBlank()) {
                "Текст для перевода пустой"
            }

            val encodedText = URLEncoder.encode(
                text,
                "UTF-8"
            )

            val address =
                "https://api.mymemory.translated.net/get" +
                        "?q=$encodedText&langpair=en%7Cru"

            val connection =
                URL(address).openConnection() as HttpURLConnection

            try {

                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode

                if (responseCode !in 200..299) {
                    throw IOException("Ошибка сервера: $responseCode")
                }

                val response = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                val json = JSONObject(response)

                if (json.optInt("responseStatus") != 200) {
                    throw IOException("Перевод не найден")
                }

                val translated = json
                    .getJSONObject("responseData")
                    .optString("translatedText")
                    .trim()

                val result = Html.fromHtml(
                    translated,
                    Html.FROM_HTML_MODE_LEGACY
                ).toString().trim()

                if (result.isBlank()) {
                    throw IOException("Пустой результат перевода")
                }

                val startsWithCapital = text
                    .firstOrNull { it.isLetter() }
                    ?.isUpperCase() == true

                val firstLetterIndex = result.indexOfFirst { it.isLetter() }

                if (startsWithCapital && firstLetterIndex >= 0) {
                    result.replaceRange(
                        firstLetterIndex,
                        firstLetterIndex + 1,
                        result[firstLetterIndex].uppercase()
                    )
                } else {
                    result
                }

            } finally {
                connection.disconnect()
            }
        }
}
