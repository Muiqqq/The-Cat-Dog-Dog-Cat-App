package fi.tuni.tamk.catdogapp.utils

import android.view.View
import android.view.animation.Animation
import fi.tuni.tamk.catdogapp.AnimationAdapter

/**
 * Sets a callback function to be called on animation end and then starts the animation.
 *
 * @param v View in which the animation is supposed to be started in.
 * @param a Animation that is being given an onAnimationEnd() implementation.
 * @param cb Callback function, the implementation of onAnimationEnd()
 */
fun animateAndExecuteCallbackOnAnimationEnd(v: View, a: Animation, cb: () -> Unit) {
    a.setAnimationListener(object : AnimationAdapter() {
        override fun onAnimationEnd(animation: Animation?) {
            super.onAnimationEnd(animation)
            cb()
        }
    })
    v.startAnimation(a)
}

/**
 * Mixes two mutable lists, so that every other element is from listA, and every other from listB.
 *
 * @param listA First MutableList
 * @param listB Second MutableList
 * @return ListA and ListB combined, with every other element from listA, every other from ListB.
 */
fun <T> mixMutableLists(listA: MutableList<T>, listB: MutableList<T>): MutableList<T> {
    val smaller : MutableList<T>
    val combined : MutableList<T> = if (listA.size >= listB.size) {
        smaller = listB
        listA
    } else {
        smaller = listA
        listB
    }
    for (i in 0 until listA.size + listB.size step 2) {
        if (smaller.size > 0) {
            combined.add(i, smaller.removeFirst())
        }
    }
    return combined
}