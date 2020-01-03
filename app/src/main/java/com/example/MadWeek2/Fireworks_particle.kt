package com.example.MadWeek2

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.graphics.rotationMatrix
import kotlin.coroutines.coroutineContext
import kotlin.random.Random


class Firework_particle {
    private val random = Random
    private val particle_pool = ParticlePool()

    private val default_energy = 7
    private val default_move_energy = 5
    private val default_colors = arrayOf(0xFF0000, 0xFFCC00, 0x00FF00, 0x00CCFF)
    private val default_number = 25
    private val default_move_number = 15
    private val default_lifetime = 5

    private val energy_std = 10
    private val energy_move_std = 3
    private val color_std = 50
    private val number_std = 10

    fun create(x:Double, y:Double, move:Boolean) {
        var large_boom = false
        var boom_count = 1

        if (!move && random.nextInt(30) == 0) {
            large_boom = true
            boom_count = 2
        }
        if (!move && random.nextInt(5) == 0)
            boom_count = 2

        for (i in IntArray(boom_count) {int -> int}) {
            var energy = random.nextDouble()
            var trail = false
            var lifetime = 0

            if (!move) {
                if (large_boom) {
                    energy = (energy * energy_std) + (default_energy * 2)
                } else
                    energy = energy * energy_std + default_energy
                trail = true
                lifetime = (energy * 3).toInt() + default_lifetime
            } else {
                energy = energy * energy_move_std + default_move_energy
                lifetime = (energy * 2).toInt()
            }

            var fire_count = (energy / 2).toInt()
            if (!move) {
                if (large_boom) {
                    fire_count = 2 * energy.toInt()
                }
                fire_count += default_number
            } else
                fire_count += default_move_number

            var base_color: Int
            if (large_boom) {
                val color_index = random.nextInt(4)
                val color_sample = default_colors[color_index]
                val red = random.nextInt(51)
                val green = random.nextInt(51)
                val blue = random.nextInt(51)

                base_color = when (color_index) {
                    0 -> Color.argb(
                        255,
                        color_sample.red - red,
                        color_sample.green + green,
                        color_sample.blue + blue
                    )  //red FF0000
                    1 -> Color.argb(
                        255,
                        color_sample.red - red,
                        color_sample.green + green,
                        color_sample.blue + blue
                    )  //yellow FFCC00
                    2 -> Color.argb(
                        255,
                        color_sample.red + red,
                        color_sample.green - green,
                        color_sample.blue + blue
                    )  //green 00FF00
                    3 -> Color.argb(
                        255,
                        color_sample.red + red,
                        color_sample.green + green,
                        color_sample.blue - blue
                    )  //skyblue 00CCFF
                    else -> Color.argb(
                        255,
                        random.nextInt(256),
                        random.nextInt(256),
                        random.nextInt(256)
                    )
                }
            } else {
                base_color =
                    Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
            }

            for (i in IntArray(fire_count) { int -> int }) {
                var angle =
                    i * 360.toDouble() / (fire_count) + (random.nextDouble() * 360.toDouble() / (2 * fire_count))
                val len = energy + (random.nextFloat() * energy / 2) //random.nextFloat() * energy
                val xVel = len * Math.cos(angle)
                val yVel = len * Math.sin(angle)

                particle_pool.create(x, y, xVel, yVel, lifetime, base_color, trail)
            }
        }
    }

    fun animate() {
        particle_pool.animate()
    }

    fun doDraw(canvas: Canvas?) {
        particle_pool.doDraw(canvas)
    }
}