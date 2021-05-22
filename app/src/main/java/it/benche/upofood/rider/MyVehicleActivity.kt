package it.benche.upofood.rider

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.R
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.activity_my_vehicle.*


class MyVehicleActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_vehicle)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(mAuth.currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                when {
                    document.getString("vehicle").toString() == "Bicicletta \uD83D\uDEB2" -> {
                        radioBike.isChecked = true
                    }
                    document.getString("vehicle").toString() == "Scooter \uD83D\uDEF5" -> {
                        radioScooter.isChecked = true
                    }
                    document.getString("vehicle").toString() == "Monopattino \uD83D\uDEF4" -> {
                        radioMonopattino.isChecked = true
                    }
                    document.getString("vehicle").toString() == "Automobile \uD83D\uDE97" -> {
                        radioCar.isChecked = true
                    }
                    document.getString("vehicle").toString() == "Space Shuttle \uD83D\uDE80" -> {
                        radioSpace.isChecked = true
                    }
                }

                if(document.getString("marca").toString() != "null") {
                    marca.setText(document.getString("marca").toString())
                }
                if(document.getString("modello").toString() != "null") {
                    modello.setText(document.getString("modello").toString())
                }
            }

        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            // This will get the radiobutton that has changed in its check state
            val checkedRadioButton = group.findViewById<View>(checkedId) as RadioButton
            // This puts the value (true/false) into the variable
            val isChecked = checkedRadioButton.isChecked
            // If the radiobutton that has changed in check state is now checked...
            if (isChecked) {
                db.collection("users")
                    .document(mAuth.currentUser.uid)
                    .update("vehicle", checkedRadioButton.text)
            }
        })

        btnSaveVehicle.onClick {
            db.collection("users")
                .document(mAuth.currentUser.uid)
                .update(mapOf(
                    "marca" to marca.text.toString(),
                    "modello" to modello.text.toString()
                ))
                .addOnSuccessListener {
                    Toast.makeText(context, "Le informazioni sono state aggiornate con successo!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Ops... qualcosa è andato storto! Prego, riprovare", Toast.LENGTH_SHORT).show()
                }
        }
    }
}