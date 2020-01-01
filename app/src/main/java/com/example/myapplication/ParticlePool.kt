package com.example.myapplication

import android.graphics.Canvas
import android.util.Log

class ParticlePool {
    private val POOL_SIZE = 1000
    private var particles_ = Array<Particle>(POOL_SIZE, {i -> Particle()})
    private var first_available : Int? = 0

    init {
        for (i in IntArray(POOL_SIZE) {int -> int}) {
            particles_[i].setNext(i+1)
        }
        particles_[POOL_SIZE-1].setNext(null)
    }

    fun create (x:Double, y:Double, xVel:Double, yVel:Double, lifetime:Int, color:Int, trail:Boolean) {
        if (first_available != null) {
            val particle: Particle = particles_[first_available!!]
            particle.init(x, y, xVel, yVel, lifetime, color, trail)
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