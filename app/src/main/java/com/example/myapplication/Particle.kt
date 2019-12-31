package com.example.myapplication

import android.graphics.*
import android.util.Log

class Particle() {
    var framesLeft_:Int = 0

    private var x_:Double = 0.toDouble()
    private var y_:Double = 0.toDouble()
    private var xVel_:Double = 0.toDouble()
    private var yVel_:Double = 0.toDouble()
    private var color_:Int = Color.argb(0,255,255,255)
    private var colorvel_:Int = Color.argb(0, 0, 0, 0)
    private var gravity_:Double = 1.toDouble()
    private var resistance_:Double = 1.toDouble()

    private var next:Int? = null

    private var Speed : Int = 0

    fun init(x:Double, y:Double, xVel:Double, yVel:Double,
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
        //xVel_ -= resistance_
        //yVel_ += gravity_
        var dwindle = framesLeft_
        if (framesLeft_ > 0xff)
            dwindle = 0xff
        //color_ = color_.and(0xffffff).or(dwindle.and(0xff).shl(24))

        return framesLeft_ == 0
    }

    fun doDraw(canvas: Canvas?) {
        if (!inUse()) return
        val paint = Paint()
        paint.setColor(color_)
        canvas!!.drawCircle(x_.toFloat(), y_.toFloat(), 5f, paint)
    }

    fun undoDraw(canvas: Canvas?) {
        if (!inUse()) return
        val paint = Paint()
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.CLEAR))
        canvas!!.drawCircle(x_.toFloat(), y_.toFloat(), 5f, paint)
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