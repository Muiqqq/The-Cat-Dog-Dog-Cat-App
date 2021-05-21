package fi.tuni.tamk.catdogapp.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class FavoriteData {
    val id : Int? = null
    val image : ImageData? = null
}