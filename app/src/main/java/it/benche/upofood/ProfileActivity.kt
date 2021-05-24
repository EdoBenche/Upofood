package it.benche.upofood

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.client.MyOldOrdersActivity
import it.benche.upofood.manager.AddManagerActivity
import it.benche.upofood.manager.MyShopActivity
import it.benche.upofood.manager.OldOrdersActivity
import it.benche.upofood.manager.RidersPositionActivity
import it.benche.upofood.rider.MyOldTripsActivity
import it.benche.upofood.rider.MyVehicleActivity
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.nav_header_main.*

class ProfileActivity: AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        var uid = mAuth.uid
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

        signOutButton.onClick {
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
            FirebaseAuth.getInstance().signOut();
            startActivity(Intent(context,LoginActivity::class.java))
        }

        deleteCache.onClick {
            context.cacheDir.deleteRecursively()
            Toast.makeText(context, "La cache è stata pulita con successo!", Toast.LENGTH_SHORT).show()
        }

        modifyProfile.onClick {
            modifyProfile()
        }

        //Bottoni gestore
        manageShop.onClick {
            startActivity(Intent(context, MyShopActivity::class.java))

        }

        tvAddGestore.onClick {
            startActivity(Intent(context, AddManagerActivity::class.java))
        }

        storicoOrdiniGestore.onClick {
            startActivity(Intent(context, OldOrdersActivity::class.java))
        }

        myRiders.onClick {
            startActivity(Intent(context, RidersPositionActivity::class.java))
        }

        //Bottoni cliente
        tvManageAddress.onClick {
            startActivity(Intent(context, AddAddressActivity::class.java))
        }

        tvMyOrders.onClick {
            startActivity(Intent(context, MyOldOrdersActivity::class.java))

        }

        //Bottoni rider
        storicoViaggi.onClick {
            startActivity(Intent(context, MyOldTripsActivity::class.java))
        }

        myTransport.onClick {
            startActivity(Intent(context, MyVehicleActivity::class.java))
        }
    }

    private fun modifyProfile() {
        var intent = Intent(this, ModifyProfileActivity::class.java)
        startActivity(intent)
    }
}