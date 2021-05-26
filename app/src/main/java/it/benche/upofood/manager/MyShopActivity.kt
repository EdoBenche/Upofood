package it.benche.upofood.manager

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.R
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.activity_my_shop.*

class MyShopActivity: AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_shop)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            finish()
            super.onBackPressed() }

        db = FirebaseFirestore.getInstance()

        db.collection("shop")
                .document("info")
                .get()
                .addOnSuccessListener { document ->
                    edtName.setText(document.getString("name").toString())
                    if(document.getString("address.Via").toString() == "null") {
                        edtAddress.setText("")
                        edtPinCode.setText("")
                        edtCity.setText("")
                        edtProvincia.setText("")
                    } else {
                        edtAddress.setText(document.getString("address.Via").toString())
                        edtPinCode.setText(document.getString("address.CAP").toString())
                        edtCity.setText(document.getString("address.Citta").toString())
                        edtProvincia.setText(document.getString("address.Provincia").toString())
                    }
                }

        btnSave.setOnClickListener {
            updateShop()
        }
    }

    private fun updateShop() {
        if(edtName.text.isEmpty()) {
            edtName.error = "Inserire un nome valido"
            return
        }
        val address = hashMapOf(
                "Via" to edtAddress.text.toString(),
                "CAP" to edtPinCode.text.toString(),
                "Citta" to edtCity.text.toString(),
                "Provincia" to edtProvincia.text.toString()
        )
        db.collection("shop")
                .document("info")
                .update(mapOf(
                        "address" to address,
                        "name" to edtName.text.toString()
                ))
                .addOnSuccessListener {
                    Toast.makeText(this, "Le informazioni sono state aggiornate con successo!", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
                .addOnFailureListener {

                }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}