package com.davidshinto.fitenglish

import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener

fun View.startAnimation(animation: Animation, onEnd: () -> Unit){
    animation.setAnimationListener(object : AnimationListener{
        override fun onAnimationStart(animation: Animation?) = Unit

        override fun onAnimationEnd(animation: Animation?) {
            onEnd()
        }

        override fun onAnimationRepeat(animation: Animation?) = Unit
    })
    this.startAnimation(animation)
}