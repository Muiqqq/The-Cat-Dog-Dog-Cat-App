package fi.tuni.tamk.catdogapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import fi.tuni.tamk.catdogapp.data.ImageData
import fi.tuni.tamk.catdogapp.utils.addFavorite
import fi.tuni.tamk.catdogapp.utils.animateAndExecuteCallbackOnAnimationEnd
import fi.tuni.tamk.catdogapp.utils.fetchOne
import fi.tuni.tamk.catdogapp.utils.parseImageData
import java.util.*

/**
 * The main activity for CatDogApp. Shows a random image of either a cat or a dog, depending
 * which mode the app is in. Modes are: PREFLESS, CAT, DOG. Check AppConfig doc for more detail.
 * The mode can be switched with a button, which then shows a dialog in which the user can choose
 * between the modes. User can add the currently viewed image as a favorite, and there is a button
 * to change the activity to another where the user can see their favorites. New image can be
 * loaded by swiping the current image either left or right.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Helper class, holds information about the image that's currently on screen.
     */
    data class CurrentImg(
        var image : ImageData? = null,
        var apiUrl : String? = null,
    )

    lateinit var prefs : SharedPreferences

    lateinit var parent : ConstraintLayout

    lateinit var appHeader : TextView
    var currentImage : CurrentImg = CurrentImg()

    lateinit var translateLeftAnim : Animation
    lateinit var translateRightAnim : Animation

    lateinit var swapModeDialog : SwapModeDialog

    val config = AppConfig()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState?.getInt("theme") != 0 && savedInstanceState != null) {
            setTheme(savedInstanceState.getInt("theme"))
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("fi.tuni.tamk.catdogapp", MODE_PRIVATE)

        parent = findViewById(R.id.parent)

        when(savedInstanceState?.getString("config")) {
            "Dog" -> config.changeAppState(AppConfig.DOG)
            "Cat" -> config.changeAppState(AppConfig.CAT)
            else -> {
                config.changeAppState(AppConfig.PREFLESS)
            }
        }

        appHeader = findViewById(R.id.appHeader)
        appHeader.text = config.appHeader

        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_img_left)
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_img_right)

        val imageView: ImageView = findViewById(R.id.imageView)
        switchImage(config, imageView)

        // Make the image swipeable, play an animation and then switch the image.
        // imageView is temporarily set invisible, so that the previous image doesn't momentarily
        // show up after the animation ends.
        imageView.setOnTouchListener(object : OnSwipeTouchListener(this@MainActivity) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                onSwipe(imageView, translateLeftAnim)
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                onSwipe(imageView, translateRightAnim)
            }

            // Helper func so same code doesn't need to be repeated
            fun onSwipe(imgView: ImageView, anim: Animation) {
                animateAndExecuteCallbackOnAnimationEnd(imgView, anim) {
                    imgView.visibility = View.INVISIBLE
                    switchImage(config, imgView)
                }
            }
        })

        swapModeDialog = SwapModeDialog(this)

        // Show the swapModeDialog right at app launch if launching for the first time
        // Add a UUID to prefs, used as sub_id in some of the network requests.
        if (prefs.getBoolean("first_run", true)) {
            swapModeDialog.show()

            val uuid = UUID.randomUUID().toString()
            prefs.edit().putString("uuid", uuid).apply()
            prefs.edit().putBoolean("first_run", false).apply()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("theme", config.theme)
        outState.putString("config", config.type)
    }

    /**
     * The swap button opens the swapModeDialog.
     * @see fi.tuni.tamk.catdogapp.SwapModeDialog
     */
    fun onSwapButtonClicked(v: View) {
        swapModeDialog.show()
    }

    /**
     * Add to favorites button adds the image to favorites
     */
    fun onFavoriteClicked(v: View) {
        val url = currentImage.apiUrl
        // Log.d("TAG", config.apiUrl)
        url?.addFavorite(config.apikey, currentImage.image?.id!!, prefs.getString("uuid", "test")!!) {
            val snackBarText = when(it) {
                "200" -> "Added to favorites successfully"
                else -> {
                    "ERROR: Could not add to favorites."
                }
            }
            Snackbar.make(parent, snackBarText, Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Show Favorites button opens the favorites activity, where user can view their favorites.
     */
    fun onShowFavoritesClicked(v: View) {
        val intent = Intent(this, FavoritesActivity::class.java).apply {
            putExtra("appState", config.appState)
            // Log.d("AppState", "${config.appState}")
        }
        startActivity(intent)
    }

    /**
     * Function to switch the image showing on the screen. Fetches a new one and loads it in using
     * Picasso.
     */
    private fun switchImage(config: AppConfig, view: ImageView) {
        val url = config.apiUrl
        url.fetchOne(config.apikey) {
            // Log.d("Result", it)
            val result = parseImageData(it)
            runOnUiThread() {
                currentImage.image = result[0]
                currentImage.apiUrl = url
                Picasso
                    .get()
                    .load(result[0].url)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .fit()
                    .centerCrop()
                    .into(view)
                view.visibility = View.VISIBLE
            }
        }
    }
}