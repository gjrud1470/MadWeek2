package com.example.MadWeek2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.example.MadWeek2.Retrofit.LoginService
import com.example.MadWeek2.Retrofit.RetrofitClient
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.rengwuxian.materialedittext.MaterialEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : AppCompatActivity() {

    lateinit var iMyService: LoginService
    internal var compositeDisposable = CompositeDisposable()

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        //Init API
        val retrofit = RetrofitClient.getInstance()
        iMyService = retrofit.create(LoginService::class.java)

        //event
        btn_login.setOnClickListener {
            loginUser(input_email.text.toString(), input_password.text.toString())
        }

        link_signup.setOnClickListener {
            Log.wtf("SIGNUP", "HIDASF;ALSKD")
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

    private fun loginUser (email: String, password: String) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this@LoginActivity, "Please fill in email", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this@LoginActivity, "Please fill in password", Toast.LENGTH_SHORT).show()
            return
        }

        compositeDisposable.add(iMyService.loginUser(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                Toast.makeText(this@LoginActivity, ""+result, Toast.LENGTH_SHORT).show()
            })
    }

    private fun registerUser (name: String, email: String, password: String) {
        compositeDisposable.add(iMyService.registerUser(name, email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                Toast.makeText(this@LoginActivity, ""+result, Toast.LENGTH_SHORT).show()
            })
    }
}
