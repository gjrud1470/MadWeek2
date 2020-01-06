package com.example.MadWeek2

import android.Manifest
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.MadWeek2.ui.main.SectionsPagerAdapter
import android.content.Intent
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.view.MotionEventCompat
import android.util.Log
import com.example.MadWeek2.R


class MainActivity : AppCompatActivity() {

    val DEBUG_TAG : String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)   //activity_main

        var intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)

        startapp()
    }

    private fun startapp() {
        val sectionsPagerAdapter =
            SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: nonSwipeViewPager = findViewById(R.id.view_pager)
        viewPager.offscreenPageLimit = 4
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = this.findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

}

class nonSwipeViewPager(context: Context, attributeSet: AttributeSet): ViewPager(context, attributeSet) {
    var enable: Boolean = false

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if(enable){
            return super.onInterceptTouchEvent(ev)
        }
        else{
            if(MotionEventCompat.getActionMasked(ev)==MotionEvent.ACTION_MOVE){
            }
            else{
                if(super.onInterceptTouchEvent(ev)){
                    super.onTouchEvent(ev)
                }
            }
            return false
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean{
        if(enable){
            return super.onTouchEvent(ev)
        }
        else{
            return MotionEventCompat.getActionMasked(ev) != MotionEvent.ACTION_MOVE && super.onTouchEvent(ev)
        }
    }
}
