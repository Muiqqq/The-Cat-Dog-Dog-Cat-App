package fi.tuni.tamk.catdogapp

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import androidx.core.content.res.ResourcesCompat

/**
 * A Custom dialog which shows a text and three buttons. Purpose of this dialog
 * is to swap the mode the app is in, that happens with the buttons.
 *
 * @param context The context this dialog is supposed to be created in, f. ex: an activity.
 * @constructor Creates a dialog with custom background, assigns onClickListeners to the buttons.
 */
class SwapModeDialog(context: Context) {
    val dialog = Dialog(context)

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(
            ResourcesCompat.getDrawable(context.resources,
            R.drawable.swap_dialog_background,
            null))

        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.swap_mode_dialog,
            dialog.findViewById(R.id.swapModeDialogParent)
        )

        val bothBtn : Button = layout.findViewById(R.id.bothBtn)
        val catBtn : Button = layout.findViewById(R.id.catBtn)
        val dogBtn : Button = layout.findViewById(R.id.dogBtn)

        val ctx = context as MainActivity
        val cfg = ctx.config

        /**
         * Helper function to be called in the onClickListeners. Calls changeAppState() of the
         * config object with the given stateId.
         *
         * @see fi.tuni.tamk.catdogapp.AppConfig
         * @param stateId Int identifier of the state to be swapped to.
         */
        fun changeAppState(stateId: Int) {
            dialog.dismiss()
            if (stateId != cfg.appState) {
                cfg.changeAppState(stateId)
                ctx.recreate()
            }
        }

        bothBtn.setOnClickListener {
            changeAppState(AppConfig.PREFLESS)
        }

        catBtn.setOnClickListener {
            changeAppState(AppConfig.CAT)
        }

        dogBtn.setOnClickListener {
            changeAppState(AppConfig.DOG)
        }
        dialog.setContentView(layout)
    }

    /**
     * A helper function so you can call swapModeDialog.show() instead of swapModeDialog.dialog.show()
     */
    fun show() {
        dialog.show()
    }
}