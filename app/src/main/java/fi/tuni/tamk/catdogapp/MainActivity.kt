package fi.tuni.tamk.catdogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlin.reflect.typeOf

data class AppConfig (
    val type: String,
    val theme: Int,
    val appHeader: String,
    )

class MainActivity : AppCompatActivity() {
    private val catConfig = AppConfig("Cat", R.style.Theme_CatDogApp, "The Cat/Dog App")
    private val dogConfig = AppConfig("Dog", R.style.Theme_DogCatApp, "The Dog/Cat App")
    lateinit var appHeader : TextView
    lateinit var config : AppConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState?.getInt("theme") != 0 && savedInstanceState != null) {
            setTheme(savedInstanceState.getInt("theme"))
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("theme", config.theme)
        outState.putString("config", config.type)
    }

    fun onSwapButtonClicked(v: View) {
        Log.d("tag", "swap clicked")
        config = when(config.type) {
            "Cat" -> dogConfig
            "Dog" -> catConfig
            else -> {
                catConfig
            }
        }
        this.recreate()
    }
}