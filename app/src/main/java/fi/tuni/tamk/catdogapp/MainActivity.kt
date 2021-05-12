package fi.tuni.tamk.catdogapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

// TODO: Change button labels etc. to match current config where applicable
//       Add a third config for no preference between cats/dogs. (Shows both animals)
//       Add ability to delete favorites
//       Add ability to undo add favorite (from snackbar)
//       Add an overlaying imageView when any image clicked (able to (un)favorite / get new img in it)
//       Refactor to use fragments instead of separate activities?
//       Make the UI cooler
data class AppConfig (
    val type: String,
    val theme: Int,
    val appHeader: String,
    val apiUrl: String,
    val apikey: String,
)

class MainActivity : AppCompatActivity() {
    private val catConfig = AppConfig(
        "Cat",
        R.style.Theme_CatDogApp,
        "The Cat App",
        "https://api.thecatapi.com/v1/",
        BuildConfig.CATAPI_KEY
    )

    private val dogConfig = AppConfig(
        "Dog",
        R.style.Theme_DogCatApp,
        "The Dog App",
        "https://api.thedogapi.com/v1/",
        BuildConfig.DOGAPI_KEY
    )

    lateinit var parent : ConstraintLayout

    lateinit var appHeader : TextView
    lateinit var config : AppConfig
    lateinit var currentImage : ImageData

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState?.getInt("theme") != 0 && savedInstanceState != null) {
            setTheme(savedInstanceState.getInt("theme"))
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        parent = findViewById(R.id.parent)

        val configType = savedInstanceState?.getString("config") ?: "Cat"
        config = when(configType) {
            "Dog" -> dogConfig
            "Cat" -> catConfig
            else -> {
                catConfig
            }
        }
        appHeader = findViewById(R.id.appHeader)
        appHeader.text = config.appHeader

        val imageView: ImageView = findViewById(R.id.imageView)
        switchImage(config, imageView)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("theme", config.theme)
        outState.putString("config", config.type)
    }

    fun onSwapButtonClicked(v: View) {
        config = when(config.type) {
            "Cat" -> dogConfig
            "Dog" -> catConfig
            else -> {
                catConfig
            }
        }
        this.recreate()
    }

    fun onNewImageClicked(v: View) {
        switchImage(config, findViewById(R.id.imageView))
    }

    fun onFavoriteClicked(v: View) {
        config.apiUrl.addFavorite(config.apikey, currentImage.id!!, "test") {
            val snackBarText = when(it) {
                "200" -> "Added to favorites successfully"
                else -> {
                    "ERROR: Could not add to favorites."
                }
            }
            Snackbar.make(parent, snackBarText, Snackbar.LENGTH_SHORT)
                .setAction("Undo") {
                    // TODO: Add UNDO FUNCTIONALITY!!!
                    Log.d("UNDO", "Trying to undo favorite.")
                }
                .show()
        }
    }

    fun onShowFavoritesClicked(v: View) {
        val intent = Intent(this, FavoritesActivity::class.java).apply {
            putExtra("type", config.type)
            putExtra("theme", config.theme)
            putExtra("appHeader", config.appHeader)
            putExtra("apiUrl", config.apiUrl)
            putExtra("apikey", config.apikey)
        }
        startActivity(intent)
    }

    private fun switchImage(config: AppConfig, view: ImageView) {
        config.apiUrl.fetchOne(config.apikey) {
            // Log.d("Result", it)
            val result = parseImageData(it)
            runOnUiThread() {
                currentImage = result[0]
                Picasso
                    .get()
                    .load(result[0].url)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .fit()
                    .centerCrop()
                    .into(view)
            }
        }
    }
}