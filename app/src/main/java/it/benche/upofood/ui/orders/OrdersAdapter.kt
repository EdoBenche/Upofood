package it.benche.upofood.ui.orders

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.OrderActivity
import it.benche.upofood.ProductActivity
import it.benche.upofood.R
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.item_order.view.*
import kotlinx.android.synthetic.main.nav_header_main.*

class OrdersAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Order> = ArrayList()

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var numOrder: TextView? = null
        var client: TextView? = null
        var new: TextView? = null

        init {
            numOrder = itemView.findViewById(R.id.numberOrder)
            client = itemView.findViewById(R.id.client)
            new = itemView.findViewById(R.id.status)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OrderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is OrderViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(ordersList: List<Order>) {
        items = ordersList
    }

    class OrderViewHolder constructor(
            itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        val orderNumber = itemView.numberOrder
        val client = itemView.client
        val status = itemView.status
        val clickable = itemView.clickable
        lateinit var db: FirebaseFirestore

        fun bind(orderCard : Order) {
            orderNumber.text = "Ordine #${orderCard.number}"
            db = FirebaseFirestore.getInstance()
            db.collection("users").get().addOnSuccessListener { result ->
                for(document in result) {
                    if(document.id == orderCard.client) {
                        val name = document.getString("nome").toString()
                        val surname = document.getString("cognome").toString()
                        client.text = "$name $surname"
                    }
                }
            }

            if(orderCard.isNew == "yes") {
                status.visibility = TextView.VISIBLE
            }

            when (orderCard.status) {
                "Consegnato al rider" -> {
                    status.text = "Consegnato al rider"
                    status.visibility = TextView.VISIBLE
                }
                "Preso in carico" -> {
                    status.text = "Preso in carico"
                    status.visibility = TextView.VISIBLE
                }
                "In preparazione" -> {
                    status.text = "In preparazione"
                    status.visibility = TextView.VISIBLE
                }
                "Non consegnato" -> {
                    status.text = "Non consegnato"
                    status.visibility = TextView.VISIBLE
                }
            }

            clickable.onClick {
                if(status.visibility == TextView.VISIBLE) {
                    db.collection("orders")
                            .document(orderCard.number)
                            .update(mapOf("isNew" to "no"))
                            .addOnSuccessListener {
                                status.visibility = TextView.GONE
                            }
                            .addOnFailureListener {  }
                }
                val intent: Intent = Intent(context, OrderActivity::class.java)
                intent.putExtra("ORDER_NUMBER", orderCard.number)
                intent.putExtra("ORDER_CLIENT", orderCard.client)
                ContextCompat.startActivity(context, intent, null)
            }
        }



    }


}