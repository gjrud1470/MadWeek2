package com.example.MadWeek2.Game.Objects

import android.content.res.Resources
import android.graphics.Canvas
import kotlin.math.abs

class Bullet_pool (resources: Resources) {
    private val POOL_SIZE = 30
    private var bullets_ = Array<Bullet>(POOL_SIZE, { i -> Bullet() })
    private var first_available : Int? = 0
    private var resources_ = resources

    private var radius = 8

    init {
        for (i in 0..POOL_SIZE-1) {
            bullets_[i].setNext(i+1)
        }
        bullets_[POOL_SIZE-1].setNext(null)
    }

    fun create (x:Float, y:Float, screen_width:Float, screen_height:Float, angle:Float) {
        if (first_available != null) {
            val particle: Bullet = bullets_[first_available!!]
            particle.init(x, y, screen_width, screen_height, angle, resources_)
            first_available = particle.getNext()
        }
    }

    fun animate() {
        for (i in IntArray(POOL_SIZE) {int -> int}) {
            if (bullets_[i].animate()) {
                bullets_[i].setNext(first_available)
                first_available = i
            }
        }
    }

    fun doDraw(canvas: Canvas?) {
        for (i in IntArray(POOL_SIZE) {int -> int}) {
            bullets_[i].doDraw(canvas)
        }
    }

    fun check_killed (hunter: Hunter) : Boolean {
        var dist = 0.toDouble()

        for (i in 0..POOL_SIZE-1) {
            if (bullets_[i].inUse()) {
                dist = Math.sqrt(
                    Math.pow(bullets_[i].x_ - hunter.x_.toDouble(), 2.toDouble())
                            + Math.pow(bullets_[i].y_ - hunter.y_.toDouble(), 2.toDouble())
                )
                if (dist < radius + hunter.radius) return true
            }
        }
        return false
                    /*
                && abs(bullets_[i].x_ - hunter.x_) < radius + hunter.radius
                && abs(bullets_[i].y_ - hunter.y_) < radius + hunter.radius) {
                return true
            }
        }
        return false*/

    }

}