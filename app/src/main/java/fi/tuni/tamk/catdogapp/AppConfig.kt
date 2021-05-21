package fi.tuni.tamk.catdogapp

import kotlin.random.Random

/**
 * Class to handle the different modes this app can be in (Preferenceless mode or PREFLESS for short,
 * cat mode, dog mode)
 * All this class does, is return the correct information (theme, url, apikey, appHeader) depending
 * on the mode the app is in.
 * In PREFLESS mode, the app can show images of both cats and dogs in the main and favorites view.
 * In DOG mode, the app shows dogs.
 * In CAT mode, the app shows cats.
 * Defaults to PREFLESS mode.
 */
class AppConfig() {
    var appState = PREFLESS
    val apiUrls = ApiUrls()
    val apiKeys = ApiKeys()

    data class ApiUrls(
        val cat : String = "https://api.thecatapi.com/v1/",
        val dog : String = "https://api.thedogapi.com/v1/",
    )

    data class ApiKeys(
        val cat : String = BuildConfig.CATAPI_KEY,
        val dog : String = BuildConfig.DOGAPI_KEY,
    )

    companion object States {
        const val PREFLESS = 0
        const val CAT = 1
        const val DOG = 2
    }

    val type : String
        get() {
            return when (appState) {
                PREFLESS -> "Prefless"
                CAT -> "Cat"
                DOG -> "Dog"
                else -> {
                    return "ERROR"
                }
            }
        }

    val theme : Int
        get() {
            return when (appState) {
                PREFLESS -> R.style.Theme_NoPrefApp
                CAT -> R.style.Theme_CatDogApp
                DOG -> R.style.Theme_DogCatApp
                else -> {
                    return R.style.Theme_NoPrefApp
                }
            }
        }

    val appHeader : String
        get() {
            return when (appState) {
                PREFLESS -> { return if (Random.nextBoolean()) "The Cat & Dog App" else "The Dog & Cat App" }
                CAT -> "The Cat App"
                DOG -> "The Dog App"
                else -> {
                    return "The NULL App (If you see this, send the dev an angry message)"
                }
            }
        }

    val apiUrl : String
        get() {
            return when (appState) {
                PREFLESS -> {
                    return if (Random.nextBoolean()) {
                        apikey = apiKeys.cat
                        apiUrls.cat
                    } else {
                        apikey = apiKeys.dog
                        apiUrls.dog
                    }
                }
                CAT -> apiUrls.cat
                DOG -> apiUrls.dog
                else -> {
                    return "The NULL App (If you see this, send the dev an angry message)"
                }
            }
        }

    var apikey : String = ""
        get() {
            return when(appState) {
                CAT -> BuildConfig.CATAPI_KEY
                DOG -> BuildConfig.DOGAPI_KEY
                else -> {
                    return field
                }
            }
        }

    fun changeAppState(state: Int) {
        this.appState = state
    }
}