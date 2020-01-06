package com.example.MadWeek2.Game

import android.view.SurfaceHolder
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.example.MadWeek2.R
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_game.view.*

class GameView(context: Context, attributeSet: AttributeSet) : SurfaceView(context, attributeSet), SurfaceHolder.Callback {

    private var mHolder: SurfaceHolder
    private var mThread: DrawThread? = null

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
        cx = Width_d/2
        cy = Height_d/2
        x1 = cx
        y1 = cy
        dst.set(0, 0, Width_d, Height_d)

        imgBack = BitmapFactory.decodeResource(resources, R.drawable.game_background)
        //imgBack = (resources.getDrawable(R.drawable.game_background) as BitmapDrawable).getBitmap()
        imgBack = Bitmap.createScaledBitmap(imgBack!!, Width_d * 2, Height_d * 2, true)

        mThread!!.start()

        val joystickleft = findViewById<JoystickView>(R.id.joystickView_left)
        joystickleft.visibility = View.VISIBLE
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        StopGame()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (mThread != null) {
            mThread!!.SizeChange(width, height)
        }
    }

    fun StopGame() {
        mThread!!.bExit = true

        while (true) {
            try {
                mThread!!.join() // Thread 종료 기다리기
                break
            } catch (e: Exception) {
            }
        }
    }

    fun PauseGame() {
        mThread!!.isWait = true

        while (true) {
            try {
                mThread!!.join() // Thread 종료 기다리기
                break
            } catch (e: Exception) {
            }
        }
    }

    fun ResumeGame() {
        mThread!!.isWait = false
    }

    fun RestartGame() {
        StopGame()  // 스레드 중지

        // 현재의 스레드를 비우고 다시 생성
        mThread = null
        mThread = DrawThread(mHolder)
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
            canvas.drawBitmap(imgBack!!, src, dst, null)
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
}