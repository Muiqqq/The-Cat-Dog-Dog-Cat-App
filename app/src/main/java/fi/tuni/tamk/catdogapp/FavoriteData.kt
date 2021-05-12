package fi.tuni.tamk.catdogapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class FavoriteData {
    val image : ImageData? = null
}