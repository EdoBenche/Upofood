package it.benche.upofood.manager

import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.R
import it.benche.upofood.utils.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_select_rider.*

class OldOrdersActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var arrayList: ArrayList<Order>
    private lateinit var ordersAdapter: OldOrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_rider)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }
        titleActivity.text = "Storico ordini"
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        arrayList = ArrayList()

        db.collection("orders")
            .orderBy("dateOrder")
            .get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    for (document in task.result!!) {
                        if(document.getString("isDelivered").toString() == "yes") {
                            val numberOrder = document.id
                            val date = document.getString("dateOrder").toString()
                            val aaaa = date.substring(0, 4)
                            val mm = date.substring(4, 6)
                            val gg = date.substring(6, 8)
                            val d = "$gg/$mm/$aaaa"
                            val totalPrice = document.getLong("totalPrice")!!.toDouble()
                            val rating1 = document.getString("ratingQuality").toString()
                            val rating2 = document.getString("ratingFast").toString()
                            val rating3 = document.getString("ratingCourtesy").toString()
                            val status = document.getString("deliveryState").toString()
                            db.collection("users")
                                .document(document.getString("client").toString())
                                .get()
                                .addOnSuccessListener { doc ->
                                    val client = "${doc.getString("nome").toString()} ${doc.getString("cognome").toString()}"
                                    val o = Order(numberOrder, d, client, totalPrice, status, rating1, rating2, rating3)
                                    arrayList.add(o)
                                }
                        }
                    }

                    loadingPanelR.visibility = RelativeLayout.GONE
                    initRecyclerView()
                    addDataSet()
                } else {
                    Log.w(
                        "OldOrdersActivity",
                        "Error getting documents.",
                        task.exception
                    )
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
        ordersAdapter.submitList(arrayList)
    }

    private fun initRecyclerView() {
        recyView.apply {
            layoutManager = LinearLayoutManager(this@OldOrdersActivity)
            val topSpacingDecoration = TopSpacingItemDecoration(20,0,0)
            addItemDecoration(topSpacingDecoration)
            ordersAdapter = OldOrdersAdapter()
            recyView.adapter = ordersAdapter
        }

    }
}

class Order(val number: String, val date: String, val client: String, val totalPrice: Double, val status: String, val rating1: String, val rating2: String, val rating3: String)