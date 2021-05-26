package it.benche.upofood

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.view.*
import kotlinx.android.synthetic.main.layout_type_user.*


@Suppress("DEPRECATION")
class SignupActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            finish()
            super.onBackPressed() }
        setStatusBarWhite(this@SignupActivity)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        button_signin.setOnClickListener{
            signInUser()
        }

        initUsersDialog()
        spinner.setOnClickListener {
            dialog.show()
        }

    }

    private fun setStatusBarWhite(activity: AppCompatActivity){
        //Make status bar icons color dark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.statusBarColor = Color.WHITE
        }
    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if(currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
        //updateUI(currentUser)
    }

    private fun signInUser() {
        mAuth.createUserWithEmailAndPassword(et_email.text.toString(), et_password.text.toString())
            .addOnCompleteListener(this
            ) { task ->
                if (task.isSuccessful) {
                    val mSpinner: TextView = findViewById(R.id.spinner)
                    if (mSpinner.text == "Cliente") {
                        addClientInCloud()
                        startActivity(Intent(this, AddAddressActivity::class.java))
                    } else if (mSpinner.text == "Rider") {
                        addRiderInCloud()
                        startActivity(Intent(this, DrawerActivityRider2::class.java))
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
    }

    private fun addClientInCloud() {
        // Create a new user with a first and last name
        val user = hashMapOf(
            "nome" to et_name.text.toString(),
            "cognome" to et_surname.text.toString(),
            "indirizzo" to "",
            "telefono" to et_phone.text.toString(),
            "account" to "Cliente"
        )


// Add a new document with a generated ID

// Add a new document with a generated ID
        val TAG = "SignupActivity"
        val uid: String? = mAuth.uid
        if (uid != null) {
            db.collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener { Log.d(TAG, "Utente aggiunto correttamente" )}
                .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
        }
    }

    private fun addRiderInCloud() {
        // Create a new user with a first and last name
        val user = hashMapOf(
                "nome" to et_name.text.toString(),
                "cognome" to et_surname.text.toString(),
                "telefono" to et_phone.text.toString(),
                "numeroRider" to (0..999999).random(),
                "account" to "Rider"
        )


// Add a new document with a generated ID

// Add a new document with a generated ID
        val TAG = "SignupActivity"
        val uid: String? = mAuth.uid
        if (uid != null) {
            db.collection("users")
                    .document(uid)
                    .set(user)
                    .addOnSuccessListener { Log.d(TAG, "Utente aggiunto correttamente" )}
                    .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initUsersDialog() {
        dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.layout_type_user)
        dialog.rgUsers.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.client -> {
                    dialog.dismiss()
                    spinner.text = "Cliente"
                }
                R.id.rider -> {
                    dialog.dismiss()
                    spinner.text = "Rider"
                }
            }
        }
        dialog.ivClose.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}