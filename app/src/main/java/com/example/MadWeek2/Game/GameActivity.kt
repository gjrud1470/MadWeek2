package com.example.MadWeek2.Game

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.MadWeek2.R
import com.example.MadWeek2.Retrofit.ContactService
import com.example.MadWeek2.Retrofit.RetrofitClient
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.socket.client.IO
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_game.view.*
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

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

    var game_started: Boolean = false
    var pulse : Animation? = null

    var mSocket : Socket? = null
    lateinit var MyGameService: ContactService
    internal var compositeDisposable = CompositeDisposable()
    private var user_name : String = "Default_name"

    val mhandler = Handler()

    var player_list = ArrayList<String>()
    var player_number = -1

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

        // Setup for server communication
        val retrofit = RetrofitClient.getInstance()
        MyGameService = retrofit.create(ContactService::class.java)

        val salt = find_login_info()
        if (salt != null) {
            get_user_name(salt)
            user_name = find_name_info()!!
        }

        // Connect to Socket
        try {
            mSocket = IO.socket("http://192.249.19.251:0480")
            mSocket!!.connect()
            mSocket!!.emit("open_game", user_name)
            mSocket!!.on("create_failed") {
                mhandler.post { Toast.makeText(this, "Game already exists. Try joining one.", Toast.LENGTH_SHORT).show() }
            }
            mSocket!!.on("room_created") {
                mhandler.post { create_room() }
            }
            mSocket!!.on("join_failed") {
                mhandler.post { Toast.makeText(this, "Game does not exists. Try creating one.", Toast.LENGTH_SHORT).show() }
            }
            mSocket!!.on("join_success") {
                mhandler.post {
                    join_room(it[0].toString())
                }
            }
            mSocket!!.on("user_entered") {
                mhandler.post {
                    player_list.add("2. ${it[0]}")
                    val adapter = ArrayAdapter(this, R.layout.room_member_item, player_list)
                    room_member_list.adapter = adapter
                    //room_member_list.invalidate()
                }
            }
            mSocket!!.on("user_left_room") {
                mhandler.post {
                    player_list.remove("1. ${it[0]}")
                    player_list.remove("2. ${it[0]}")
                    val adapter = ArrayAdapter(this, R.layout.room_member_item, player_list)
                    room_member_list.adapter = adapter
                    //room_member_list.invalidate()
                }
            }
            mSocket!!.on("game_started") {
                start_game()
            }
            mSocket!!.on("player1_move") {
                val myplayer = myGameView.survivors[0]
                val json_object = it[0] as JSONObject
                val angle = json_object.getInt("angle")
                val strength = json_object.getInt("strength")
                myplayer?.setmovement(angle.toFloat(), strength/5.toFloat())
            }
            mSocket!!.on("player1_shoot") {
                val myplayer = myGameView.survivors[0]
                val angle = it[0] as Int
                myplayer?.setshootangle(angle.toFloat())
            }
            mSocket!!.on("player2_move") {
                val myplayer = myGameView.survivors[1]
                val json_object = it[0] as JSONObject
                val angle = json_object.getInt("angle")
                val strength = json_object.getInt("strength")
                myplayer?.setmovement(angle.toFloat(), strength/5.toFloat())
            }
            mSocket!!.on("player2_shoot") {
                val myplayer = myGameView.survivors[1]
                val angle = it[0] as Int
                myplayer?.setshootangle(angle.toFloat())
            }
            mSocket!!.on("generate_hunter") {
                val json_object = it[0] as JSONObject
                val id = json_object.getInt("id")
                val x = json_object.getDouble("x")
                val y = json_object.getDouble("y")
                myGameView.hunter_create(x, y, id)
            }
            mSocket!!.on("is_player_dead") {
                val id = it[0] as Int
                if (id == 1) {
                    if (myGameView.player1flag) {
                        mSocket!!.emit("player_truly_dead", id)
                    }
                    else
                        mSocket!!.emit("player_not_dead", id)
                }
                else if (id == 2) {
                    if (myGameView.player2flag) {
                        mSocket!!.emit("player_truly_dead", id)
                    }
                    else
                        mSocket!!.emit("player_not_dead", id)
                }
            }
            mSocket!!.on("player_dead") {
                val id = it[0] as Int
                if (id == 1) {
                    myGameView.player1death = true
                    if (myGameView.survivors[1] != null) {
                        mhandler.post {
                            Toast.makeText(this, "Player1 has been KILLED!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else if (id == 2) {
                    myGameView.player2death = true
                    if (myGameView.survivors[0] != null) {
                        mhandler.post {
                            Toast.makeText(this, "Player2 has been KILLED!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            mSocket!!.on("player_not_dead") {
                val id = it[0] as Int
                if (id == 1) {
                    myGameView.player1flag = false
                }
                else if (id == 2) {
                    myGameView.player2flag = false
                }
            }
        } catch (e : Exception) {
        }

        // Set Create game, Join game text pulses
        pulse = AnimationUtils.loadAnimation(this, R.anim.heart_pulse)
        create_game.startAnimation(pulse)
        join_game.startAnimation(pulse)

        create_game.setOnClickListener {
            mSocket!!.emit("create_game", user_name)
        }
        join_game.setOnClickListener {
            mSocket!!.emit("join_game", user_name)
        }
        leave_room_button.setOnClickListener {
            player_list = ArrayList<String>()
            mSocket!!.emit("leave_room", user_name)
            leave_room()
        }
        quit_game.setOnClickListener {
            mSocket!!.disconnect()
            finish()
        }
        start_game_button.setOnClickListener {
            mSocket!!.emit("start_game")
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
            restart_game()
        }

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun create_room() {
        player_number = 0

        player_list.add("1. ${user_name}")
        val adapter = ArrayAdapter(this, R.layout.room_member_item, player_list)
        room_member_list.adapter = adapter

        game_options.visibility = View.GONE
        matchmaking_layout.visibility = View.VISIBLE
    }

    private fun join_room(name : String) {
        player_number = 1

        player_list.add("1. ${name}")
        player_list.add("2. ${user_name}")
        val adapter = ArrayAdapter(this, R.layout.room_member_item, player_list)
        room_member_list.adapter = adapter

        game_options.visibility = View.GONE
        matchmaking_layout.visibility = View.VISIBLE
        start_game_button.visibility = View.GONE
    }

    private fun leave_room() {
        player_number = -1

        game_options.visibility = View.VISIBLE
        start_game_button.visibility = View.VISIBLE
        matchmaking_layout.visibility = View.GONE
    }

    private fun start_game() {
        joystickView_left.setOnMoveListener { angle, strength ->
            mSocket!!.emit("player${player_number+1}_move", angle, strength)
        }
        joystickView_right.setOnMoveListener { angle, _ ->
            mSocket!!.emit("player${player_number+1}_shoot", angle)
        }

        mhandler.post {
            joystickView_left.visibility = View.VISIBLE
            joystickView_right.visibility = View.VISIBLE
            matchmaking_layout.visibility = View.GONE
        }

        game_started = true
        myGameView.StartGame(2)
    }

    fun restart_game() {
        mSocket!!.emit("stop_game")
        player_list = ArrayList<String>()

        myGameView.RestartGame()

        joystickView_left.visibility = View.GONE
        joystickView_right.visibility = View.GONE
        game_options.visibility = View.VISIBLE
        create_join_holder.visibility = View.VISIBLE
        create_game.visibility = View.VISIBLE
        join_game.visibility = View.VISIBLE

        game_started = false
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

    private fun get_user_name (salt: String) {
        compositeDisposable.add(MyGameService.get_user_name(salt)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                val json_object = JSONObject(result)
                val success = json_object.getString("get_name")
                if (success == "success") {
                    save_name_info(json_object.getString("name"))
                }
            })
    }

    private fun save_name_info (name: String) {
        val pref : SharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = pref.edit()
        editor.putString("name", name)
        editor.apply()
    }

    private fun find_name_info () : String? {
        val pref : SharedPreferences = this.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return pref.getString("name", "")
    }

    private fun find_login_info () : String? {
        val pref : SharedPreferences = this.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return pref.getString("salt", "")
    }

    /* ######################################################################### */
}
