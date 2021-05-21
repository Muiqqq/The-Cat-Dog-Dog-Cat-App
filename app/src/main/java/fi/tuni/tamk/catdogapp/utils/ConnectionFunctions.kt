package fi.tuni.tamk.catdogapp.utils

import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fi.tuni.tamk.catdogapp.data.FavoriteData
import fi.tuni.tamk.catdogapp.data.ImageData
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import org.apache.commons.io.IOUtils
import kotlin.concurrent.thread

/**
 * Fetches one image from one of the APIs. No need to give the request any parameters,
 * the APIs used default to 1 image.
 *
 * @param apikey Apikey for whichever API url the function was called on.
 * @param cb Callback function to be called after request, request response is given as parameter.
 */
fun String.fetchOne(apikey: String, cb: (String) -> Unit) {
    thread {
        val baseUrl = this + "images/search"
        val url = URL(baseUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("x-api-key", apikey)

        var result = ""
        connection.inputStream.use {
            result = IOUtils.toString(it, StandardCharsets.UTF_8)
        }
        cb(result)
    }
}

/**
 * Sends a request to one of the APIs to add an image as a favorite. In the APIs used, the favorites
 * are stored on a per app basis, based on the apikey. Users are differentiated with sub ID's, which
 * are given by the app in question.
 *
 * @param apikey Apikey for whichever API url the function was called on.
 * @param imageId ID of the image to be added as a favorite
 * @param subId Some ID of the application user, in this app's case, it's an UUID
 * @param cb Callback function to be called after request, response code given as parameter.
 */
fun String.addFavorite(apikey: String, imageId: String, subId: String, cb: (String) -> Unit) {
    thread {
        val jsonString = "{\"image_id\": \"${imageId}\", \"sub_id\": \"${subId}\"}"
        val url = URL(this + "favourites/")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("x-api-key", apikey)
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        connection.outputStream.write(jsonString.toByteArray())
        connection.outputStream.flush()
        connection.outputStream.close()

        var result = ""
        if(connection.responseCode == 200) {
            connection.inputStream.use {
                result = IOUtils.toString(it, StandardCharsets.UTF_8)
                Log.d("SUCCESS", result)
            }
        } else {
            connection.errorStream.use {
                result = IOUtils.toString(it, StandardCharsets.UTF_8)
                Log.d("ERROR", result)
            }
        }

        cb("${connection.responseCode}")
    }
}

/**
 * Fetches the favorites for a specific sub ID. In the APIs used, the favorites
 * are stored on a per app basis, based on the apikey. Users are differentiated with sub ID's, which
 * are given by the app in question.
 *
 * @param apikey Apikey for whichever API url the function was called on.
 * @param subId Some ID of the application user, in this app's case, it's an UUID
 * @param cb Callback function to be called after request, request response given as parameter.
 */
fun String.fetchFavorites(apikey: String, subId: String, cb: (String) -> Unit) {
    thread {
        val baseUrl = this + "favourites"
        val args = "?sub_id=$subId&limit=100&page=0"
        val url = URL(baseUrl + args)
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("x-api-key", apikey)

        var result = ""
        if (connection.responseCode == 200) {
            connection.inputStream.use {
                result = IOUtils.toString(it, StandardCharsets.UTF_8)
            }
        } else {
            connection.errorStream.use {
                result = IOUtils.toString(it, StandardCharsets.UTF_8)
            }
        }
        cb(result)
    }
}

/**
 * Sends a request to delete an image from favorites.
 *
 * @param apikey Apikey for whichever API url the function was called on.
 * @param id ID of the favorited image. NOT the same as an image's ID!
 * @param cb Callback function to be called after request, response code given as parameter.
 */
fun String.deleteFavorite(apikey: String, id: Int, cb: (String) -> Unit) {
    thread {
        val baseUrl = this + "favourites/$id"
        val url = URL(baseUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "DELETE"
        connection.setRequestProperty("x-api-key", apikey)
        connection.setRequestProperty("Content-Type", "application/json")

        var result = ""
        if (connection.responseCode == 200) {
            connection.inputStream.use {
                result = IOUtils.toString(it, StandardCharsets.UTF_8)
                Log.d("SUCCESS:", result)
            }
        } else {
            connection.errorStream.use {
                result = IOUtils.toString(it, StandardCharsets.UTF_8)
                Log.d("ERROR:", result)
            }
        }
        cb("${connection.responseCode}")
    }
}

/**
 * Parses a given jsonString into a MutableList<ImageData>
 *
 * @param jsonString String to be parsed.
 */
fun parseImageData(jsonString: String) : MutableList<ImageData> {
    val objectMapper = jacksonObjectMapper()
    return objectMapper.readValue(jsonString)
}

/**
 * Parses a given jsonString into a MutableList<FavoriteData>
 *
 * @param jsonString String to be parsed.
 */
fun parseFavoriteData(jsonString: String) : MutableList<FavoriteData> {
    val objectMapper = jacksonObjectMapper()
    return objectMapper.readValue(jsonString)
}
