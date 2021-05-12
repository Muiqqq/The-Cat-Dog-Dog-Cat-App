package fi.tuni.tamk.catdogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritesActivity : AppCompatActivity() {
    lateinit var config : AppConfig
    lateinit var favoritesRecView : RecyclerView
    lateinit var favHeader : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        config = AppConfig(
            intent.getStringExtra("type")!!,
            intent.getIntExtra("theme", -1),
            intent.getStringExtra("appHeader")!!,
            intent.getStringExtra("apiUrl")!!,
            intent.getStringExtra("apikey")!!
        )
        setTheme(config.theme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        favoritesRecView = findViewById(R.id.favoritesRecView)
        favHeader = findViewById(R.id.favHeader)

        val temp = "Your Favorite ${config.type} Pictures!"
        favHeader.text = temp

        val adapter = FavoritesRecViewAdapter()

        config.apiUrl.fetchFavorites(config.apikey, "test") { result ->
            runOnUiThread() {
                adapter.imageData = parseFavoriteData(result).asReversed().map { it.image!! }
            }
        }
        favoritesRecView.adapter = adapter
        favoritesRecView.layoutManager = GridLayoutManager(this, 2)
    }
}