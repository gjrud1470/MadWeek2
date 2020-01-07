package com.example.MadWeek2.Game.Objects

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import com.example.MadWeek2.R
import kotlin.math.cos
import kotlin.math.sin

class Survivor {
    var x_:Float = 0.toFloat()
    var y_:Float = 0.toFloat()
    private var xVel_:Float = 0.toFloat()
    private var yVel_:Float = 0.toFloat()
    var angle_:Float = 90.toFloat()
    private var turn:Float = 0.toFloat()
    private var image: Bitmap? = null
    private var screen_width_:Float = 0.toFloat()
    private var screen_height_:Float = 0.toFloat()
    private var id_:Int = 0

    fun init(x:Float, y:Float, angle:Float, screen_width:Float, screen_height:Float, id:Int, resources: Resources) {
        x_ = x
        y_ = y
        angle_ = angle % 360
        screen_width_ = screen_width - 100
        screen_height_ = screen_height - 100
        id_ = id

        if (id_ == 1) image = BitmapFactory.decodeResource(resources, R.drawable.user_character1)
        else if (id_ == 2) image = BitmapFactory.decodeResource(resources, R.drawable.user_character2)
        image = Bitmap.createScaledBitmap(image!!, 100, 100, true)
    }

    fun setmovement (angle: Float, strength: Float) {
        xVel_ = strength * cos(Math.toRadians(angle.toDouble())).toFloat()
        yVel_ = (-strength) * sin(Math.toRadians(angle.toDouble())).toFloat()
    }

    fun setshootangle (angle: Float) {
        val prov = angle - angle_
        var direction : Float? = null
        if (prov > 180)
            direction = prov - 360
        else if (prov <= -180)
            direction = prov + 360
        else
            direction = prov

        if (direction > 0)
            turn = 10.toFloat()
        else
            turn = (-10).toFloat()
    }

    fun animate() {
        x_ = x_ + xVel_
        y_ = y_ + yVel_
        if (x_ < 10) x_ = 10.toFloat()
        else if (x_ > screen_width_) x_ = screen_width_
        if (y_ < 10) y_ = 10.toFloat()
        else if (y_ > screen_height_) y_ = screen_height_
        angle_ += turn

        angle_ = (angle_.toInt() % 360).toFloat()
        turn = 0.toFloat()
    }

    fun doDraw(canvas: Canvas?) {
        //val matrix = Matrix()
        //matrix.postRotate(angle_)
        //var rot_image = Bitmap.createBitmap(image!!, 0, 0, image!!.width, image!!.height, matrix, true)
        //canvas!!.drawBitmap(rot_image, x_, y_, null)

        canvas!!.save()
        canvas.rotate(-angle_, x_+50, y_+50)
        canvas.drawBitmap(image!!, x_, y_, null)
        canvas.restore()
    }
}