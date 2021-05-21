package fi.tuni.tamk.catdogapp

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.tuni.tamk.catdogapp.utils.fetchFavorites
import fi.tuni.tamk.catdogapp.utils.mixMutableLists
import fi.tuni.tamk.catdogapp.utils.parseFavoriteData
import fi.tuni.tamk.catdogapp.data.FavoriteData

/**
 * Activity which shows user's favorite images. Which images are shown depends on the mode
 * the app is in. This activity can show either dog or cat pictures, or both at the same time.
 * Each image can be clicked, and opens another view (defined in FavoritesRecViewAdapter) which
 * shows the image in larger form, with a button to delete said image from favorites.
 */
class FavoritesActivity : AppCompatActivity() {
    lateinit var prefs : SharedPreferences
    lateinit var config : AppConfig
    lateinit var favoritesRecView : RecyclerView
    lateinit var favHeader : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        config = AppConfig()
        config.changeAppState(intent.getIntExtra("appState", AppConfig.PREFLESS))

        setTheme(config.theme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        prefs = getSharedPreferences("fi.tuni.tamk.catdogapp", MODE_PRIVATE)

        favoritesRecView = findViewById(R.id.favoritesRecView)

        favHeader = findViewById(R.id.favHeader)

        val animalWord = when(config.type) {
            "Dog" -> "Dog"
            "Cat" -> "Cat"
            "Prefless" -> "Pet"
            else -> {
                ""
            }
        }
        val favHeaderText = "Your Favorite $animalWord Pictures!"
        favHeader.text = favHeaderText

        val adapter = FavoritesRecViewAdapter(this)

        if (config.appState == AppConfig.PREFLESS) {
            fetchBoth() {
                runOnUiThread {
                    adapter.imageData = it
                }
            }
        } else {
            fetchOne(config.apiUrl, config.apikey) {
                runOnUiThread {
                    adapter.imageData = it
                }
            }
        }
        favoritesRecView.adapter = adapter
        favoritesRecView.layoutManager = GridLayoutManager(this, 2)
    }

    /**
     * Helper function, calls url.fetchFavorites for one API.
     *
     * @param apiUrl URL of the api
     * @param apikey Apikey matching the url.
     * @param cb Callback function, defines what to do with the resulting MutableList<FavoriteData>
     */
    private fun fetchOne(apiUrl: String, apikey: String, cb: (MutableList<FavoriteData>) -> Unit) {
        var result: MutableList<FavoriteData>
        apiUrl.fetchFavorites(apikey, prefs.getString("uuid", "test")!!) {
            result = parseFavoriteData(it).asReversed()
            cb(result)
        }
    }

    /**
     * Helper function, calls fetchOne() for both APIs.
     *
     * @param cb Callback function, defines what to do with the resulting data from both APIs.
     */
    private fun fetchBoth(cb: (MutableList<FavoriteData>) -> Unit) {
        fetchOne(config.apiUrls.dog, config.apiKeys.dog) { result ->
            val dogs = result
            fetchOne(config.apiUrls.cat, config.apiKeys.cat) {
                val cats = it
                cb(mixMutableLists(cats, dogs))
            }
        }
    }
}