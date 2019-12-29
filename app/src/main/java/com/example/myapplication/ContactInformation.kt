package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import com.mikhaellopez.circularimageview.CircularImageView

class ContactInformation : AppCompatActivity() {

    private val TAG = "ContactInformation"
    private var contact = ContactModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_information)

        var pos = intent.getIntExtra("POS", 0)
        contact = ContactHolder.getDataById(pos)

        var PhotoView: CircularImageView = findViewById(R.id.info_photo)
        PhotoView.setImageBitmap(contact.photo)

        var NameText: TextView = findViewById(R.id.info_name)
        NameText.text = contact.name

        var MobileText: TextView = findViewById(R.id.info_phonenumber)
        var MobileText2: TextView = findViewById(R.id.info_phonenumber2)
        MobileText.text = contact.mobileNumber
        MobileText2.text = contact.mobileNumber

        var GroupText: TextView = findViewById(R.id.info_group)
        GroupText.text = contact.group
    }
}
