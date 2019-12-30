package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mikhaellopez.circularimageview.CircularImageView

class ContactInformation : AppCompatActivity() {

    private val TAG = "ContactInformation"
    private var contact = ContactModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_information)

        SetupView()
        SetupButtons()
    }

    private fun SetupView() {
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

    private fun SetupButtons() {
        var call_icon_view : ImageView = findViewById(R.id.info_button_call)

        call_icon_view.setOnClickListener { v ->
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent (Intent.ACTION_CALL, Uri.fromParts("tel", contact.mobileNumber, null))
                startActivity(intent)
            }
            else {
                val intent = Intent (Intent.ACTION_DIAL, Uri.fromParts("tel", contact.mobileNumber, null))
                startActivity(intent)
            }
        }

        var text_icon_view : ImageView = findViewById(R.id.info_button_text)
        text_icon_view.setOnClickListener { v ->
            val intent = Intent (Intent.ACTION_VIEW)
            intent.setData(Uri.parse("sms:${contact.mobileNumber}"))
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
