package it.benche.upofood

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.activity_modify_profile.*

class ModifyProfileActivity: AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_profile)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            finish()
            super.onBackPressed() }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        db.collection("users")
                .document(mAuth.currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    edtName.setText(document.getString("nome").toString())
                    edtSurname.setText(document.getString("cognome").toString())
                    edtPhoneNumber.setText(document.getString("telefono").toString())
                    edtEmail.setText(mAuth.currentUser.email.toString())
                }

        btnSave.onClick {
            updateProfile()
        }

    }

    private fun updateProfile() {
        if(edtNewPassword.text.isNotEmpty() && edtOldPassword.text.isNotEmpty()) {
            var credential: AuthCredential = EmailAuthProvider.getCredential(
                    mAuth.currentUser.email,
                    edtOldPassword.text.toString()
            )
            mAuth.currentUser.reauthenticate(credential)
                    .addOnSuccessListener {
                        mAuth.currentUser.updatePassword(edtNewPassword.text.toString())
                        db.collection("users")
                                .document(mAuth.currentUser.uid)
                                .update(mapOf(
                                        "nome" to edtName.text.toString(),
                                        "cognome" to edtSurname.text.toString(),
                                        "telefono" to edtPhoneNumber.text.toString()
                                ))
                        Toast.makeText(this, "Le informazioni sono state aggiornate con successo!", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }
                    .addOnFailureListener {
                        edtOldPassword.error = "La password che hai inserito non corrisponde alla vecchia password!"
                        return@addOnFailureListener
                    }

        } else {
            db.collection("users")
                    .document(mAuth.currentUser.uid)
                    .update(mapOf(
                            "nome" to edtName.text.toString(),
                            "cognome" to edtSurname.text.toString(),
                            "telefono" to edtPhoneNumber.text.toString()
                    ))
            Toast.makeText(this, "Le informazioni sono state aggiornate con successo!", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }

        if(edtNewPassword.text.isEmpty() && edtOldPassword.text.isNotEmpty()) {
            edtNewPassword.error = "Devi inserirci la nuova password!"
            return
        }
        if(edtNewPassword.text.isNotEmpty() && edtOldPassword.text.isEmpty()) {
            edtOldPassword.error = "Devi inserirci la vecchia password!"
            return
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}