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
    abstract var health : Int

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
                1 -> Alex()
                2 -> Bob()
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

            if (bulletPool_.check_killed(hunters[i])) {
                hunters[i].alive = false
                hunters[i].setNext(first_available)
                first_available = i
                return 0
            }

            hit_id = check_reached(hunters[i], x1, y1, x2, y2)
            if (hit_id != 0) {
                hunters[i].alive = false
                hunters[i].setNext(first_available)
                first_available = i
                return hit_id
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

        var dist1 = 0.toDouble()
        var dist2 = 0.toDouble()
        if (x1 != null && y1 != null) {
            dist1 = Math.sqrt(Math.pow(x1 - hunter.x_.toDouble(), 2.toDouble())
                    + Math.pow(y1 - hunter.y_.toDouble(), 2.toDouble()))
            if (dist1 < survivor_radius + hunter.radius) return 1
        }
        if (x2 != null && y2 != null) {
            dist2 = Math.sqrt(Math.pow(x2 - hunter.x_.toDouble(), 2.toDouble())
                    + Math.pow(y2 - hunter.y_.toDouble(), 2.toDouble()))
            if (dist2 < survivor_radius + hunter.radius) return 2
        }
        return 0

        /*
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
         */
    }

}