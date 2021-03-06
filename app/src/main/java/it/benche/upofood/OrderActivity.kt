package it.benche.upofood

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.message.MessageListActivity
import it.benche.upofood.utils.RecapProductsAdapter
import it.benche.upofood.utils.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_active_order.*
import kotlinx.android.synthetic.main.activity_order2.*
import kotlinx.android.synthetic.main.activity_shopping_cart.*
import kotlinx.android.synthetic.main.fragment_list_trips.*

@Suppress("DEPRECATION", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class OrderActivity: AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth
    lateinit var uid: String
    lateinit var arrayList: ArrayList<Product>
    lateinit var rider: ArrayList<String>

    private lateinit var productAdapter: RecapProductsAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order2)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        checkRiderStatus()

        db = FirebaseFirestore.getInstance()

        val nOrd = intent.getStringExtra("ORDER_NUMBER")
        val cOrd = intent.getStringExtra("ORDER_CLIENT")

        numberOrder.text = "#${nOrd}"
        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                if (document.id == cOrd) {
                    val name = document.getString("nome").toString()
                    val surname = document.getString("cognome").toString()

                    val city = document.getString("indirizzo.Citta").toString()
                    val cap = document.getString("indirizzo.CAP").toString()
                    val provence = document.getString("indirizzo.Provincia").toString()
                    val via = document.getString("indirizzo.Via").toString()
                    client.text = "$name $surname"
                    address.text = "$via, $cap $city ($provence)"
                }
            }
        }


        mAuth = FirebaseAuth.getInstance()

        arrayList = ArrayList()
        rider = ArrayList()

        if (nOrd != null) {
            db.collection("orders")
                    .document(nOrd)
                    .collection("products")
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                val product: String = document.getString("prodotto").toString()
                                val qty: Int = document.getLong("qty")!!.toInt()

                                val p = Product(product, qty)
                                arrayList.add(p)
                            }
                            loadingPanel1.visibility = RelativeLayout.GONE
                            initRecyclerView()
                            addDataSet()

                        } else {
                            Log.w(
                                    "OrderActivity",
                                    "Error getting documents.",
                                    task.exception
                            )
                        }
                    }
        }

        db.collection("orders")
                .document("$nOrd")
                .get()
                .addOnSuccessListener { result ->
                    when (result.getString("deliveryState").toString()) {
                        "Preso in carico" -> {
                            btnAccept.setTextAppearance(this, R.style.BottomButton_Accept)
                            btnPrep.setTextAppearance(this, R.style.BottomButton_Primary)
                            btnDelivered.setTextAppearance(this, R.style.BottomButton_Disabled)
                        }
                        "In preparazione" -> {
                            btnAccept.setTextAppearance(R.style.BottomButton_Accept)
                            btnPrep.setTextAppearance(R.style.BottomButton_Accept)
                            btnDelivered.setTextAppearance(R.style.BottomButton_Primary)

                        }
                        "Consegnato al rider" -> {
                            btnAccept.setTextAppearance(R.style.BottomButton_Accept)
                            btnPrep.setTextAppearance(R.style.BottomButton_Accept)
                            btnDelivered.setTextAppearance(R.style.BottomButton_Accept)
                        }
                    }
                }

        btnAccept.setOnClickListener {
            db.collection("orders")
                    .document("$nOrd")
                    .update(mapOf("deliveryState" to "Preso in carico"))
                    .addOnSuccessListener {
                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                    .addOnFailureListener {  }
        }

        btnPrep.setOnClickListener {
            db.collection("orders")
                    .document("$nOrd")
                    .update(mapOf("deliveryState" to "In preparazione"))
                    .addOnSuccessListener {
                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                    .addOnFailureListener {  }
        }

        btnDelivered.setOnClickListener {
            db.collection("orders")
                    .document("$nOrd")
                    .update(mapOf("deliveryState" to "Consegnato al rider"))
                    .addOnSuccessListener {
                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                    .addOnFailureListener {  }
        }

        btnSelectRider.setOnClickListener {
            val intent = Intent(this, RidersActivity::class.java)
            intent.putExtra("ORDINE", nOrd)
            startActivity(intent)
        }

        toChat.setOnClickListener {
            val intent = Intent(this, MessageListActivity::class.java)
            intent.putExtra("SENDER", mAuth.currentUser.uid)
            intent.putExtra("RECEIVER", rider[0])
            startActivity(intent)
        }

        refreshOrd.setOnRefreshListener {
            refreshOrd.isRefreshing = false
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)

        }
    }

    private fun addDataSet() {
        productAdapter.submitList(arrayList)
    }

    private fun initRecyclerView() {
        recView.apply {
            layoutManager = LinearLayoutManager(this@OrderActivity)
            val topSpacingDecoration = TopSpacingItemDecoration(30, 0, 0)
            addItemDecoration(topSpacingDecoration)
            productAdapter = RecapProductsAdapter()
            recView.adapter = productAdapter
        }

    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun checkRiderStatus() {
        db = FirebaseFirestore.getInstance()
        //Controlla che la richiesta sia in attesa
        db.collection("requests")
                .get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        for(document in task.result!!) {
                            if (document.getString("order").toString() == intent.getStringExtra("ORDER_NUMBER").toString()) {
                                btnSelectRider.isEnabled = false
                                tvSelectRider.text = "In attesa di conferma"
                            }
                        }
                    }
                }

        //Controlla che il rider abbia accettato l'incarico
        db.collection("orders")
                .document(intent.getStringExtra("ORDER_NUMBER").toString())
                .get()
                .addOnSuccessListener { task ->
                    if(task.getString("rider").toString() != "") {
                        btnSelectRider.isEnabled = false
                        toChat.visibility = CardView.VISIBLE
                        db.collection("users")
                                .get()
                                .addOnCompleteListener { task1 ->
                                    if(task1.isSuccessful) {
                                        for(document in task1.result!!) {
                                            if(document.getLong("numeroRider").toString() == task.getString("rider").toString()) {
                                                rider.add(document.id)
                                                tvSelectRider.text = "${document.getString("nome").toString()} ${document.getString("cognome").toString()}"
                                                rdCheck.setColorFilter(Color.parseColor(("#32CD32")))
                                                tvSelectRider.setTextColor(Color.parseColor("#32CD32"))
                                            }
                                        }
                                    }
                                }

                    }
                }
    }

}

