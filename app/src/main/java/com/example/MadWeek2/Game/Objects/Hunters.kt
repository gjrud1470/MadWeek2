package com.example.MadWeek2.Game.Objects

import android.content.res.Resources
import android.graphics.Canvas
import android.util.Log
import kotlin.math.abs

abstract class Hunter {
    abstract var x_ : Float
    abstract var y_ : Float
    abstract var radius : Float
    abstract var alive : Boolean

    abstract fun init(x:Float, y:Float, screen_width:Float, screen_height:Float, resources: Resources)
    abstract fun animate(x1:Float?, y1:Float?, x2:Float?, y2:Float?)
    abstract fun doDraw(canvas: Canvas?)
    abstract fun inUse() : Boolean
    abstract fun inScreen() : Boolean
    abstract fun getNext() : Int?
    abstract fun setNext(int : Int?)
}

class Hunter_pool (resources: Resources, bulletPool: Bullet_pool) {
    private val POOL_SIZE = 100
    private var hunters = Array<Hunter>(POOL_SIZE) {Timmy()}
    private var first_available : Int? = 0
    private var resources_ = resources
    private var bulletPool_ = bulletPool

    private var survivor_radius = 40

    init {
        for (i in 0..POOL_SIZE-1) {
            hunters[i].setNext(i+1)
        }
        hunters[POOL_SIZE-1].setNext(null)
    }

    fun create (x:Float, y:Float,  screen_width:Float, screen_height:Float, id: Int) {
        if (first_available != null) {
            val next_available = hunters[first_available!!].getNext()
            val hunter = when (id) {
                0 -> Timmy()
                else -> Timmy()
            }
            hunters[first_available!!] = hunter
            hunter.init(x, y, screen_width, screen_height, resources_)
            hunter.setNext(next_available)
            first_available = hunter.getNext()
        }
    }

    fun animate(x1:Float?, y1:Float?, x2:Float?, y2:Float?) : Int {
        var hit_id = 0
        for (i in IntArray(POOL_SIZE) {int -> int}) {
            hunters[i].animate(x1, y1, x2, y2)
            hit_id = check_reached(hunters[i], x1, y1, x2, y2)
            if (hit_id != 0) {
                return hit_id
            }
            if (bulletPool_.check_killed(hunters[i])) {
                hunters[i].alive = false
                hunters[i].setNext(first_available)
                first_available = i
            }
        }
        return hit_id
    }

    fun doDraw(canvas: Canvas?) {
        for (i in IntArray(POOL_SIZE) {int -> int}) {
            hunters[i].doDraw(canvas)
        }
    }

    fun check_reached (hunter: Hunter, x1:Float?, y1:Float?, x2:Float?, y2:Float?) : Int {
        if (x1 != null && y1 != null
            && abs(hunter.x_ - x1) < survivor_radius + hunter.radius
            && abs(hunter.y_ - y1) < survivor_radius + hunter.radius) {
            return 1
        }
        else if (x2 != null && y2 != null
            && abs(hunter.x_ - x2) < survivor_radius + hunter.radius
            && abs(hunter.y_ - y2) < survivor_radius + hunter.radius) {
            return 2
        }
        return 0
    }

}