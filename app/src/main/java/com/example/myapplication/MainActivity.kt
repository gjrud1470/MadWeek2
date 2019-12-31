package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.ui.main.SectionsPagerAdapter
import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import androidx.core.view.MotionEventCompat


class MainActivity : AppCompatActivity() {

    val DEBUG_TAG : String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()

        var intent : Intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)

        startapp()
    }

    private fun startapp() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.offscreenPageLimit = 4
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = this.findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

    private val PERM_REQUEST_CODE = 100

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupPermissions(){
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE
        )

        if (!hasPermissions(this, permissions)) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE),
            PERM_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERM_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.i(DEBUG_TAG, "Permission has been granted by user")
                    startapp()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i(DEBUG_TAG, "Permission has been denied by user")
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }

}