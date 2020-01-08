package com.example.MadWeek2.Game

import android.view.SurfaceHolder
import android.content.Context
import android.graphics.*
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TabHost
import android.widget.TextView
import android.widget.Toast
import com.example.MadWeek2.FireworksView
import com.example.MadWeek2.Game.Objects.Bullet_pool
import com.example.MadWeek2.Game.Objects.Hunter
import com.example.MadWeek2.Game.Objects.Hunter_pool
import com.example.MadWeek2.Game.Objects.Survivor
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_game.view.*
import kotlin.random.Random
import android.os.Handler
import android.os.Message
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.MadWeek2.R


class GameView(context: Context, attributeSet: AttributeSet) : SurfaceView(context, attributeSet), SurfaceHolder.Callback {

    private val gameactivity = context as GameActivity
    private var survivors_created = false

    private var mHolder: SurfaceHolder
    private var mThread: DrawThread? = null
    private var bulletThread: BulletThread? = null
    //private var hunterThread: HunterThread? = null

    var survivors = arrayOfNulls<Survivor>(2)
    var bullets = Bullet_pool(resources)
    var hunters = Hunter_pool(resources, bullets)

    var player_number = 0

    private var x1 = 0
    private var y1 = 0
    private var cx = 0
    private var cy = 0
    private var sx = -1
    private var sy = -1
    private var Width_d = 0
    private var Height_d = 0

    var imgBack : Bitmap? = null
    var src = Rect()
    var dst = Rect()

    var mhandler : Handler? = null
    private var toast_flag = true
    var player1flag = false
    var player2flag = false
    var player1death = false
    var player2death = false

    companion object {
        val DELAY = 25L
    }

    init {
        mHolder = holder
        mHolder.addCallback(this)

        mhandler = Handler()

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size = Point()
        wm.defaultDisplay.getSize(size)
        Width_d = size.x
        Height_d = size.y

        InitGame()
    }

    private fun InitGame() {

        //joystickView_left.visibility = View.VISIBLE
        //joystickView_right.visibility = View.VISIBLE

    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mThread = DrawThread(mHolder)
        bulletThread = BulletThread(mHolder)
        //hunterThread = HunterThread(mHolder)

        cx = Width_d/2
        cy = Height_d/2
        x1 = cx
        y1 = cy
        dst.set(0, 0, Width_d, Height_d)

        imgBack = BitmapFactory.decodeResource(resources, com.example.MadWeek2.R.drawable.game_background)
        //imgBack = (resources.getDrawable(R.drawable.game_background) as BitmapDrawable).getBitmap()
        imgBack = Bitmap.createScaledBitmap(imgBack!!, Width_d * 2, Height_d * 2, true)

        mThread!!.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        StopGame()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (mThread != null) {
            mThread!!.SizeChange(width, height)
        }
        if (bulletThread != null) {
            bulletThread!!.SizeChange(width, height)
        }
        //if (hunterThread != null) {
            //hunterThread!!.SizeChange(width, height)
        //}
    }

    fun StartGame(players: Int) {
        player_number = players

        val player1 = Survivor()
        player1.init((Width_d.toFloat()/2)-100, Height_d.toFloat()/2, 0.toFloat(), Width_d.toFloat(), Height_d.toFloat(), 1, resources)
        val player2 = Survivor()
        player2.init((Width_d.toFloat()/2)+100, Height_d.toFloat()/2, 0.toFloat(), Width_d.toFloat(), Height_d.toFloat(), 2, resources)
        survivors[0] = player1
        survivors[1] = player2

        survivors_created = true
        toast_flag = true
        player1flag = false
        player2flag = false

        bulletThread!!.start()
        //hunterThread!!.start()
    }

    fun StopGame() {
        mThread!!.bExit = true
        bulletThread!!.bulletExit = true
        //hunterThread!!.hunterExit = true

        while (true) {
            try {
                mThread!!.join()
                bulletThread!!.join()
                //hunterThread!!.join()
                break
            } catch (e: Exception) {
            }
        }
    }

    fun PauseGame() {
        mThread!!.isWait = true
        bulletThread!!.isWait = true
        //hunterThread!!.isWait = true
    }

    fun ResumeGame() {
        mThread!!.isWait = false
        bulletThread!!.isWait = false
        //hunterThread!!.isWait = false
    }

    fun RestartGame() {
        StopGame()  // 스레드 중지
        toast_flag = false

        for (i in 0..1) {
            survivors[i] = null
        }

        // 현재의 스레드를 비우고 다시 생성
        mThread = null
        mThread = DrawThread(mHolder)
        bulletThread = null
        bulletThread = BulletThread(mHolder)
        //hunterThread = null
        //hunterThread = HunterThread(mHolder)

        bullets = Bullet_pool(resources)
        hunters = Hunter_pool(resources, bullets)

        mThread!!.start()
    }

    fun hunter_create (x: Double, y: Double, id: Int) {
        var x = x * Width_d
        var y = y * Height_d
        if (x < Width_d/2) {x += Width_d}
        else {x -= Width_d}
        if (y < Height_d/2) {y += Height_d}
        else {y -= Height_d}

        hunters.create(x.toFloat(), y.toFloat(),
            Width_d.toFloat(), Height_d.toFloat(), 0)
    }

    internal inner class DrawThread(var mHolder: SurfaceHolder) : Thread() {
        var bExit: Boolean = false
        var isWait: Boolean = false
        var mWidth: Int = 0
        var mHeight: Int = 0

        init {
            bExit = false
        }

        fun MoveAll() {
            ScrollImage()
            src.set(x1, y1, x1 + cx, y1 + cy)

            for (i in 0..1) {
                if (survivors[i] != null)
                    survivors[i]!!.animate()
            }

            var hit_id = 0
            bullets.animate()
            if (survivors[0] != null && survivors[1] != null) {
                hit_id = hunters.animate(
                    survivors[0]!!.x_+50,
                    survivors[0]!!.y_+50,
                    survivors[1]!!.x_+50,
                    survivors[1]!!.y_+50
                )
            }
            else if (survivors[0] != null) {
                hit_id = hunters.animate(
                    survivors[0]!!.x_+50,
                    survivors[0]!!.y_+50,
                    null,
                    null
                )
            }
            else if (survivors[1] != null) {
                hit_id = hunters.animate(
                    null,
                    null,
                    survivors[1]!!.x_+50,
                    survivors[1]!!.y_+50
                )
            }

            if (player1death) survivors[0] = null
            if (player2death) survivors[1] = null

            if (hit_id != 0) {
                if (hit_id == 1) player1flag = true
                else if (hit_id == 2) player2flag = true
                gameactivity.mSocket!!.emit("player_truly_dead", hit_id)
                //survivors[hit_id-1] = null
/*
                if (survivors[0] != null || survivors[1] != null) {
                    mhandler!!.post {
                        run() {
                            Toast.makeText(
                                context,
                                "Player${hit_id} has been KILLED!", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
 */
            }

            if (toast_flag && survivors_created
                && survivors[0] == null && survivors[1] == null) {
                survivors_created = false

                //RestartGame()
                mhandler!!.post {
                    run() {
                        Toast.makeText(context, "HAHA YOU LOSE!!", Toast.LENGTH_LONG).show()
                        gameactivity.restart_game()
                    }
                }
            }
        }

        fun ScrollImage() {
            x1 += sx
            y1 += sy
            if (x1 < 0 || x1 > Width_d) {
                sx *= -1
                x1 += sx * 2
            }
            if (y1 < 0 || y1 > Height_d) {
                sy *= -1
                y1 += sy * 2
            }
        }

        fun DrawAll(canvas: Canvas) {
            // Draw background
            canvas.drawBitmap(imgBack!!, src, dst, null)

            // Draw Bullets
            bullets.doDraw(canvas)
            // Draw Hunters
            hunters.doDraw(canvas)

            // Draw Survivors
            for (i in 0..1) {
                if (survivors[i] != null)
                    survivors[i]!!.doDraw(canvas)
            }
        }

        fun SizeChange(Width: Int, Height: Int) {
            mWidth = Width
            mHeight = Height
        }

        override fun run() {
            var canvas: Canvas?

            while (bExit == false) {
                run {
                }

                canvas = mHolder.lockCanvas()
                if (canvas != null) {
                    synchronized(mHolder) {
                        MoveAll()
                        DrawAll(canvas)
                    }
                    mHolder.unlockCanvasAndPost(canvas)
                }

                try {
                    sleep(DELAY)
                } catch (e: Exception) {
                }

                while (isWait) {
                    try {
                        sleep(DELAY)
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    internal inner class BulletThread (var bHolder: SurfaceHolder) : Thread() {
        var bulletExit: Boolean = false
        var isWait: Boolean = false

        var bWidth: Int = 0
        var bHeight: Int = 0

        init {
            bulletExit = false
        }

        fun SizeChange(Width: Int, Height: Int) {
            bWidth = Width
            bHeight = Height
        }

        override  fun run() {
            while (bulletExit == false) {
                run {
                    for (i in 0..player_number-1) {
                        if (survivors[i] != null) {
                            bullets.create(
                                survivors[i]!!.x_+50,
                                survivors[i]!!.y_+50,
                                Width_d.toFloat(),
                                Height_d.toFloat(),
                                survivors[i]!!.angle_
                            )
                        }
                    }
                }
                try {
                    sleep(DELAY*10)
                } catch (e: Exception) {
                }
                while (isWait) {
                    try {
                        sleep(DELAY)
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    internal inner class HunterThread (var bHolder: SurfaceHolder) : Thread() {
        var hunterExit: Boolean = false
        var isWait: Boolean = false

        var bWidth: Int = 0
        var bHeight: Int = 0
        private val random = Random

        init {
            hunterExit = false
        }

        fun SizeChange(Width: Int, Height: Int) {
            bWidth = Width
            bHeight = Height
        }

        override  fun run() {
            while (hunterExit == false) {
                run {
                    val ran = random.nextInt(0, 10)
                    var x = random.nextDouble(0.toDouble(), Width_d.toDouble())
                    var y = random.nextDouble(0.toDouble(), Height_d.toDouble())
                    if (x < Width_d/2) {x += Width_d}
                    else {x -= Width_d}
                    if (y < Height_d/2) {y += Height_d}
                    else {y -= Height_d}

                    if (ran > 0) {    //  create Timmy
                        hunters.create(x.toFloat(), y.toFloat(), Width_d.toFloat(),
                            Height_d.toFloat(), 0)
                    }
                }
                try {
                    sleep(DELAY*30)
                } catch (e: Exception) {
                }
                while (isWait) {
                    try {
                        sleep(DELAY)
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }
}