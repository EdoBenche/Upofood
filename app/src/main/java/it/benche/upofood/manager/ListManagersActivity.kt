package it.benche.upofood.manager

import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.R
import it.benche.upofood.utils.RecapProductsAdapter
import it.benche.upofood.utils.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_chat_with_manager.*
import kotlinx.android.synthetic.main.activity_order.*

class ListManagersActivity: AppCompatActivity() {

    private lateinit var arrayList: ArrayList<Manager>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var managerAdapter: ListManagersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_with_manager)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        arrayList = ArrayList()
        getData()
    }

    fun getData() {
        db.collection("users")
                .get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        for(document in task.result!!) {
                            if(document.getString("account").toString() == "Gestore") {
                                val name = document.getString("nome").toString()
                                val surname = document.getString("cognome").toString()
                                val idManager = document.id
                                val m = Manager(name, surname, idManager)
                                arrayList.add(m)
                            }
                        }
                        loading.visibility = RelativeLayout.GONE
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

    private fun addDataSet() {
        managerAdapter.submitList(arrayList)
    }

    private fun initRecyclerView() {
        recViewMan.apply {
            layoutManager = LinearLayoutManager(this@ListManagersActivity)
            val topSpacingDecoration = TopSpacingItemDecoration(30, 0, 0)
            addItemDecoration(topSpacingDecoration)
            managerAdapter = ListManagersAdapter()
            recViewMan.adapter = managerAdapter
        }

    }
}

class Manager(val name: String, val surname: String, val id: String)