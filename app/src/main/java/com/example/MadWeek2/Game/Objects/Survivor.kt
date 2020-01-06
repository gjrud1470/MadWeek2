package com.example.MadWeek2.Game.Objects

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import com.example.MadWeek2.R

class Survivor {
    var x_:Float = 0.toFloat()
    var y_:Float = 0.toFloat()
    private var angle_:Float = 0.toFloat()
    private var image: Bitmap? = null
    private var screen_width_:Float = 0.toFloat()
    private var screen_height_:Float = 0.toFloat()
    private var id_:Int = 0

    fun init(x:Float, y:Float, angle:Float, screen_width:Float, screen_height:Float, id:Int) {
        x_ = x
        y_ = y
        angle_ = angle
        screen_width_ = screen_width
        screen_height_ = screen_height
        id_ = id
    }

    fun animate(xVel:Float, yVel:Float, turn:Float) {
        x_ = x_ + xVel
        y_ = y_ + yVel
        if (x_ < 0 || x_ > screen_width_)
            x_ -= xVel
        if (y_ < 0 || y_ > screen_width_)
            y_ -= yVel
        angle_ = angle_ + turn
    }

    fun doDraw(canvas: Canvas?, resources: Resources) {
        if (id_ == 1) image = BitmapFactory.decodeResource(resources, R.drawable.user_character1)
        else if (id_ == 2) image = BitmapFactory.decodeResource(resources, R.drawable.user_character2)
        val matrix = Matrix()
        matrix.postRotate(angle_)
        val rot_image = Bitmap.createBitmap(image!!, 0, 0, image!!.width, image!!.height, matrix, true)
        canvas!!.drawBitmap(rot_image, x_, y_, null)
    }
}