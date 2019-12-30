package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ImageInformation : AppCompatActivity() {

    private val TAG = "ImageInformation"
    private var image = ImageItem(null, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        var pos = intent.getIntExtra("POS", 0)
        image = ImageHolder.getDataById(pos)
    }
}