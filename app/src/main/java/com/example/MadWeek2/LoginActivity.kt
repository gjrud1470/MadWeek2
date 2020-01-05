package com.example.MadWeek2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.example.MadWeek2.Retrofit.LoginService
import com.example.MadWeek2.Retrofit.RetrofitClient
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.rengwuxian.materialedittext.MaterialEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.coroutines.delay
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    val DEBUG_TAG = "Login Activity"

    lateinit var iMyService: LoginService
    internal var compositeDisposable = CompositeDisposable()

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        // Setup Permissions
        setupPermissions()
    }

    private fun run_login() {
        //Init API
        val retrofit = RetrofitClient.getInstance()
        if (retrofit != null) {
            iMyService = retrofit.create(LoginService::class.java)

            try_pre_login()
            start_login()
        }
        else {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun try_pre_login() {
        var salt = find_login_info()
        if (salt != null && salt.isNotEmpty()) {
            preloginUser(salt)
        }
    }

    private fun start_login() {
        //event
        btn_login.setOnClickListener {
            loginUser(input_email.text.toString(), input_password.text.toString())
        }

        link_signup.setOnClickListener {
            val itemView = LayoutInflater.from(this)
                .inflate(R.layout.register_activity, null)

            MaterialStyledDialog.Builder(this)
                .setIcon(R.drawable.ic_user)
                .setTitle("REGISTRATION")
                .setDescription("Please fill all fields")
                //.setHeaderDrawable(R.drawable.duck_pond)
                .setCustomView(itemView)
                .setNegativeText("CANCEL")
                .onNegative {dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveText("REGISTER")
                .setCancelable(true)
                .setScrollable(true)
                .withDialogAnimation(true)
                .onPositive (
                    MaterialDialog.SingleButtonCallback { _, _ ->
                    val input_email = itemView.findViewById<View>(R.id.input_email) as MaterialEditText
                    val input_name = itemView.findViewById<View>(R.id.input_name) as MaterialEditText
                    val input_password = itemView.findViewById<View>(R.id.input_password) as MaterialEditText

                    if (TextUtils.isEmpty(input_name.text.toString())) {
                        Toast.makeText(this@LoginActivity, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        return@SingleButtonCallback
                    }
                        if (TextUtils.isEmpty(input_email.text.toString())) {
                            Toast.makeText(this@LoginActivity, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                            return@SingleButtonCallback
                        }
                        if (TextUtils.isEmpty(input_password.text.toString())) {
                            Toast.makeText(this@LoginActivity, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                            return@SingleButtonCallback
                        }


                        registerUser(input_name.text.toString(), input_email.text.toString(), input_password.text.toString())
                })
                .show()
        }
    }

    private fun preloginUser (salt: String) {
        val loading_view = LayoutInflater.from(this)
            .inflate(R.layout.loading_layout, null)
        login_page.addView(loading_view)

        compositeDisposable.add(iMyService.preloginUser(salt)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                val json_object = JSONObject(result)
                val success = json_object.getString("login_success")

                Handler().postDelayed(Runnable(){
                    login_page.removeView(loading_view)
                }, 1000)

                if (success == "success") {
                    // Start Main Activity
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()    //finish activity just in case
                }
            })
    }

    private fun loginUser (email: String, password: String) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this@LoginActivity, "Please fill in email", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this@LoginActivity, "Please fill in password", Toast.LENGTH_SHORT).show()
            return
        }

        val loading_view = LayoutInflater.from(this)
            .inflate(R.layout.loading_layout, null)

        login_page.addView(loading_view)

        compositeDisposable.add(iMyService.loginUser(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result->
                val json_object = JSONObject(result)
                val success = json_object.getString("login_success")

                Handler().postDelayed(Runnable(){
                    login_page.removeView(loading_view)
                }, 2000)

                if (success == "success") {
                    val salt = json_object.getString("salt")
                    save_login_info(salt)

                    // Start Main Activity
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else {
                    Toast.makeText(this@LoginActivity, "Login Failed!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun registerUser (name: String, email: String, password: String) {
        compositeDisposable.add(iMyService.registerUser(email, password, name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                Toast.makeText(this@LoginActivity, ""+result, Toast.LENGTH_SHORT).show()
            })
    }

    private fun save_login_info (salt: String) {
        val pref : SharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = pref.edit()
        editor.putString("salt", salt)
        editor.apply()
    }

    private fun find_login_info () : String? {
        val pref : SharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return pref.getString("salt", "")
    }

    private val PERM_REQUEST_CODE = 100

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupPermissions(){
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE
        )

        if (!hasPermissions(this, permissions)) {
            makeRequest()
        }
        else
            run_login()
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE),
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
                    run_login()
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
