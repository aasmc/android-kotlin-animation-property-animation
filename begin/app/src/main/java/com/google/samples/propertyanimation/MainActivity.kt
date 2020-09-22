/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.propertyanimation

import android.animation.*
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView


class MainActivity : AppCompatActivity() {

    lateinit var star: ImageView
    lateinit var rotateButton: Button
    lateinit var translateButton: Button
    lateinit var scaleButton: Button
    lateinit var fadeButton: Button
    lateinit var colorizeButton: Button
    lateinit var showerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        star = findViewById(R.id.star)
        rotateButton = findViewById<Button>(R.id.rotateButton)
        translateButton = findViewById<Button>(R.id.translateButton)
        scaleButton = findViewById<Button>(R.id.scaleButton)
        fadeButton = findViewById<Button>(R.id.fadeButton)
        colorizeButton = findViewById<Button>(R.id.colorizeButton)
        showerButton = findViewById<Button>(R.id.showerButton)

        rotateButton.setOnClickListener {
            rotater()
        }

        translateButton.setOnClickListener {
            translater()
        }

        scaleButton.setOnClickListener {
            scaler()
        }

        fadeButton.setOnClickListener {
            fader()
        }

        colorizeButton.setOnClickListener {
            colorizer()
        }

        showerButton.setOnClickListener {
            shower()
        }
    }

    /**
     * Rotates the star from -360 degrees to 0 degrees (full circle)
     * Any view's default rotation value is 0 degrees, so here we end up in that position
     * It is recommended because later animations invoked on the view may expect the default value
     */
    private fun rotater() {
        /**
         * @param star target view
         * @param View.ROTATION - property to be animated
         * @param -360f start value for the animation
         * @param 0f end value for the animation
         */
        val animator = ObjectAnimator.ofFloat(star, View.ROTATION, -360f, 0f)
        animator.duration = 1000 // default duration is 300 ms
        // adding a listener to disable button when rotation is in progress
        animator.disableButtonDuringAnimation(rotateButton)
        animator.start()

    }

    private fun ObjectAnimator.disableButtonDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }

    /**
     * Translates the star image along the X axis by 200 pixels and returns it back
     * Disables the button during animation
     */
    private fun translater() {
        val animator = ObjectAnimator.ofFloat(star, View.TRANSLATION_X, 200f)
        animator.apply {
            disableButtonDuringAnimation(translateButton)
            duration = 1000
            // indicates that the animation will be repeated once after the first time
            repeatCount = 1
            // indicates that during the repetition the view will go back to its initial position
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    /**
     * Scales the target object according to the PropertyValuesHolder parameters passed to the
     * ObjectAnimator.Ideal use-case for the PropertyValuesHolder is when you need to animate
     * multiple properties in parallel
     */
    private fun scaler() {
        // create holder to store TRANSLATION_X property
        // value 4f means that the target object will increase 4 times along the X axis from 1f (default value) to 4f
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f)
        // value 4f means that the target object will increase 4 times along the Y axis from 1f (default value) to 4f
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f)

        val animator = ObjectAnimator.ofPropertyValuesHolder(star, scaleX, scaleY)
        animator.apply {
            repeatCount = 1
            duration = 2000
            repeatMode = ObjectAnimator.REVERSE
            disableButtonDuringAnimation(scaleButton)
        }
        animator.start()
    }

    /**
     * Fades the target object from the screen and then makes it reappear
     */
    private fun fader() {
        ObjectAnimator.ofFloat(star, View.ALPHA, 0f).apply {
            repeatCount = 1
            duration = 1000
            repeatMode = ObjectAnimator.REVERSE
            disableButtonDuringAnimation(fadeButton)
        }.start()
    }

    /**
     * Colors the background from black to red and back to black
     * Creates ObjectAnimator from Argb factory that enables smooth transition between colors
     * background property isn't in android.util.Property package therefore we can't use View.BACKGROUND as
     * we used n earlier methods. We need to pass a string that corresponds to the getter and setter
     * Of the property, i.e. View.setBackgroundColor(int)
     */
    private fun colorizer() {
        ObjectAnimator.ofArgb(star.parent, "backgroundColor", Color.BLACK, Color.RED).apply {
            duration = 2000
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
            disableButtonDuringAnimation(colorizeButton)
        }.start()
    }

    private fun shower() {
        val container = star.parent as ViewGroup

        val containerW = container.width
        val containerH = container.height
        var starH = star.height.toFloat()
        var starW = star.width.toFloat()

        val newStar = AppCompatImageView(this).apply {
            setImageResource(R.drawable.ic_star)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }
        container.addView(newStar)
        newStar.apply {

            // set the size of the star to range from 0.1f to 1.6f from its default size
            // use the scale factor to later calculate width and height pixels
            scaleX = Math.random().toFloat() * 1.5f + 0.1f
            scaleY = scaleX

            // cache the dimens to calculate the star position on the screen
            starW *= scaleX
            starH *= scaleY

            // position the star somewhere on the screen from the left edge to the right edge
            // the star may be positioned half-way off the screen from both left and right
            translationX = Math.random().toFloat() * containerW - starW / 2
        }

        // to provide different types of motions we need different interpolators that are passed to
        // ObjectAnimators as parameters. Therefore we need different ObjectAnimators to enable
        // parallel animation

        // start value for the mover is -starH to make star appear from the top of the screen
        // end value is containerH + starH enables the star to disappear at the bottom of the screen
        val mover = ObjectAnimator.ofFloat(newStar, View.TRANSLATION_Y, -starH, containerH + starH)

        // add interpolator that will accelerate the animation at a constant rate
        mover.interpolator = AccelerateInterpolator(1f)

        // a star will randomly rotate up to three times around
        val rotator = ObjectAnimator.ofFloat(newStar, View.ROTATION, (Math.random() * 3 * 360).toFloat())

        val set = AnimatorSet()
        set.playTogether(mover, rotator)
        set.duration = (Math.random() * 1500 + 500).toLong()

        // !! IMPORTANT
        // once the star has fallen down we need to remove it from the screen
        // here we use listener
        set.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                container.removeView(newStar)
            }
        })

        set.start()
    }

}
