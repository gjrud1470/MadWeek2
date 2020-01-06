package com.example.MadWeek2

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.rengwuxian.materialedittext.MaterialEditText
import kotlinx.android.synthetic.main.contact_edit_layout.*

class Contact_edit : AppCompatActivity() {

    var contact = ContactModel()
    var edit = false

    val RESULT_CANCELED = 0
    val RESULT_SAVED = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact_edit_layout)

        var pos = intent.getIntExtra("pos", -1)

        if (pos != -1) {
            edit = true
            var contact_original = ContactHolder.getDataById(pos)
            contact_edit_layout.findViewById<MaterialEditText>(R.id.edit_info_name).setText(contact_original.name)
            contact_edit_layout.findViewById<MaterialEditText>(R.id.edit_info_phonenumber).setText(contact_original.mobileNumber)
            contact_edit_layout.findViewById<MaterialEditText>(R.id.edit_info_group).setText(contact_original.group)
            contact.photo = contact_original.photo
        }

        save_contact.setOnClickListener {
            val input_name = contact_edit_layout.findViewById<MaterialEditText>(R.id.edit_info_name)
            val input_phone = contact_edit_layout.findViewById<MaterialEditText>(R.id.edit_info_phonenumber)
            val input_group = contact_edit_layout.findViewById<MaterialEditText>(R.id.edit_info_group)

            if (TextUtils.isEmpty(input_name.text.toString())
                || TextUtils.isEmpty(input_phone.text.toString())) {
                Toast.makeText(this@Contact_edit, "이름과 전화번호를 입력해 주세요" + input_name.text.toString() + input_phone.text.toString(), Toast.LENGTH_SHORT).show()
            }
            else {
                contact.name = input_name.text.toString()
                contact.mobileNumber = input_phone.text.toString()
                if (!TextUtils.isEmpty(input_group.text.toString()))
                    contact.group = input_group.text.toString()

                if (!edit) {
                    ContactHolder.getDataList().add(contact)
                }
                else {
                    ContactHolder.getDataList()[pos] = contact
                }
                ContactHolder.sortData()
                setResult(RESULT_SAVED, intent)
                finish()
            }
        }

        cancel_save_contact.setOnClickListener {
            setResult(RESULT_CANCELED, intent)
            finish()
        }
    }
}