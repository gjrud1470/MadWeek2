package com.example.MadWeek2.Game.Objects

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import com.example.MadWeek2.R
import kotlin.math.cos
import kotlin.math.sin

class Bullet {
    var x_:Float = 0.toFloat()
    var y_:Float = 0.toFloat()
    private var xVel_:Float = 0.toFloat()
    private var yVel_:Float = 0.toFloat()
    private var screen_width_:Float = 0.toFloat()
    private var screen_height_:Float = 0.toFloat()
    private var vel = 30.toFloat()
    private var paint : Paint? = null

    private var next:Int? = null

    fun init(x:Float, y:Float, screen_width:Float, screen_height:Float, angle:Float, resources: Resources) {
        x_ = x
        y_ = y
        screen_width_ = screen_width
        screen_height_ = screen_height
        xVel_ = cos(Math.toRadians(angle.toDouble())).toFloat() * vel
        yVel_ = -sin(Math.toRadians(angle.toDouble())).toFloat() * vel
        paint = Paint()
        paint!!.setColor(resources.getColor(R.color.bullet))
    }

    fun animate() : Boolean {
        if (!inUse()) return false
        x_ += xVel_
        y_ += yVel_

        return !inUse()
    }

    fun doDraw(canvas: Canvas?) {
        if (!inUse()) return
        canvas?.drawCircle(x_-4, y_-4, 8f, paint!!)
    }

    fun inUse() : Boolean {
        return x_ > 0 && x_ < screen_width_ && y_ > 0 && y_ < screen_height_
    }

    fun getNext() : Int? {
        return next
    }

    fun setNext(int : Int?) {
        next = int
    }
}