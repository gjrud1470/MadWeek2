package com.example.MadWeek2.Game.Objects

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import com.example.MadWeek2.R
import kotlin.math.cos
import kotlin.math.sin

class Bullet {
    private var x_:Float = 0.toFloat()
    private var y_:Float = 0.toFloat()
    private var angle_:Float = 0.toFloat()
    private var screen_width_:Float = 0.toFloat()
    private var screen_height_:Float = 0.toFloat()
    private var vel = 1.toFloat()

    fun init(x:Float, y:Float, screen_width:Float, screen_height:Float, angle:Float) {
        x_ = x
        y_ = y
        screen_width_ = screen_width
        screen_height_ = screen_height
        angle_ = angle
    }

    fun animate() {
        x_ += cos(angle_) * vel
        y_ += sin(angle_) * vel
    }

    fun doDraw(canvas: Canvas?, resources: Resources) {
        val paint = Paint()
        paint.setColor(resources.getColor(R.color.bullet))
        canvas!!.drawCircle(x_, y_, 2f, paint)
    }
}