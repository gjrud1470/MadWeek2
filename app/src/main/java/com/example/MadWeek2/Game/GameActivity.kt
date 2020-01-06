package com.example.MadWeek2.Game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import com.example.MadWeek2.R
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_game.view.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class GameActivity : AppCompatActivity() {
    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        myGameView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreen_content_controls.visibility = View.VISIBLE
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    private var game_started: Boolean = false
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set Create game, Join game text pulses
        val pulse = AnimationUtils.loadAnimation(this, R.anim.heart_pulse)
        create_game.startAnimation(pulse)
        join_game.startAnimation(pulse)

        create_game.setOnClickListener {
            create_room()
        }
        leave_room_button.setOnClickListener {
            leave_room()
        }
        quit_game.setOnClickListener {
            finish()
        }
        start_game_button.setOnClickListener {
            start_game()
        }

        joystickView_left.setOnMoveListener { angle, strength ->
            myGameView.survivors[0]!!.setmovement(angle.toFloat(), strength/10.toFloat())
        }
        joystickView_right.setOnMoveListener { angle, _ ->
            myGameView.survivors[0]!!.setshootangle(angle.toFloat())
        }

        mVisible = true

        // Set up the user interaction to manually show or hide the system UI.
        myGameView.setOnClickListener { if (game_started) toggle() }
        fullscreen_content_controls.setOnClickListener { if (game_started) toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        pause_game.setOnClickListener {
            myGameView.PauseGame()
            resume_game.visibility = View.VISIBLE
            pause_game.visibility = View.GONE
        }
        resume_game.setOnClickListener {
            toggle()
            myGameView.ResumeGame()
            resume_game.visibility = View.GONE
            pause_game.visibility = View.VISIBLE
        }
        stop_game.setOnClickListener {
            toggle()
            myGameView.RestartGame()
            game_options.visibility = View.VISIBLE
            joystickView_left.visibility = View.INVISIBLE
            joystickView_right.visibility = View.INVISIBLE
            game_started = false
        }


        var player_list = ArrayList<String>()
        player_list.add("1. DEFAULT_NAME")
        val adapter = ArrayAdapter(this, R.layout.room_member_item, player_list)
        room_member_list.adapter = adapter
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun create_room() {
        game_options.visibility = View.INVISIBLE
        matchmaking_layout.visibility = View.VISIBLE
    }

    private fun leave_room() {
        game_options.visibility = View.VISIBLE
        matchmaking_layout.visibility = View.GONE
    }

    private fun start_game() {
        joystickView_left.visibility = View.VISIBLE
        joystickView_right.visibility = View.VISIBLE
        matchmaking_layout.visibility = View.GONE

        game_started = true
        myGameView.StartGame(2)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreen_content_controls.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        myGameView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    override fun onBackPressed() {
        finish()
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }
}
