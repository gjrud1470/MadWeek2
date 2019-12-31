package com.example.myapplication

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import androidx.core.graphics.rotationMatrix
import kotlin.random.Random


class Firework_particle {
    private var random = Random
    private var particle_pool = ParticlePool()
    private val default_energy = 10
    private val default_colors = arrayOf(R.color.red, R.color.yellow, R.color.green)
    private val default_number = 10

    private val energy_std = 1
    private val color_std = 50
    private val number_std = 10

    fun create(x:Double, y:Double) {
        val energy = default_energy + (random.nextDouble() * energy_std)
        val red = random.nextInt(256)
        val blue = random.nextInt(256)
        val green = random.nextInt(256)
        val base_color = Color.argb(255, red, blue, green)
        val fire_count = default_number + (random.nextInt(number_std))

        for (i in IntArray(fire_count) {int -> int}) {
            val angle = i * 360.toDouble()/fire_count + (random.nextDouble() * 360.toDouble()/(3*fire_count))
            val len = random.nextFloat() * energy
            val xVel = len * Math.cos(angle)
            val yVel = len * Math.sin(angle)
            val lifetime = (energy*2).toInt()
            val color = base_color

            particle_pool.create(x, y, xVel, yVel, lifetime, color)
            Log.wtf("fireworkparticls", "particle created at ${xVel}, ${yVel} with lifetime ${lifetime}, of color ${color} of angle ${angle}")
        }
    }

    fun animate() {
        particle_pool.animate()
    }

    fun doDraw(canvas: Canvas?) {
        particle_pool.doDraw(canvas)
    }

    fun undoDraw(canvas: Canvas?) {
        particle_pool.undoDraw(canvas)
    }
}