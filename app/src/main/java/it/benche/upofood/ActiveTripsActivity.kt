package it.benche.upofood

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.utils.ActiveTripsAdapter
import it.benche.upofood.utils.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_active_trips.*
import kotlinx.android.synthetic.main.activity_requests.*

class ActiveTripsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var activeTripsAdapter : ActiveTripsAdapter
    private lateinit var arrayList: ArrayList<Trips>
    private lateinit var numberRider: ArrayList<String>
    private lateinit var client: ArrayList<String>
    private lateinit var price: ArrayList<String>
    private lateinit var order: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_trips)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        arrayList = ArrayList()
        numberRider = ArrayList()
        client = ArrayList()
        price = ArrayList()
        order = ArrayList()

        db.collection("users")
            .document(mAuth.uid.toString())
            .get()
            .addOnSuccessListener { document ->
                numberRider.add(document.getLong("numeroRider").toString())

                db.collection("orders")
                    .get()
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            for(document in task.result!!) {
                                if(document.getString("rider") == numberRider[0] && document.getString("isPaid") == "no" && document.getString("deliveryState") != "Non consegnato") {
                                    client.add(document.getString("client").toString())
                                    price.add(document.getDouble("totalPrice").toString())
                                    order.add(document.id)
                                }
                            }
                            for(i in 0 until client.size) {
                                db.collection("users")
                                    .document(client[i])
                                    .get()
                                    .addOnSuccessListener { document ->
                                        val name = "${document.getString("nome").toString()} ${document.getString("cognome").toString()}"
                                        val via = document.getString("indirizzo.Via").toString()
                                        val cap = document.getString("indirizzo.CAP").toString()
                                        val citta = document.getString("indirizzo.Citta").toString()
                                        val addr = "${via}, $cap $citta"
                                        val pay = price[i]
                                        val ord = order[i]
                                        val id = client[i]

                                        val t = Trips(
                                            name,
                                            addr,
                                            pay,
                                            ord,
                                            id
                                        )
                                        arrayList.add(t)
                                    }

                            }
                            loadingPanelTrips.visibility = RelativeLayout.GONE
                            initRecyclerView()
                            addDataSet()
                        } else {
                            Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                        }
                    }
            }

        refreshTrips.setOnRefreshListener {
            refreshTrips.isRefreshing = false
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    private fun addDataSet() {
        activeTripsAdapter.submitList(arrayList)
    }

    private fun initRecyclerView() {
        recViewTrips.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(10, 0, 0)
            addItemDecoration(topSpacingDecoration)
            activeTripsAdapter = ActiveTripsAdapter()
            recViewTrips.adapter = activeTripsAdapter
        }
    }
}

class Trips(val name: String, val address: String, val toPay: String, val order: String, val clientId: String)