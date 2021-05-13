package it.benche.upofood

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.manager.ListManagersActivity
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*


class DrawerActivityRider : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_rider)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.shop)
        fab.setImageResource(R.drawable.ic_baseline_notification_important_24)
        fab.setOnClickListener { view ->
            showPendentRequests()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.home, R.id.nav_list_trips, R.id.nav_chat
                ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance()

        var uid = mAuth.uid

        //var nameandsurname: TextView = findViewById(R.id.nameandsurname)
        //var role: TextView = findViewById(R.id.role)
        var TAG = "DrawerActivity";

        db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for(document in result) {
                        if(document.id == uid.toString()) {
                            val name = document.getString("nome")
                            val surname = document.getString("cognome")
                            var r_ole = document.getString("account")

                            nameandsurname.setText(name + " " + surname)
                            role.setText(r_ole)

                            Snackbar.make(drawerLayout, "Accesso eseguito come " + mAuth.currentUser.email, 5000).show()
                        }
                    }
                }
        navView.menu.findItem(R.id.nav_slideshow).setOnMenuItemClickListener { menuItem: MenuItem? ->
            startActivity(Intent(this, ListManagersActivity::class.java))
            return@setOnMenuItemClickListener true
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

    private fun signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this
                ) { startActivity(Intent(this, LoginActivity::class.java)) }
    }

    private fun showPendentRequests() {
        startActivity(Intent(this, RequestsActivity::class.java))
    }

}

class User(val riderID: String, val coord: Coordinate)
class Coordinate(val lat: Double, val lng: Double)