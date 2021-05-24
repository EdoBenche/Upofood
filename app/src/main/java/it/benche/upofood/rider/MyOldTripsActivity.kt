package it.benche.upofood.rider

import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.R
import it.benche.upofood.manager.OldOrdersAdapter
import it.benche.upofood.manager.Order
import it.benche.upofood.utils.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_select_rider.*

class MyOldTripsActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var arrayList: ArrayList<OldTrips>
    private lateinit var tripsAdapter: MyOldTripsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_rider)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }
        titleActivity.text = "Storico viaggi"

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        arrayList = ArrayList()

        db.collection("users")
            .document(mAuth.currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                val nRider = document.getLong("numeroRider")!!.toString()
                db.collection("orders")
                    .get()
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            for(document in task.result!!) {
                                if(document.getString("isDelivered").toString() == "yes" && document.getString("rider").toString() == nRider) {
                                    val numberOrder = document.id
                                    val date = document.getString("dateOrder").toString()
                                    val aaaa = date.substring(0, 4)
                                    val mm = date.substring(4, 6)
                                    val gg = date.substring(6, 8)
                                    val d = "$gg/$mm/$aaaa"
                                    val status = document.getString("deliveryState").toString()
                                    db.collection("users")
                                        .document(document.getString("client").toString())
                                        .get()
                                        .addOnSuccessListener { doc ->
                                            val client = "${doc.getString("nome").toString()} ${doc.getString("cognome").toString()}"
                                            val o = OldTrips(numberOrder, client, status, d)
                                            arrayList.add(o)
                                        }
                                }
                            }
                            loadingPanelR.visibility = RelativeLayout.GONE
                            initRecyclerView()
                            addDataSet()
                        }
                    }
            }

        refreshRid.setOnRefreshListener {
            refreshRid.isRefreshing = false
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    private fun addDataSet() {
        tripsAdapter.submitList(arrayList)
    }

    private fun initRecyclerView() {
        recyView.apply {
            layoutManager = LinearLayoutManager(this@MyOldTripsActivity)
            val topSpacingDecoration = TopSpacingItemDecoration(20,0,0)
            addItemDecoration(topSpacingDecoration)
            tripsAdapter = MyOldTripsAdapter()
            recyView.adapter = tripsAdapter
        }

    }
}

class OldTrips(val number: String, val client: String, val status: String, val date: String)