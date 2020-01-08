package com.example.MadWeek2.Game.Objects

import android.content.res.Resources
import android.graphics.*
import android.util.Log
import com.example.MadWeek2.R

class Bob : Hunter() {
    override var x_:Float = 0.toFloat()
    override var y_:Float = 0.toFloat()
    override var radius: Float = 200.toFloat()
    override var alive: Boolean = false

    private var screen_width_ = 0.toFloat()
    private var screen_height_ = 0.toFloat()

    private var vel = 1.toFloat()
    private var paint : Paint? = null

    private var next:Int? = null

    override var health: Int = 50

    override fun init(x:Float, y:Float, screen_width:Float, screen_height:Float, resources: Resources) {
        x_ = x
        y_ = y
        alive = true
        screen_width_ = screen_width
        screen_height_ = screen_height

        paint = Paint()
        paint!!.setColor(resources.getColor(R.color.bob))
    }

    override fun animate(x1:Float?, y1:Float?, x2:Float?, y2:Float?) {
        if (!inUse()) return

        var dist1 = 0.toDouble()
        var dist2 = 0.toDouble()

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
                x_ += (x1-x_)/dist1.toFloat() * vel
                y_ += (y1-y_)/dist1.toFloat() * vel
            }
            else {
                x_ += (x2-x_)/dist2.toFloat() * vel
                y_ += (y2-y_)/dist2.toFloat() * vel
            }
        }
        else if (x1 != null && y1 != null) {
            dist1 = Math.sqrt(
                Math.pow(x1 - x_.toDouble(), 2.toDouble()) + Math.pow(
                    y1 - y_.toDouble(),
                    2.toDouble()
                )
            )

            x_ += (x1-x_)/dist1.toFloat() * vel
            y_ += (y1-y_)/dist1.toFloat() * vel
        }
        else if (x2 != null && y2 != null) {
            dist2 = Math.sqrt(
                Math.pow(x2 - x_.toDouble(), 2.toDouble()) + Math.pow(
                    y2 - y_.toDouble(),
                    2.toDouble()
                )
            )

            x_ += (x2-x_)/dist2.toFloat() * vel
            y_ += (y2-y_)/dist2.toFloat() * vel
        }
    }

    override fun doDraw(canvas: Canvas?) {
        if (!inUse()) return
        if (inScreen()) {
            canvas!!.drawCircle(x_, y_, 200f, paint!!)
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