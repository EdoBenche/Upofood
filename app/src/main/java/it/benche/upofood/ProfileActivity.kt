package it.benche.upofood

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.client.MyOldOrdersActivity
import it.benche.upofood.manager.AddManagerActivity
import it.benche.upofood.manager.MyShopActivity
import it.benche.upofood.manager.OldOrdersActivity
import it.benche.upofood.manager.RidersPositionActivity
import it.benche.upofood.rider.MyOldTripsActivity
import it.benche.upofood.rider.MyVehicleActivity
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity: AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            finish()
            super.onBackPressed() }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val uid = mAuth.uid
        db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for(document in result) {
                        if(document.id == uid.toString()) {
                            val name = document.getString("nome")
                            val surname = document.getString("cognome")
                            val role = document.getString("account")

                            txtDisplayName.text = ("$name $surname")

                            if(role == "Gestore") {
                                tvAddGestore.visibility = TextView.VISIBLE
                                storicoOrdiniGestore.visibility = TextView.VISIBLE
                                myRiders.visibility = TextView.VISIBLE
                                manageShop.visibility = TextView.VISIBLE

                            } else if(role == "Cliente") {
                                tvManageAddress.visibility = TextView.VISIBLE
                                tvMyOrders.visibility = TextView.VISIBLE
                            } else if(role == "Rider") {
                                storicoViaggi.visibility = TextView.VISIBLE
                                myTransport.visibility = TextView.VISIBLE
                            }
                        }
                    }
                }

        signOutButton.setOnClickListener {
            db.collection("users")
                    .get()
                    .addOnSuccessListener { result ->
                        for(document in result) {
                            if(document.id == uid.toString() && document.getString("account") == "Rider") {
                                db.collection("users")
                                        .document(document.id)
                                        .update(mapOf("available" to "no"))
                            }
                        }
                    }
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,LoginActivity::class.java))
        }

        deleteCache.setOnClickListener {
            cacheDir.deleteRecursively()
            Toast.makeText(this, "La cache è stata pulita con successo!", Toast.LENGTH_SHORT).show()
        }

        modifyProfile.setOnClickListener {
            modifyProfile()
        }

        //Bottoni gestore
        manageShop.setOnClickListener {
            startActivity(Intent(this, MyShopActivity::class.java))

        }

        tvAddGestore.setOnClickListener {
            startActivity(Intent(this, AddManagerActivity::class.java))
        }

        storicoOrdiniGestore.setOnClickListener {
            startActivity(Intent(this, OldOrdersActivity::class.java))
        }

        myRiders.setOnClickListener {
            startActivity(Intent(this, RidersPositionActivity::class.java))
        }

        //Bottoni cliente
        tvManageAddress.setOnClickListener {
            startActivity(Intent(this, AddAddressActivity::class.java))
        }

        tvMyOrders.setOnClickListener {
            startActivity(Intent(this, MyOldOrdersActivity::class.java))

        }

        //Bottoni rider
        storicoViaggi.setOnClickListener {
            startActivity(Intent(this, MyOldTripsActivity::class.java))
        }

        myTransport.setOnClickListener {
            startActivity(Intent(this, MyVehicleActivity::class.java))
        }
    }

    private fun modifyProfile() {
        val intent = Intent(this, ModifyProfileActivity::class.java)
        startActivity(intent)
    }
}