package com.example.MadWeek2

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.MadWeek2.R

class SplashActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        Handler().postDelayed(Runnable(){
            //startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}