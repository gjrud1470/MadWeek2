package com.example.myapplication

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ImageInformation : AppCompatActivity() {

    private val TAG = "ImageInformation"
    private var thisImage = ImageItem(null, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        var pos = intent.getIntExtra("POS", 0)
        thisImage = ImageHolder.getDataById(pos)

        var imageInfo : ImageView = findViewById(R.id.thumbnail)
        imageInfo.setImageBitmap(thisImage.image)

        var titleInfo : TextView = findViewById(R.id.title)
        titleInfo.text = "This is "+ thisImage.title
    }
}