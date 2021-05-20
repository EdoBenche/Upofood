package it.benche.upofood

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.view.*
import kotlinx.android.synthetic.main.layout_type_user.*


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
        toolbar.setNavigationOnClickListener { super.onBackPressed() }
        setStatusBarWhite(this@SignupActivity)

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance()

        button_signin.setOnClickListener{
            signInUser()
        }

        initUsersDialog()
        spinner.onClick {
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
            .addOnCompleteListener(this,
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        var mSpinner: TextView = findViewById(R.id.spinner)
                        if(mSpinner.text == "Cliente") {
                            addClientInCloud()
                            startActivity(Intent(this, AddAddressActivity::class.java))
                        }
                        else if(mSpinner.text == "Rider") {
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

                })
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
        val TAG: String = "SignupActivity";
        var uid: String? = mAuth.uid
        if (uid != null) {
            db.collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener { Log.d(TAG, "Utente aggiunto correttamente" )}
                .addOnFailureListener(OnFailureListener { e -> Log.w(TAG, "Error adding document", e) })
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
        val TAG: String = "SignupActivity";
        var uid: String? = mAuth.uid
        if (uid != null) {
            db.collection("users")
                    .document(uid)
                    .set(user)
                    .addOnSuccessListener { Log.d(TAG, "Utente aggiunto correttamente" )}
                    .addOnFailureListener(OnFailureListener { e -> Log.w(TAG, "Error adding document", e) })
        }
    }

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
        dialog.ivClose.onClick {
            dialog.dismiss()
        }
    }


}

private fun Any.onClick(function: () -> Unit) {

}
