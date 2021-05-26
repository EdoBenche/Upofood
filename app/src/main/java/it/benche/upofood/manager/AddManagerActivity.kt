package it.benche.upofood.manager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.LoginActivity
import it.benche.upofood.R
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.activity_modify_profile.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class AddManagerActivity: AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_profile)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        textInfo.visibility = LinearLayout.GONE
        old.hint = "Password"
        newPwd.hint = "Conferma password"
        edtEmail.isEnabled = true
        btnSave.text = "Invia"

        btnSave.setOnClickListener {
            sendInfo()
        }
    }

    private fun sendInfo() {
        if(edtOldPassword.text.isEmpty()) {
            edtOldPassword.error = "Campo vuoto!"
            return
        }
        if(edtNewPassword.text.isEmpty()) {
            edtNewPassword.error = "Campo vuoto!"
            return
        }
        if(edtEmail.text.isEmpty()) {
            edtEmail.error = "Campo vuoto!"
            return
        }
        if(edtName.text.isEmpty()) {
            edtName.error = "Campo vuoto!"
            return
        }
        if(edtSurname.text.isEmpty()) {
            edtSurname.error = "Campo vuoto!"
            return
        }
        if(edtPhoneNumber.text.isEmpty()) {
            edtPhoneNumber.error = "Campo vuoto!"
            return
        }
        if(edtOldPassword.text.toString() == edtNewPassword.text.toString()) {
            mAuth.createUserWithEmailAndPassword(edtEmail.text.toString(), edtOldPassword.text.toString())
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            addDataInCloud()
                        }
                    }
        } else {
            edtNewPassword.error = "La password non corrisponde!"
            return
        }

    }

    private fun addDataInCloud() {

        val user = hashMapOf(
                "nome" to edtName.text.toString(),
                "cognome" to edtSurname.text.toString(),
                "telefono" to edtPhoneNumber.text.toString(),
                "account" to "Gestore"
        )
        db.collection("users")
                .document(mAuth.currentUser.uid)
                .set(user)
                .addOnSuccessListener { Toast.makeText(this, "Nuovo gestore aggiunto con successo! Per favore, riesegui il login", Toast.LENGTH_SHORT).show()
                    mAuth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                .addOnFailureListener {}
    }
}