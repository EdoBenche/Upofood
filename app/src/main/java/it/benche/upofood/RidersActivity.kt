package it.benche.upofood

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.utils.RecapProductsAdapter
import it.benche.upofood.utils.RidersAdapter
import it.benche.upofood.utils.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_requests.*
import kotlinx.android.synthetic.main.activity_select_rider.*
import kotlinx.android.synthetic.main.activity_select_rider.recyView

class RidersActivity: AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth
    lateinit var uid: String
    lateinit var arrayList: ArrayList<Rider>

    private lateinit var ridersAdapter: RidersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_rider)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        db = FirebaseFirestore.getInstance()
        arrayList = ArrayList()

        db.collection("users")
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        for (document in result.result!!) {
                            if (document.getString("available") == "yes") {
                                val nome = document.getString("nome").toString()
                                val cognome = document.getString("cognome").toString()
                                val numeroRider = document.getLong("numeroRider").toString()
                                val ordine = intent.getStringExtra("ORDINE")
                                val r = Rider(nome, cognome, numeroRider, ordine!!)
                                arrayList.add(r)
                            }
                        }
                        initRecyclerView()
                        addDataSet()
                    } else {
                        Log.w(
                                "RidersActivity",
                                "Error getting documents.",
                                result.exception
                        )
                    }
                }

        refreshRid.setOnRefreshListener {
            refreshRid.isRefreshing = false
            finish()
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);

        }
    }

    private fun addDataSet() {
        ridersAdapter.submitList(arrayList)
    }

    private fun initRecyclerView() {
        recyView.apply {
            layoutManager = LinearLayoutManager(this@RidersActivity)
            val topSpacingDecoration = TopSpacingItemDecoration(30,0,0)
            addItemDecoration(topSpacingDecoration)
            ridersAdapter = RidersAdapter()
            recyView.adapter = ridersAdapter
        }

    }
}

class Rider(val nome: String, val cognome: String, val numeroRider: String, val ordine: String)