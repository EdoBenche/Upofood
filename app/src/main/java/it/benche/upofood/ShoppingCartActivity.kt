package it.benche.upofood

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.utils.CartAdapter
import it.benche.upofood.utils.TopSpacingItemDecoration
import it.benche.upofood.utils.extensions.onClick
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_shopping_cart.*
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.confirm_order.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ShoppingCartActivity: AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth
    lateinit var uid: String
    lateinit var arrayList: ArrayList<Product>
    var totalPrice: Double = 0.0
    lateinit var casual: String
    lateinit var vibrator: Vibrator

    private lateinit var productAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        var products: Array<Product>
        arrayList = ArrayList()
        uid = mAuth.uid.toString()

        if (uid != null) {
            db.collection("carts")
                    .document(uid)
                    .collection("cart")
                    .get()
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            for (document in task.result!!) {
                                val product: String = document.getString("prodotto").toString()
                                val qty: Int = document.getLong("qty")!!.toInt()
                                val singlePrice: Double? = document.getDouble("price")

                                totalPrice = (totalPrice + (qty * singlePrice!!))

                                val p = Product(product, qty)
                                arrayList.add(p)
                            }
                            total.text = "$totalPrice €"
                            if(task == null) {
                                empty.visibility = TextView.VISIBLE
                            }

                        } else {
                            Log.w(
                                    "ShoppingCartActivity",
                                    "Error getting documents.",
                                    task.exception
                            )
                        }
                    }



            Handler().postDelayed({
                initRecyclerView()
                addDataSet()
            }, 1000)


            /*Handler().postDelayed({products = arrayList.toTypedArray()
            val myList = findViewById<ListView>(R.id.myListView)
            myList.adapter = ShoppingCartAdapter(this, R.layout.item_product_cart, products)}, 2000)*/
        }


        btnSubmitOrder.onClick {
            if(totalPrice != 0.0) {
                submitOrder()
            }
            else {
                Toast.makeText(context, "Il carrello è vuoto!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addDataSet() {
        productAdapter.submitList(arrayList)

        if(totalPrice == 0.0) {
            empty.visibility = TextView.VISIBLE
        }
    }

    private fun initRecyclerView() {
        myListView.apply {
            layoutManager = LinearLayoutManager(this@ShoppingCartActivity)
            val topSpacingDecoration = TopSpacingItemDecoration(30,0,0)
            addItemDecoration(topSpacingDecoration)
            productAdapter = CartAdapter()
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(myListView)
            myListView.adapter = productAdapter
        }

    }

    private fun removeFromCart(pos: Int) {
        db.collection("carts")
            .document(uid)
            .collection("cart")
            .document(arrayList[pos].prodotto)
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

    private var itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(60)
                removeFromCart(viewHolder.adapterPosition)
                arrayList.removeAt(viewHolder.adapterPosition)
                productAdapter.notifyDataSetChanged()
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(this@ShoppingCartActivity, R.color.red))
                        .addActionIcon(R.drawable.ic_baseline_delete_forever_24)
                        .create()
                        .decorate()

                super.onChildDraw(c!!, recyclerView!!, viewHolder!!, dX, dY, actionState, isCurrentlyActive)
            }
        }




    private fun submitOrder() {
        val numberOrder = UUID.randomUUID().toString()
        casual = Random().nextInt(999999).toString()

        for(product1 in arrayList) {
            val product = hashMapOf(
                    "prodotto" to product1.prodotto,
                    "qty" to product1.qty
            )
            /*db.collection("orders")
                    .document(uid)
                    .collection(casual)
                    .document(product1.prodotto)
                    .set(product)
                    .addOnSuccessListener {
                        //updateWarehouse()
                        resetCart()
                        confirmed()
                    }
                    .addOnFailureListener { e -> Log.w(
                        "ShoppingCartActivity",
                        "Error adding document",
                        e
                    ) }*/

            db.collection("orders")
                .document(casual)
                .collection("products")
                .document(product1.prodotto)
                .set(product)
                .addOnSuccessListener {
                    //updateWarehouse()
                    resetCart()
                    confirmed()
                }
                .addOnFailureListener { e -> Log.w(
                        "ShoppingCartActivity",
                        "Error adding document",
                        e
                ) }

        }

        val info = hashMapOf(
                "totalPrice" to totalPrice,
                "dateOrder" to SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()),
                "rider" to "",
                "isNew" to "yes",
                "isPaid" to "no",
                "isDelivered" to "no",
                "deliveryState" to "Wait",
                "client" to uid
        )
        /*db.collection("orders")
                .document(uid)
                .collection(casual)
                .document("infoOrder")
                .set(info).addOnSuccessListener {

                }
                .addOnFailureListener { e -> Log.w(
                    "ShoppingCartActivity",
                    "Error adding document",
                    e
                ) }*/

        db.collection("orders")
            .document(casual)
            .set(info).addOnSuccessListener {

            }
            .addOnFailureListener { e -> Log.w(
                    "ShoppingCartActivity",
                    "Error adding document",
                    e
            ) }

    }

    private fun updateWarehouse() {
        var qtNew = 0
        for(product1 in arrayList) {
            db.collection("products")
                .get().addOnSuccessListener { result ->
                        for (document in result) {
                            if(document.id == product1.prodotto)
                            {
                                val qty: Int = document.getString("quantita")!!.toInt()
                                qtNew = qty - product1.qty
                            }
                        }
                }

            db.collection("products")
                    .document(product1.prodotto)
                    .update("quantita", qtNew.toString())
        }
    }

    private fun resetCart() {

        for(product in arrayList) {
            db.collection("carts")
                    .document(uid)
                    .collection("cart")
                    .document(product.prodotto)
                    .delete()
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener { e -> Log.w(
                            "ShoppingCartActivity",
                            "Error deleting document",
                            e
                    ) }
        }

        db.collection("carts")
                .document(uid)
                .delete()
                .addOnSuccessListener {
                }
                .addOnFailureListener { e -> Log.w(
                        "ShoppingCartActivity",
                        "Error deleting document",
                        e
                ) }

    }

    @SuppressLint("SetTextI18n")
    private fun confirmed() {
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.setContentView(R.layout.confirm_order)
        dialog.findViewById<TextView>(R.id.confirmedOrder)!!.text = "L\'ordine #${casual} è stato elaborato con successo"
        dialog.window?.setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()

        Handler().postDelayed({
            startActivity(Intent(this, DrawerActivityClient::class.java))
        }, 6000)

    }
}

class Product(val prodotto: String, val qty: Int)