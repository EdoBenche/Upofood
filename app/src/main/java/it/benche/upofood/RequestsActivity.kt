
package it.benche.upofood

import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.utils.RequestsAdapter
import it.benche.upofood.utils.RidersAdapter
import it.benche.upofood.utils.TopSpacingItemDecoration
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_requests.*
import kotlinx.android.synthetic.main.activity_select_rider.*
import kotlinx.android.synthetic.main.activity_select_rider.recyView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RequestsActivity: AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth
    lateinit var uid: String
    lateinit var arrayList: ArrayList<Request>
    lateinit var listOrd: ArrayList<String>
    lateinit var list: ArrayList<String>
    lateinit var listClient: ArrayList<String>
    lateinit var listPrices: ArrayList<String>
    lateinit var listReq: ArrayList<String>
    lateinit var vibrator: Vibrator

    private lateinit var requestsAdapter: RequestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        uid = mAuth.uid.toString()
        arrayList = ArrayList()
        list = ArrayList()
        listOrd = ArrayList()
        listClient = ArrayList()
        listPrices = ArrayList()
        listReq = ArrayList()
        var numeroRider = " "

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { task ->
                    list.add(task.getLong("numeroRider").toString())

                    db.collection("requests")
                            .get()
                            .addOnCompleteListener { result ->
                                for(document in result.result!!) {
                                    if(document.getString("rider").toString() == list[0]) {
                                        listOrd.add(document.getString("order").toString())
                                        listReq.add(document.getString("numberRequest").toString())

                                    }
                                }
                                db.collection("orders")
                                        .get()
                                        .addOnCompleteListener { result ->
                                            for(document in result.result!!) {
                                                listPrices.add(document.getDouble("totalPrice").toString())
                                                listClient.add(document.getString("client").toString())
                                            }
                                            /*for(i in 0 until listReq.size) {
                                                db.collection("users")
                                                        .document(listClient[i])
                                                        .get()
                                                        .addOnSuccessListener { document ->
                                                            val name = document.getString("nome").toString()
                                                            val surname = document.getString("cognome").toString()
                                                            val via = document.getString("indirizzo.Via").toString()
                                                            val provincia = document.getString("indirizzo.Provincia").toString()
                                                            val cap = document.getString("indirizzo.CAP").toString()
                                                            val citta = document.getString("indirizzo.Citta").toString()
                                                            val req = listReq[i]
                                                            val price = listPrices[i]

                                                            val r = Request(name, surname, citta, cap, provincia, via, price, req)
                                                            arrayList.add(r)
                                                        }
                                            }*/
                                            for(i in 0 until listReq.size) {
                                                db.collection("users")
                                                    .get()
                                                    .addOnCompleteListener { document ->
                                                        if(document.isSuccessful) {
                                                            for(document in document.result!!) {
                                                                if(document.id == listClient[i]) {
                                                                    val name = document.getString("nome").toString()
                                                                    val surname = document.getString("cognome").toString()
                                                                    val via = document.getString("indirizzo.Via").toString()
                                                                    val provincia = document.getString("indirizzo.Provincia").toString()
                                                                    val cap = document.getString("indirizzo.CAP").toString()
                                                                    val citta = document.getString("indirizzo.Citta").toString()
                                                                    val req = listReq[i]
                                                                    val price = listPrices[i]

                                                                    val r = Request(name, surname, citta, cap, provincia, via, price, req)
                                                                    arrayList.add(r)
                                                                }
                                                            }
                                                        }

                                                    }
                                            }
                                            loadingPanel.visibility = RelativeLayout.GONE
                                            initRecyclerView()
                                            addDataSet()

                                        }
                            }
                }

        refreshReq.setOnRefreshListener {
            refreshReq.isRefreshing = false
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    private fun addDataSet() {
        requestsAdapter.submitList(arrayList)
    }

    private fun initRecyclerView() {
        recyView.apply {
            layoutManager = LinearLayoutManager(this@RequestsActivity)
            val topSpacingDecoration = TopSpacingItemDecoration(30, 0, 0)
            addItemDecoration(topSpacingDecoration)
            requestsAdapter = RequestsAdapter()
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView((recyView))
            ItemTouchHelper(itemTouchHelperCallback2).attachToRecyclerView((recyView))
            recyView.adapter = requestsAdapter
        }

    }

    private var itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(60)
                    declineRequest(viewHolder.adapterPosition)
                    arrayList.removeAt(viewHolder.adapterPosition)
                    requestsAdapter.notifyDataSetChanged()
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addBackgroundColor(ContextCompat.getColor(this@RequestsActivity, R.color.red))
                            .addActionIcon(R.drawable.ic_baseline_delete_forever_24)
                            .setSwipeLeftActionIconTint(R.color.white)
                            .create()
                            .decorate()

                    super.onChildDraw(c!!, recyclerView!!, viewHolder!!, dX, dY, actionState, isCurrentlyActive)
                }
            }

    private var itemTouchHelperCallback2: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(60)
                    acceptRequest(viewHolder.adapterPosition)
                    arrayList.removeAt(viewHolder.adapterPosition)
                    requestsAdapter.notifyDataSetChanged()
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addBackgroundColor(ContextCompat.getColor(this@RequestsActivity, R.color.md_green_500))
                            .addActionIcon(R.drawable.ic_yes)
                            .setSwipeRightActionIconTint(R.color.white)
                            .create()
                            .decorate()

                    super.onChildDraw(c!!, recyclerView!!, viewHolder!!, dX, dY, actionState, isCurrentlyActive)
                }
            }

    private fun declineRequest(pos:Int) {
        db.collection("requests")
                .document(arrayList[pos].req)
                .delete()
                .addOnSuccessListener {
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
                .addOnFailureListener { e -> Log.w(
                        "ShoppingCartActivity",
                        "Error deleting document",
                        e
                ) }
    }

    private fun acceptRequest(pos:Int) {
        db.collection("orders")
                .document(listOrd[pos])
                .update(mapOf("rider" to list[0]))

        declineRequest(pos)
    }
}

class Request(val name: String, val surname: String, val city: String, val cap: String, val provence: String, val via: String, val price: String, val req: String)