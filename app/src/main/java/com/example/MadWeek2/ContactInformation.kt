package com.example.MadWeek2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.mikhaellopez.circularimageview.CircularImageView
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class ContactInformation : AppCompatActivity() {

    private val TAG = "ContactInformation"
    private var contact = ContactModel()

    var pos = -1

    val REQUEST_EDIT = 1

    val RESULT_SAVED = 1
    val RESULT_DELETED = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_information)

        pos = intent.getIntExtra("POS", -1)
        contact = ContactHolder.getDataById(pos)

        SetupView()
        SetupButtons()
    }

    private fun SetupView() {
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

        val back_button = findViewById<ImageButton>(R.id.back_button)
        back_button.setOnClickListener {
            onBackPressed()
        }

    }

    fun showPopup (v : View) {
        val popupMenu = PopupMenu (this, v)
        popupMenu.menuInflater.inflate(R.menu.contact_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.action_edit -> {
                    var intent = Intent(this, Contact_edit::class.java)
                    intent.putExtra("pos", pos)
                    startActivityForResult(intent, REQUEST_EDIT)
                }
                R.id.action_share ->
                    Toast.makeText(this@ContactInformation, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                R.id.action_delete -> {
                    ContactHolder.getDataList().remove(contact)
                    setResult(RESULT_DELETED, intent)
                    finish()
                }
            }
            true
        })
        popupMenu.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_EDIT) {
            if (resultCode == RESULT_SAVED) {
                contact = ContactHolder.getDataById(pos)
                SetupView()
                setResult(RESULT_SAVED, intent)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
