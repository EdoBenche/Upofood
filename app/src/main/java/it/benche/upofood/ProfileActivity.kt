package it.benche.upofood

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import kotlinx.android.synthetic.main.about.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*


@Suppress("DEPRECATION")
class ProfileActivity: AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
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
            /*db.collection("users")
                    .get()
                    .addOnSuccessListener { result ->
                        for(document in result) {
                            if(document.id == uid.toString() && document.getString("account") == "Rider") {
                                db.collection("users")
                                        .document(document.id)
                                        .update(mapOf("available" to "no"))
                            }
                        }
                    }*/
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,LoginActivity::class.java))
        }

        about.setOnClickListener {
            showAbout()
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

        lang_sq.setOnClickListener {
            setLocate("sq")
            restart()
            Toast.makeText(this, "Albanese", Toast.LENGTH_SHORT).show()
        }
        lang_ro.setOnClickListener {
            setLocate("ro")
            restart()
            Toast.makeText(this, "Rumeno", Toast.LENGTH_SHORT).show()
        }
        lang_es.setOnClickListener {
            setLocate("es")
            restart()
            Toast.makeText(this, "Spagnolo", Toast.LENGTH_SHORT).show()
        }
        lang_en.setOnClickListener {
            setLocate("en")
            restart()
            Toast.makeText(this, "Inglese", Toast.LENGTH_SHORT).show()
        }
        lang_it.setOnClickListener {
            setLocate("")
            restart()
            Toast.makeText(this, "Italiano", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAbout() {
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.setContentView(R.layout.about)
        dialog.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        dialog.show()
        dialog.ivClose.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun restart() {
        Toast.makeText(this, "L'app verrà riavviata tra 2 secondi", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
            finishAffinity()}, 2000)

    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun language(activity: Activity, language: String){
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = activity.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun setLocate(Lang:String) {
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", Lang)
        editor.apply()
    }

    private fun modifyProfile() {
        val intent = Intent(this, ModifyProfileActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}