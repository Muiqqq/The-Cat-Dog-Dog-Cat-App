package fi.tuni.tamk.catdogapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import fi.tuni.tamk.catdogapp.utils.animateAndExecuteCallbackOnAnimationEnd
import fi.tuni.tamk.catdogapp.utils.deleteFavorite
import fi.tuni.tamk.catdogapp.data.FavoriteData

/**
 * A custom RecyclerViewAdapter meant to show a list of images in FavoritesActivity.
 *
 * @param context The context this RecyclerViewAdapter resides in, f.ex: an activity.
 */
class FavoritesRecViewAdapter(val context: Context) : RecyclerView.Adapter<FavoritesRecViewAdapter.ViewHolder>() {
    var imageData = mutableListOf<FavoriteData>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return imageData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.favorite_image, parent, false) as View

        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // There's a bunch that happens here, probably should have been split into multiple
        // functions.

        // Firstly, an image is loaded with Picasso into the list item this recycler view shows,
        // in this case, it's an ImageView
        Picasso.get().load(imageData[position].image?.url).fit().centerCrop().into(holder.imageView)

        // Then, each list item, or ImageView in this case, gets an onClickListener.
        holder.parent.setOnClickListener {
            // In hindsight, most code below, in this onClickListener, could / should have been
            // in it's own custom dialog class, probably.

            // A custom dialog is created. It shows the clicked image in a larger form, and a button
            // to delete that image from favorites.
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val inflater = LayoutInflater.from(context)
            val layout = inflater.inflate(R.layout.favorite_imageview,
                dialog.findViewById(R.id.fav_imgViewParent)
            )

            val imgView : ImageView = layout.findViewById(R.id.imgFavBig)
            Picasso.get().load(imageData[position].image?.url).fit().centerCrop().into(imgView)

            val displayMetrics = DisplayMetrics()
            val winMgr = context.getSystemService(WINDOW_SERVICE) as WindowManager

            // defaultDisplay is deprecated, but I couldn't find a workaround that works
            // with lower API levels than the suggested solution, which only works on API level 30!!
            // Suggested solution being: context.display instead of WindowManager.defaultDisplay
            winMgr.defaultDisplay.getRealMetrics(displayMetrics)

            val translateLeftAnim = AnimationUtils.loadAnimation(context, R.anim.translate_img_left)
            val translateRightAnim = AnimationUtils.loadAnimation(context, R.anim.translate_img_right)

            dialog.setContentView(layout)
            dialog.window?.setLayout(displayMetrics.widthPixels,
                displayMetrics.widthPixels + 176
            )

            dialog.show()

            // The image in the dialog can be swiped left and right to change to the next or
            // previous favorite image.
            var step = 0
            imgView.setOnTouchListener(object : OnSwipeTouchListener(context) {
                override fun onSwipeLeft() {
                    super.onSwipeLeft()
                    animateAndExecuteCallbackOnAnimationEnd(imgView, translateLeftAnim) {
                        if (position + step >= imageData.size - 1) {
                            Toast.makeText(context,
                                "You're at last picture",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            step += 1
                            switchImage(imgView, position + step)
                        }
                    }
                }

                override fun onSwipeRight() {
                    super.onSwipeRight()
                    animateAndExecuteCallbackOnAnimationEnd(imgView, translateRightAnim) {
                        if (position + step <= 0) {
                            Toast.makeText(context,
                                "You're at first picture",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            step -= 1
                            switchImage(imgView, position + step)
                        }
                    }
                }
            })

            // Delete button to delete currently viewed image from favorites.
            val deleteButton : Button = layout.findViewById(R.id.delFavButton)
            deleteButton.setOnClickListener {
                val ctx = context as FavoritesActivity
                // Log.d("TAG", imageData[position + step].image?.url!!)

                val url: String
                val key: String
                if (imageData[position + step].image?.url!!.contains("thecatapi")) {
                    url = ctx.config.apiUrls.cat
                    key = ctx.config.apiKeys.cat
                } else {
                    url = ctx.config.apiUrls.dog
                    key = ctx.config.apiKeys.dog
                }

                url.deleteFavorite(key, imageData[position + step].id!!) {
                    val snackBarText = when(it) {
                        "200" -> "Deleted from favorites successfully"
                        else -> {
                            "ERROR: Could not delete from favorites."
                        }
                    }
                    Snackbar.make(context.findViewById(R.id.favActivityParent),
                        snackBarText,
                        Snackbar.LENGTH_SHORT
                    ).show()

                    context.runOnUiThread() {
                        imageData.removeAt(position + step)
                        notifyDataSetChanged()
                    }

                    dialog.dismiss()
                }
            }
        }
    }

    /**
     * Using Picasso, switches the currently visible image to another one. This is called after
     * an animation plays when the user wants to switch images, which is why the ImageView supplied
     * to this function is temporarily set to invisible: so that the previous image doesn't
     * temporarily show up again before the new image is loaded.
     *
     * @param imgView ImageView the new image is loaded into.
     * @param position index in the ImageData list, where the new image is to be found in.
     */
    fun switchImage(imgView: ImageView, position: Int) {
        imgView.visibility = View.INVISIBLE
        Picasso.get().load(imageData[position].image?.url).fit().centerCrop().into(imgView)
        imgView.visibility = View.VISIBLE
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView : ImageView = itemView.findViewById(R.id.imgFav)
        val parent : RelativeLayout = itemView.findViewById(R.id.favParent)
    }
}