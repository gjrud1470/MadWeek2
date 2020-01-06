package com.example.MadWeek2

import android.graphics.*

class Fire_Particle() {
    var framesLeft_:Int = 0

    private var x_:Double = 0.toDouble()
    private var y_:Double = 0.toDouble()
    private var xVel_:Double = 0.toDouble()
    private var yVel_:Double = 0.toDouble()
    private var color_:Int = Color.argb(0,255,255,255)
    private var dwindle_int :Int = 0
    private var trail_ :Boolean = false

    private var particle_path :Path? = null
    private var path_color :Int = Color.argb(0, 255, 255, 255)

    private var white_mark :Int = 200

    private var gravity_:Double = 0.2
    private var resistance_:Double = 0.9
    private var next:Int? = null

    fun init(x:Double, y:Double, xVel:Double, yVel:Double,
             lifetime:Int, color:Int, trail:Boolean) {
        x_ = x
        y_ = y
        xVel_ = xVel
        yVel_ = yVel
        framesLeft_ = lifetime
        color_ = color
        trail_ = trail

        if (trail_) {
            particle_path = Path()
            particle_path!!.moveTo(x.toFloat(), y.toFloat())
            path_color = dwindle_color(color_)
        }
    }

    fun animate() : Boolean {
        if (!inUse()) return false
        framesLeft_--
        x_ = x_ + xVel_
        y_ = y_ + yVel_
        xVel_ = resistance_ * xVel_
        yVel_ = resistance_ * yVel_ + gravity_

        var temp = color_
        color_ = dwindle_color(temp)

        if (trail_ && particle_path != null) {
            temp = color_
            particle_path!!.lineTo(x_.toFloat(), y_.toFloat())
            path_color = dwindle_color(temp)
        }

        if (framesLeft_ == 0 && trail_ && particle_path != null)
            particle_path!!.rewind()
        return framesLeft_ == 0
    }

    fun doDraw(canvas: Canvas?) {
        if (!inUse()) return
        val paint = Paint()
        paint.setColor(color_)
        canvas!!.drawCircle(x_.toFloat(), y_.toFloat(), 4f, paint)
        paint.setColor(Color.argb(white_mark, 255, 255, 255))
        canvas.drawCircle(x_.toFloat(), y_.toFloat(), 2f, paint)

        if (trail_ && particle_path != null) {
            val path_paint = Paint()
            path_paint.style = Paint.Style.STROKE
            path_paint.strokeWidth = 4f
            path_paint.setColor(path_color)
            canvas.drawPath(particle_path!!, path_paint)
            path_paint.strokeWidth = 1f
            path_paint.setColor(Color.argb(white_mark, 255, 255, 255))
            canvas.drawPath(particle_path!!, path_paint)
        }
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

    fun dwindle_color(color: Int) : Int {
        return color
        /*
        var result_color : Int

        if (color.shr(24).and(0xFF) >= dwindle_int)
            result_color = Color.argb(color.shr(24).and(0xFF) - dwindle_int,
                color.shr(16).and(0xFF),
                color.shr(8).and(0xFF),
                color.and(0xFF))
        else
            result_color = Color.argb(0, 255, 255, 255)

        return result_color
        */
    }
}