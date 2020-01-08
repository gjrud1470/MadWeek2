package com.example.MadWeek2.Game.Objects

import android.content.res.Resources
import android.graphics.*
import android.util.Log
import com.example.MadWeek2.R

class Alex : Hunter() {
    override var x_:Float = 0.toFloat()
    override var y_:Float = 0.toFloat()
    var xVel_:Float = 0.toFloat()
    var yVel_:Float = 0.toFloat()
    override var radius: Float = 10.toFloat()
    override var alive: Boolean = false

    private var screen_width_ = 0.toFloat()
    private var screen_height_ = 0.toFloat()

    private var vel = 0.2.toFloat()
    private var paint : Paint? = null

    private var next:Int? = null

    override var health: Int = 1

    override fun init(x:Float, y:Float, screen_width:Float, screen_height:Float, resources: Resources) {
        x_ = x
        y_ = y
        alive = true
        screen_width_ = screen_width
        screen_height_ = screen_height

        paint = Paint()
        paint!!.setColor(resources.getColor(R.color.alex))
    }

    override fun animate(x1:Float?, y1:Float?, x2:Float?, y2:Float?) {
        if (!inUse()) return

        var dist1 = 0.toDouble()
        var dist2 = 0.toDouble()

        var tempx = x_ + xVel_
        var tempy = y_ + yVel_
        if (x1 != null && y1 != null && x2 != null && y2 != null) {
            dist1 = Math.sqrt(
                Math.pow(x1 - x_.toDouble(), 2.toDouble()) + Math.pow(
                    y1 - y_.toDouble(),
                    2.toDouble()
                )
            )
            dist2 = Math.sqrt(
                Math.pow(x2 - x_.toDouble(), 2.toDouble()) + Math.pow(
                    y2 - y_.toDouble(),
                    2.toDouble()
                )
            )

            if (dist1 <= dist2) {
                xVel_ += (x1-x_)/dist1.toFloat() * vel
                yVel_ += (y1-y_)/dist1.toFloat() * vel
            }
            else {
                xVel_ += (x2-x_)/dist2.toFloat() * vel
                yVel_ += (y2-y_)/dist2.toFloat() * vel
            }
        }
        else if (x1 != null && y1 != null) {
            dist1 = Math.sqrt(
                Math.pow(x1 - x_.toDouble(), 2.toDouble()) + Math.pow(
                    y1 - y_.toDouble(),
                    2.toDouble()
                )
            )

            xVel_ += (x1-x_)/dist1.toFloat() * vel
            yVel_ += (y1-y_)/dist1.toFloat() * vel
        }
        else if (x2 != null && y2 != null) {
            dist2 = Math.sqrt(
                Math.pow(x2 - x_.toDouble(), 2.toDouble()) + Math.pow(
                    y2 - y_.toDouble(),
                    2.toDouble()
                )
            )

            xVel_ += (x2-x_)/dist2.toFloat() * vel
            yVel_ += (y2-y_)/dist2.toFloat() * vel
        }

        x_ = tempx
        y_ = tempy
    }

    override fun doDraw(canvas: Canvas?) {
        if (!inUse()) return
        if (inScreen()) {
            canvas!!.drawCircle(x_, y_, 20f, paint!!)
        }
    }

    override fun inUse() : Boolean {
        return alive
    }

    override fun inScreen() : Boolean {
        return x_ > 0 && x_ < screen_width_ && y_ > 0 && y_ < screen_height_
    }

    override fun getNext() : Int? {
        return next
    }

    override fun setNext(int : Int?) {
        next = int
    }

}