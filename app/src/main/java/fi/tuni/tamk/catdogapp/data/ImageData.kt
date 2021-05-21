package fi.tuni.tamk.catdogapp.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class ImageData(val id: String? = null, val url: String? = null) {}