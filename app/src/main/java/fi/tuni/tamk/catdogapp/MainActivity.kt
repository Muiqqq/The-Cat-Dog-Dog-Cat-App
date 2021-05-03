package fi.tuni.tamk.catdogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlin.reflect.typeOf

interface AppConfig {
    val type: String
    val theme: Int
    val appHeader: String
}

data class DogConfig(
    override val type: String = "DogConfig",
    override val theme: Int = R.style.Theme_DogCatApp,
    override val appHeader: String = "The Dog/Cat App",
) : AppConfig

data class CatConfig(
    override val type: String = "CatConfig",
    override val theme: Int = R.style.Theme_CatDogApp,
    override val appHeader: String = "The Cat/Dog App",
) : AppConfig

class MainActivity : AppCompatActivity() {
    lateinit var appHeader : TextView
    lateinit var config : AppConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState?.getInt("theme") != 0 && savedInstanceState != null) {
            setTheme(savedInstanceState.getInt("theme"))
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val configType = savedInstanceState?.getString("config") ?: "CatConfig"
        config = when(configType) {
            "DogConfig" -> DogConfig()
            "CatConfig" -> CatConfig()
            else -> {
                CatConfig()
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

    fun swapClicked(v: View) {
        Log.d("tag", "swap clicked")
        when(config) {
            is CatConfig -> config = DogConfig()
            is DogConfig -> config = CatConfig()
        }
        this.recreate()
    }
}