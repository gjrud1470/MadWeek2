package com.example.MadWeek2.Game

import android.view.SurfaceHolder
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TabHost
import android.widget.TextView
import com.example.MadWeek2.FireworksView
import com.example.MadWeek2.Game.Objects.Bullet_pool
import com.example.MadWeek2.Game.Objects.Survivor
import com.example.MadWeek2.R
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_game.view.*

class GameView(context: Context, attributeSet: AttributeSet) : SurfaceView(context, attributeSet), SurfaceHolder.Callback {

    private var mHolder: SurfaceHolder
    private var mThread: DrawThread? = null
    private var bulletThread: BulletThread? = null

    var survivors = arrayOfNulls<Survivor>(2)
    var bullets = Bullet_pool(resources)

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

    companion object {
        val DELAY = 10L
    }

    init {
        mHolder = holder
        mHolder.addCallback(this)

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

        cx = Width_d/2
        cy = Height_d/2
        x1 = cx
        y1 = cy
        dst.set(0, 0, Width_d, Height_d)

        imgBack = BitmapFactory.decodeResource(resources, R.drawable.game_background)
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
    }

    fun StartGame(players: Int) {
        val player1 = Survivor()
        player1.init((Width_d.toFloat()/2)-100, Height_d.toFloat()/2, 0.toFloat(), Width_d.toFloat(), Height_d.toFloat(), 1, resources)
        val player2 = Survivor()
        player2.init((Width_d.toFloat()/2)+100, Height_d.toFloat()/2, 0.toFloat(), Width_d.toFloat(), Height_d.toFloat(), 2, resources)
        survivors[0] = player1
        survivors[1] = player2


        bulletThread!!.start()
    }

    fun StopGame() {
        mThread!!.bExit = true
        bulletThread!!.bulletExit = true

        while (true) {
            try {
                mThread!!.join()
                bulletThread!!.join()
                break
            } catch (e: Exception) {
            }
        }
    }

    fun PauseGame() {
        mThread!!.isWait = true
        bulletThread!!.isWait = true
    }

    fun ResumeGame() {
        mThread!!.isWait = false
        bulletThread!!.isWait = false
    }

    fun RestartGame() {
        StopGame()  // 스레드 중지

        for (i in 0..1) {
            survivors[i] = null
        }

        // 현재의 스레드를 비우고 다시 생성
        mThread = null
        mThread = DrawThread(mHolder)
        bulletThread = null
        bulletThread = BulletThread(mHolder)
        mThread!!.start()
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

            bullets.animate()
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
                    for (i in 0..0) {
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
                    sleep(DELAY*20)
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