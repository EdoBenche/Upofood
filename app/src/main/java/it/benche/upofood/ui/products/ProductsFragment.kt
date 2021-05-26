package it.benche.upofood.ui.products

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.R
import it.benche.upofood.utils.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_requests.*
import kotlinx.android.synthetic.main.activity_requests.loadingPanel
import kotlinx.android.synthetic.main.activity_shopping_cart.*
import kotlinx.android.synthetic.main.activity_shopping_cart.myListView
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class ProductsFragment: Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var db: FirebaseFirestore
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var arrayList: ArrayList<Product>

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_products, container, false)


        db = FirebaseFirestore.getInstance()

        arrayList = ArrayList()
        getData()

        swipeRefreshLayout = view.findViewById(R.id.refresh)
        swipeRefreshLayout.setOnRefreshListener(this)

        return view
    }

    @Suppress("DEPRECATION")
    override fun onRefresh() {
        refresh.isRefreshing = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fragmentManager?.beginTransaction()?.detach(this)?.commitNow()
            fragmentManager?.beginTransaction()?.attach(this)?.commitNow()
        } else {
            fragmentManager?.beginTransaction()?.detach(this)?.attach(this)?.commit()
        }

    }

    private fun getData() {
        db.collection("products")
                    .get()
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            for (document in task.result!!) {
                                val name: String = document.getString("nomeProdotto").toString()
                                val description: String = document.getString("descrizione").toString()
                                val category: String = document.getString("categoria").toString()
                                val price: Double = document.getString("prezzo")!!.toDouble()
                                val qty: Int = document.getString("quantita")!!.toInt()
                                val img: String = document.getString("img").toString()

                                val p = Product(name, description, category, price, qty, img)
                                arrayList.add(p)
                            }
                            loadingPanel.visibility = RelativeLayout.GONE
                            initRecyclerView()
                            addDataSet()
                        } else {
                            Log.w(TAG, "Error getting documents.", task.exception)
                        }
                    }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        addDataSet()
    }

    private fun addDataSet() {
        productsAdapter.submitList(arrayList)
    }

    private fun initRecyclerView() {
        myListView.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(10, 5, 5)
            addItemDecoration(topSpacingDecoration)
            productsAdapter = ProductsAdapter()
            myListView.adapter = productsAdapter
        }

    }
}

class Product(val name: String, val description: String, val category: String, val price: Double, val qty: Int, val img: String)