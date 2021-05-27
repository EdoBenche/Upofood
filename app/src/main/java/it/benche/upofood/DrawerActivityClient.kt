package it.benche.upofood

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.client.ActiveOrderActivity
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DrawerActivityClient : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        checkActiveOrder()
        setContentView(R.layout.activity_drawer_client)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.shop)
        fab.setOnClickListener { view ->
            startActivity(Intent(this, ShoppingCartActivity::class.java))
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_categories, R.id.nav_chat
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



        val uid = mAuth.uid

        //var nameandsurname: TextView = findViewById(R.id.nameandsurname)
        //var role: TextView = findViewById(R.id.role)

        db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for(document in result) {
                        if(document.id == uid.toString()) {
                            val name = document.getString("nome")
                            val surname = document.getString("cognome")
                            val r_ole = document.getString("account")

                            nameandsurname.text = "$name $surname"
                            role.text = r_ole

                            Snackbar.make(drawerLayout, "Account " + mAuth.currentUser.email, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.drawer, menu)
        menu.findItem(R.id.action_settings).setOnMenuItemClickListener { menuItem: MenuItem? ->
            startActivity(Intent(this, ProfileActivity::class.java))
            return@setOnMenuItemClickListener true
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {

    }

    private fun checkActiveOrder() {

        db.collection("orders")
                .get()
                .addOnCompleteListener { result ->
                    if(result.isSuccessful) {
                        for(document in result.result!!) {
                            if(((document.getString("client").toString() == mAuth.currentUser.uid) && (document.getString("isDelivered").toString() == "no")) || (document.getString("client").toString() == mAuth.currentUser.uid && document.getString("deliveryState").toString() == "Consegnato") && document.getString("isDelivered").toString() == "no") {
                                val intent = Intent(this, ActiveOrderActivity::class.java)
                                intent.putExtra("CLIENT", mAuth.currentUser.uid)
                                intent.putExtra("ORDER", document.id)
                                intent.putExtra("RIDER", document.getString("rider").toString())
                                startActivity(intent, null)
                            }
                        }
                    }
                }
    }

}