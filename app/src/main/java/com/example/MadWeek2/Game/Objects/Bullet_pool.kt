package com.example.MadWeek2.Game.Objects

import android.content.res.Resources
import android.graphics.Canvas
import android.util.Log

class Bullet_pool (resources: Resources) {
    private val POOL_SIZE = 100
    private var particles_ = Array<Bullet>(POOL_SIZE, { i -> Bullet() })
    private var first_available : Int? = 0
    private var resources_ = resources

    init {
        for (i in 0..POOL_SIZE-1) {
            particles_[i].setNext(i+1)
        }
        particles_[POOL_SIZE-1].setNext(null)
    }

    fun create (x:Float, y:Float, screen_width:Float, screen_height:Float, angle:Float) {
        if (first_available != null) {
            val particle: Bullet = particles_[first_available!!]
            particle.init(x, y, screen_width, screen_height, angle, resources_)
            first_available = particle.getNext()
        }
    }

    fun animate() {
        for (i in IntArray(POOL_SIZE) {int -> int}) {
            if (particles_[i].animate()) {
                particles_[i].setNext(first_available)
                first_available = i
            }
        }
    }

    fun doDraw(canvas: Canvas?) {
        for (i in IntArray(POOL_SIZE) {int -> int}) {
            particles_[i].doDraw(canvas)
        }
    }

}