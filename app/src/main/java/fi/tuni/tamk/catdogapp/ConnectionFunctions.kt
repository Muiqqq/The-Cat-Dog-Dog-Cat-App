package fi.tuni.tamk.catdogapp

import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import org.apache.commons.io.IOUtils
import kotlin.concurrent.thread

fun String.fetchOne(apikey: String, cb: (String) -> Unit) {
    thread {
        val baseUrl = this + "images/search"
        val args = "?limit=1"
        val url = URL(baseUrl + args)
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("x-api-key", apikey)

        var result = ""
        connection.inputStream.use {
            result = IOUtils.toString(it, StandardCharsets.UTF_8)
        }
        cb(result)
    }
}

fun String.fetchMany(baseUrl: String) {

}

fun String.addFavorite(apikey: String, imageId: String, subId: String, cb: (String) -> Unit) {
    thread {
        val jsonString = "{\"image_id\": \"${imageId}\", \"sub_id\": \"${subId}\"}"
        val url = URL(this + "favourites/")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("x-api-key", apikey)
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Accept", "application/json")
        connection.doInput = true
        connection.doOutput = true
        connection.outputStream.write(jsonString.toByteArray())
        connection.outputStream.flush()
        connection.outputStream.close()

        var result = ""
        if(connection.responseCode == 200) {
            connection.inputStream.use {
                result = IOUtils.toString(it, StandardCharsets.UTF_8)
                Log.d("TAG", result)
            }
        } else {
            connection.errorStream.use {
                result = IOUtils.toString(it, StandardCharsets.UTF_8)
                Log.d("TAG", result)
            }
        }

        cb("${connection.responseCode}")
        // Log.d("code", "${connection.responseCode}")
    }
}

fun String.fetchFavorites(apikey: String, subId: String, cb: (String) -> Unit) {
    // Work in progress!!
    thread {
        val baseUrl = this + "favourites"
        val args = "?sub_id=$subId&limit=100&page=0"
        val url = URL(baseUrl + args)
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("x-api-key", apikey)

        var result = ""
        connection.inputStream.use {
            result = IOUtils.toString(it, StandardCharsets.UTF_8)
        }
        cb(result)
    }
}

fun parseImageData(jsonString: String) : List<ImageData> {
    val objectMapper = jacksonObjectMapper()
    return objectMapper.readValue(jsonString)
}

fun parseFavoriteData(jsonString: String) : List<FavoriteData> {
    val objectMapper = jacksonObjectMapper()
    return objectMapper.readValue(jsonString)
}