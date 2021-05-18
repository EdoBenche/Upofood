package it.benche.upofood.ui.orders

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.R
import it.benche.upofood.ui.home.HomeViewModel
import it.benche.upofood.utils.CartAdapter
import it.benche.upofood.utils.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_shopping_cart.*
import kotlinx.android.synthetic.main.activity_shopping_cart.myListView
import kotlinx.android.synthetic.main.fragment_list_trips.*
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.android.synthetic.main.fragment_orders.refresh

class OrdersFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var ordersAdapter: OrdersAdapter
    private lateinit var arrayList: ArrayList<Order>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_orders, container, false)

        db = FirebaseFirestore.getInstance()

        arrayList = ArrayList()

        db.collection("orders")
                .get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        for(document in task.result!!) {
                            if(document.getString("deliveryState").toString() != "Consegnato" && document.getString("deliveryState").toString() != "Non consegnato") {
                                val numOrder: String = document.id
                                val clientID: String = document.getString("client").toString()
                                val isNew: String = document.getString("isNew").toString()
                                val status: String = document.getString("deliveryState").toString()

                                val o = Order(numOrder, clientID, isNew, status)
                                arrayList.add(o)
                            }
                            loadingPanel2.visibility = RelativeLayout.GONE
                            initRecyclerView()
                            addDataSet()
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }

        swipeRefreshLayout = view.findViewById(R.id.refresh)
        swipeRefreshLayout.setOnRefreshListener(this)

        return view
    }

    @Suppress("DEPRECATION")
    override fun onRefresh() {
        refresh.isRefreshing = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fragmentManager?.beginTransaction()?.detach(this)?.commitNow();
            fragmentManager?.beginTransaction()?.attach(this)?.commitNow();
        } else {
            fragmentManager?.beginTransaction()?.detach(this)?.attach(this)?.commit();
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        addDataSet()
    }

    private fun addDataSet() {
        ordersAdapter.submitList(arrayList)
    }

    private fun initRecyclerView() {
        myListView.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(5,0,0)
            addItemDecoration(topSpacingDecoration)
            ordersAdapter = OrdersAdapter()
            myListView.adapter = ordersAdapter
        }

    }
}

class Order(val number: String, val client: String, val isNew: String, val status: String)