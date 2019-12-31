package com.example.myapplication

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Particle() {
    var framesLeft_:Int = 0

    private var x_:Float = 0.toFloat()
    private var y_:Float = 0.toFloat()
    private var xVel_:Float = 0.toFloat()
    private var yVel_:Float = 0.toFloat()
    private var color_:Int = Color.argb(0,255,255,255)
    private var colorvel_:Int = Color.argb(0, 0, 0, 0)
    private var gravity_:Float = 1.toFloat()
    private var resistance_:Float = 1.toFloat()

    private var next:Int? = null

    private var Speed : Int = 0

    fun init(x:Float, y:Float, xVel:Float, yVel:Float,
             lifetime:Int, color:Int) {
        x_ = x
        y_ = y
        xVel_ = xVel
        yVel_ = yVel
        framesLeft_ = lifetime
        color_ = color
    }

    fun animate() : Boolean {
        if (!inUse()) return false
        framesLeft_--
        x_ += xVel_
        y_ += yVel_
        xVel_ -= resistance_
        yVel_ -= gravity_
        var dwindle = framesLeft_
        if (framesLeft_ > 0xff)
            dwindle = 0xff
        color_ = color_.and(0xffffff).or(dwindle.and(0xff).shl(24))

        return framesLeft_ == 0
    }

    fun doDraw(canvas: Canvas?) {
        val paint = Paint()
        paint.setColor(color_)
        canvas!!.drawPoint(x_, y_, paint)
    }

    fun inUse() : Boolean {
        return framesLeft_ > 0
    }

    fun getNext() : Int? {
        return next
    }

    fun setNext(int : Int?) {
        next = int
    }
}