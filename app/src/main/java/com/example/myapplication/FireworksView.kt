package com.example.myapplication

import java.nio.file.Files.size
import android.view.SurfaceHolder
import android.view.MotionEvent
import android.content.Context
import android.graphics.*
import android.os.Debug
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceView
import java.util.jar.Attributes
import android.graphics.drawable.BitmapDrawable
import android.widget.LinearLayout
import android.graphics.Bitmap
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import kotlin.random.Random


class FireworksView(context: Context, attributeSet: AttributeSet) : SurfaceView(context, attributeSet), SurfaceHolder.Callback {
    val DEBUG_TAG = "FireworksView"
    val fireworks = Firework_particle()

    private var mHolder: SurfaceHolder
    private var mThread: DrawThread? = null
    private var touchThread: TouchThread? = null
    private var random_fire_Thread: Random_fireworks_Thread? = null

    companion object {
        val DELAY = 10L
    }

    init {
        mHolder = holder
        mHolder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mThread = DrawThread(mHolder)
        touchThread = TouchThread()
        random_fire_Thread = Random_fireworks_Thread()

        mThread!!.start()
        touchThread!!.start()
        random_fire_Thread!!.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mThread!!.bExit = true
        touchThread!!.tExit = true
        random_fire_Thread!!.rExit = true

        while (true) {
            try {
                mThread!!.join() // Thread 종료 기다리기
                touchThread!!.join()
                random_fire_Thread!!.join()
                break
            } catch (e: Exception) {
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (mThread != null) {
            mThread!!.SizeChange(width, height)
            random_fire_Thread!!.SizeChange(width, height)
        }
    }

    fun pause_mThread() {
        mThread!!.bExit = true
        while (true) {
            try {
                mThread!!.join() // Thread 종료 기다리기
                break
            } catch (e: Exception) {
            }
        }
    }

    fun restart_mTHread() {
        mThread!!.bExit = false
        mThread!!.start()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchThread!!.setEvent(event.action, event.x, event.y)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                fireworks.create(event.x.toDouble(), event.y.toDouble(), false)
            }
            //MotionEvent.ACTION_MOVE -> {
                //fireworks.create(event.x.toDouble(), event.y.toDouble(), true)
            //}
            else -> {
                return false
            }
        }
        return true
    }

    internal inner class TouchThread : Thread() {
        var tExit: Boolean = false
        var touchevent : Int = -1
        var x_ = 0f
        var y_ = 0f

        init {
            tExit = false
        }

        fun setEvent (action:Int, x:Float, y:Float) {
            touchevent = action
            x_ = x
            y_ = y
        }

        override fun run() {
            var startTime = System.currentTimeMillis()
            var time: Long = 0
            var down_flag = true

            while (tExit == false) {
                if (touchevent == MotionEvent.ACTION_UP)
                    down_flag = true
                while (touchevent == MotionEvent.ACTION_DOWN || touchevent == MotionEvent.ACTION_MOVE) {
                    if (touchevent == MotionEvent.ACTION_DOWN && down_flag) {
                        try {
                            sleep (100)
                        } catch (e: Exception) {}
                        down_flag = false
                        break
                    }
                    else if (touchevent == MotionEvent.ACTION_MOVE)
                        down_flag = true
                    time = System.currentTimeMillis()
                    if (time - startTime > 10) {
                        fireworks.create(x_.toDouble(), y_.toDouble(),true)
                        startTime = time
                    }
                }
            }

            try {
                sleep(FireworksView.DELAY)
            } catch (e: Exception) {
            }
        }
    }

    internal inner class DrawThread(var mHolder: SurfaceHolder) : Thread() {
        var bExit: Boolean = false
        var mWidth: Int = 0
        var mHeight: Int = 0

        init {
            bExit = false
        }

        fun SizeChange(Width: Int, Height: Int) {
            mWidth = Width
            mHeight = Height
        }

        override fun run() {
            var canvas: Canvas?

            while (bExit == false) {
                run {
                        fireworks.animate()
                    }

                synchronized(mHolder) {
                    canvas = mHolder.lockCanvas()
                    if (canvas != null) {
                        val surface_rect = Rect(0, 0, findViewById<FireworksView>(R.id.home_surface).width, findViewById<FireworksView>(R.id.home_surface).height)
                        val bitmap : Bitmap = (resources.getDrawable(R.drawable.home_background_cut) as BitmapDrawable).getBitmap()
                        canvas!!.drawBitmap(bitmap, null, surface_rect, null)
                        fireworks.doDraw(canvas)
                    }

                    mHolder.unlockCanvasAndPost(canvas)
                }

                try {
                    sleep(FireworksView.DELAY)
                } catch (e: Exception) {
                }
            }
        }
    }

    internal inner class Random_fireworks_Thread : Thread() {
        private val random = Random
        var rExit: Boolean = false
        var max_x: Int? = null
        var max_y: Int? = null

        init {
            rExit = false
        }

        fun SizeChange(Width: Int, Height: Int) {
            max_x = Width
            max_y = Height
        }

        override fun run() {
            while (rExit == false) {
                val move = when (random.nextInt(1)) {
                    0 -> false
                    else -> true
                }
                if (max_x != null && max_y != null)
                    fireworks.create(psudo_normal_dist(max_x!!), psudo_normal_dist(max_y!!), move)

                try {
                    sleep(random.nextLong(500, 2000))
                } catch (e: Exception) {
                }
            }
        }

        fun psudo_normal_dist (max:Int) : Double {
            val horizontal = when (random.nextInt(2)) {
                0 -> -1
                else -> 1
            }
            val rand_x = random.nextDouble(0.toDouble(), 3.toDouble())
            var x_res = (max/2 * horizontal * 1/(1 + (rand_x * rand_x))) + (max/2)

            return x_res
        }
    }
}