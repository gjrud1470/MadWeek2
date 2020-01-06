package com.example.MadWeek2.Game.Objects

import android.content.res.Resources
import android.graphics.*
import com.example.MadWeek2.R

class Timmy {
    private var x_:Float = 0.toFloat()
    private var y_:Float = 0.toFloat()
    private var screen_width_:Float = 0.toFloat()
    private var screen_height_:Float = 0.toFloat()
    private var vel = 1.toFloat()

    fun init(x:Float, y:Float, screen_width:Float, screen_height:Float) {
        x_ = x
        y_ = y
        screen_width_ = screen_width
        screen_height_ = screen_height
    }

    fun animate(x1:Float, y1:Float, x2:Float, y2:Float) {
        val dist1 = Math.sqrt(Math.pow(x1-x_.toDouble(), 2.toDouble()) + Math.pow(y1-y_.toDouble(), 2.toDouble()))
        val dist2 = Math.sqrt(Math.pow(x2-x_.toDouble(), 2.toDouble()) + Math.pow(y2-y_.toDouble(), 2.toDouble()))

        if (dist1 <= dist2) {
            x_ += (x1-x_)/dist1.toFloat() * vel
            y_ += (y1-y_)/dist1.toFloat() * vel
        }
        else {
            x_ += (x2-x_)/dist2.toFloat()
            y_ += (y2-y_)/dist2.toFloat()
        }
    }

    fun doDraw(canvas: Canvas?, resources: Resources) {
        val paint = Paint()
        paint.setColor(resources.getColor(R.color.timmy))
        canvas!!.drawCircle(x_, y_, 4f, paint)
    }
}