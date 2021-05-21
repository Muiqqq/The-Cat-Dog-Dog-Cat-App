package fi.tuni.tamk.catdogapp

import android.view.animation.Animation

/**
 * An adapter class for AnimationListener
 */
open class AnimationAdapter : Animation.AnimationListener {
    override fun onAnimationStart(animation: Animation?) {}

    override fun onAnimationEnd(animation: Animation?) {}

    override fun onAnimationRepeat(animation: Animation?) {}
}