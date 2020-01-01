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


//import com.example.myapplication.R


class FireworksView(context: Context, attributeSet: AttributeSet) : SurfaceView(context, attributeSet), SurfaceHolder.Callback {
    val DEBUG_TAG = "FireworksView"
    val fireworks = Firework_particle()

    private var mHolder: SurfaceHolder
    private var mThread: DrawThread? = null

    companion object {
        val DELAY = 10L
        val RAD = 24
    }

    init {
        mHolder = holder
        mHolder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mThread = DrawThread(mHolder)
        //mThread!!.start()
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
                Log.wtf(DEBUG_TAG, "ACTION_DOWN observed")
                fireworks.create(event.x.toDouble(), event.y.toDouble())
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
}