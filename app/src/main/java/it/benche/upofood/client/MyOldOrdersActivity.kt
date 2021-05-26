package it.benche.upofood.client

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.R
import it.benche.upofood.utils.OldOrdersAdapter
import it.benche.upofood.utils.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.activity_requests.*
import kotlinx.android.synthetic.main.activity_select_rider.*
import kotlinx.android.synthetic.main.activity_select_rider.recyView

class MyOldOrdersActivity : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth
    lateinit var uid: String
    lateinit var arrayList: ArrayList<Order>

    private lateinit var ordersAdapter: OldOrdersAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_rider)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            finish()
            super.onBackPressed() }
        titleActivity.text = "I miei vecchi ordini"

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        arrayList = ArrayList()

        db.collection("orders")
            .get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    for (document in task.result!!) {
                        if(document.getString("client").toString() == mAuth.uid && document.getString("isDelivered").toString() == "yes") {
                            val numberOrder = document.id
                            val date = document.getString("dateOrder").toString()
                            val aaaa = date.substring(0, 4)
                            val mm = date.substring(4, 6)
                            val gg = date.substring(6, 8)
                            val d = "$gg/$mm/$aaaa"
                            val totalPrice = document.getLong("totalPrice")!!.toDouble()

                            val o = Order(numberOrder, d, totalPrice)
                            arrayList.add(o)
                        }
                    }

                    loadingPanelR.visibility = RelativeLayout.GONE
                    initRecyclerView()
                    addDataSet()
                } else {
                    Log.w(
                        "MyOldOrdersActivity",
                        "Error getting documents.",
                        task.exception
                    )
                }
            }


        refreshRid.setOnRefreshListener {
            refreshRid.isRefreshing = false
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    private fun addDataSet() {
        ordersAdapter.submitList(arrayList)
    }

    private fun initRecyclerView() {
        recyView.apply {
            layoutManager = LinearLayoutManager(this@MyOldOrdersActivity)
            val topSpacingDecoration = TopSpacingItemDecoration(30,0,0)
            addItemDecoration(topSpacingDecoration)
            ordersAdapter = OldOrdersAdapter()
            recyView.adapter = ordersAdapter
        }

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}

class Order(val number: String, val date: String, val totalPrice: Double)