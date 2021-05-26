package it.benche.upofood

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.ui.orders.OrdersFragment
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class DrawerActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        createNotificationChannel()

        val fab: FloatingActionButton = findViewById(R.id.shop)
        fab.setImageResource(R.drawable.ic_baseline_add_24)
        fab.setOnClickListener { view ->
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_orders, R.id.nav_categories, R.id.nav_chat
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

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

                            Snackbar.make(drawerLayout, "Accesso eseguito come " + mAuth.currentUser!!.email, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }


        checkNewOrders()
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

    private fun checkNewOrders() {
        var isSomethingNew = false

        db.collection("orders")
                .get()
                .addOnSuccessListener { result ->
                    for(document in result) {
                        if(document.getString("isNew").toString() == "yes") {
                            isSomethingNew = true
                        }
                    }
                    if(isSomethingNew) {
                        // Create an explicit intent for an Activity in your app
                        val intent = Intent(baseContext, OrdersFragment::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
                        val builder = NotificationCompat.Builder(this, "blabla")
                                .setSmallIcon(R.drawable.ic_baseline_notification_add_24)
                                .setContentTitle("Nuovo ordine!")
                                .setContentText("Hai dei nuovi ordini in sospeso, vai su Ordini attivi")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)

                        with(NotificationManagerCompat.from(this)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(100, builder.build())
                        }
                    }
                }
                .addOnFailureListener { e -> Log.w(
                        "DrawerActivity",
                        "Error deleting document",
                        e
                ) }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "orderChannel"
            val descriptionText = "Channel for order notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("blabla", name, importance).apply {
                description = descriptionText
                setShowBadge(true)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}


