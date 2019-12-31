package com.example.myapplication

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import androidx.core.graphics.rotationMatrix
import kotlin.random.Random


class Firework_particle {
    private var random = Random
    private var particle_pool = ParticlePool()
    private val default_energy = 200
    private val default_colors = arrayOf(R.color.red, R.color.yellow, R.color.green)
    private val default_number = 10

    private val energy_std = 50
    private val color_std = 50
    private val number_std = 10

    fun create(x:Float, y:Float) {
        val energy = default_energy + (random.nextFloat() * energy_std)
        val red = random.nextInt(256)
        val blue = random.nextInt(256)
        val green = random.nextInt(256)
        val base_color = Color.argb(255, red, blue, green)
        val fire_count = default_number + (random.nextInt(number_std))

        for (i in IntArray(fire_count)) {
            val angle = i * 360.toFloat()/fire_count + (random.nextFloat() * 360.toFloat()/(3*fire_count))
            val vector_array = floatArrayOf(0.toFloat(), 0.toFloat())
            rotationMatrix(angle, x.toFloat(), y.toFloat()).mapPoints(vector_array, floatArrayOf(energy.toFloat(), 0.toFloat()))
            val lifetime = (energy * 10).toInt()
            val color = base_color

            particle_pool.create(x, y, vector_array[0].toFloat(), vector_array[1].toFloat(), lifetime, color)
        }
    }

    fun animate() {
        particle_pool.animate()
    }

    fun doDraw(canvas: Canvas?) {
        particle_pool.doDraw(canvas)
    }
}