package com.example.MadWeek2

import android.graphics.*

class Particle() {
    var framesLeft_:Int = 0

    private var x_:Double = 0.toDouble()
    private var y_:Double = 0.toDouble()
    private var xVel_:Double = 0.toDouble()
    private var yVel_:Double = 0.toDouble()
    private var id_:Int = 0

    private var next:Int? = null

    fun init(x:Double, y:Double, xVel:Double, yVel:Double,
             lifetime:Int, id:Int) {
        x_ = x
        y_ = y
        xVel_ = xVel
        yVel_ = yVel
        framesLeft_ = lifetime
        id_ = id
    }

    fun animate() : Boolean {
        if (!inUse()) return false
        framesLeft_--
        x_ = x_ + xVel_
        y_ = y_ + yVel_

        return framesLeft_ == 0
    }

    fun doDraw(canvas: Canvas?) {

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