package com.example.myapplication

import java.nio.file.Files.size
import android.view.SurfaceHolder
import android.view.MotionEvent
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.SurfaceView
import java.util.jar.Attributes

//import com.example.myapplication.R


class FireworksView(context: Context, attributeSet: AttributeSet) : SurfaceView(context, attributeSet), SurfaceHolder.Callback {
    //var mBack: Bitmap
    val fireworks = Firework_particle()

    private var mHolder: SurfaceHolder
    private var mThread: DrawThread? = null

    init {
        //mBack = BitmapFactory.decodeResource(context.getResources(), R.drawable.family)

        mHolder = holder
        mHolder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mThread = DrawThread(mHolder)
        mThread!!.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mThread!!.bExit = true
        while (true) {
            try {
                mThread!!.join() // Thread 종료 기다리기
                break
            } catch (e: Exception) {
            }

        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (mThread != null) {
            mThread!!.SizeChange(width, height)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> synchronized(mHolder) {
                fireworks.create(event.x, event.y)
            }
            else -> {
                return false
            }
        }
        return true
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
                    if (canvas != null)
                        fireworks.doDraw(canvas)

                    mHolder.unlockCanvasAndPost(canvas)
                }
/*
                try {
                    Thread.sleep(MyView.DELAY)
                } catch (e: Exception) {
                }*/

            }
        }
    }
/*
    companion object {
        val DELAY = 50
        val RAD = 24
    }

 */
}