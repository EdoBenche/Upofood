package it.benche.upofood

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.manager.ListManagersActivity
import kotlinx.android.synthetic.main.activity_drawer_rider2.*
import kotlinx.android.synthetic.main.activity_message_list.*

class DrawerActivityRider2 : AppCompatActivity() {
    
    lateinit var db: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_rider2)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        
        profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        newRequest.setOnClickListener {
            startActivity(Intent(this, RequestsActivity::class.java))
        }
        chatManagers.setOnClickListener {
            startActivity(Intent(this, ListManagersActivity::class.java))
        }
        listTrips.setOnClickListener {
            startActivity(Intent(this, ActiveTripsActivity::class.java))
        }
        
        db.collection("users")
            .document(mAuth.currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                Snackbar.make(rLayoutRider, "Account " + mAuth.currentUser.email, Snackbar.LENGTH_SHORT).show()
                helloRider.text = "Ciao ${document.getString("nome").toString()}!"
                val nRider = document.getLong("numeroRider")!!.toString()
                availability.isChecked = document.getString("available").toString() == "yes"
                db.collection("requests")
                    .get()
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            var isSomethingNew = false
                            for(document in task.result!!) {
                                if(document.getString("rider").toString() == nRider) {
                                    isSomethingNew = true
                                }
                            }
                            if(isSomethingNew) {
                                newRequestPin.visibility = ImageView.VISIBLE
                            }
                        }
                    }
            }
            .addOnFailureListener {
                helloRider.text = "Ciao!"
            }

        refreshRider.setOnRefreshListener {
            refreshRider.isRefreshing = false
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)

        }

        availability.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                db.collection("users")
                    .document(mAuth.currentUser.uid)
                    .update(
                        mapOf(
                            "available" to "yes"
                        )
                    )
            } else if (!isChecked) {
                db.collection("users")
                    .document(mAuth.currentUser.uid)
                    .update(
                        mapOf(
                            "available" to "no"
                        )
                    )
            }
        }
    }
}