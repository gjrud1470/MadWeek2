package com.example.myapplication

import android.graphics.Canvas

class ParticlePool {
    private val POOL_SIZE = 100
    private var particles_ = Array<Particle>(POOL_SIZE, {i -> Particle()})
    private var first_available : Int = 0

    init {
        for (i in IntArray(POOL_SIZE)) {
            particles_[i].setNext(i+1)
        }
        particles_[POOL_SIZE-1].setNext(null)
    }

    fun create (x:Float, y:Float, xVel:Float, yVel:Float, lifetime:Int, color:Int) {
        val particle:Particle = particles_[first_available]
        particle.init(x, y, xVel, yVel, lifetime, color)
        first_available = particle.getNext() as Int
    }

    fun animate() {
        for (i in IntArray(POOL_SIZE)) {
            if (particles_[i].animate()) {
                particles_[i].setNext(first_available)
                first_available = i
            }
        }
    }

    fun doDraw(canvas: Canvas?) {
        for (i in IntArray(POOL_SIZE)) {
            particles_[i].doDraw(canvas)
        }
    }
}