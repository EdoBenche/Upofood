package it.benche.upofood

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
private lateinit var mAuth: FirebaseAuth
@Suppress("DEPRECATION")
class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        mAuth = FirebaseAuth.getInstance()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            finish()
            super.onBackPressed() }
        setStatusBarWhite(this@ForgotPasswordActivity)

        button_send.setOnClickListener {
            val emailAddress = et_email.text.toString();

            mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this, "Ti abbiamo inviato un link alla mail $emailAddress", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                    else {
                        Toast.makeText(this, "Bruh", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun setStatusBarWhite(activity: AppCompatActivity){
        //Make status bar icons color dark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.statusBarColor = Color.WHITE
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}