package it.benche.upofood.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.R
import it.benche.upofood.Rider
import it.benche.upofood.RidersActivity
import it.benche.upofood.client.Order
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.item_old_order.view.*
import kotlinx.android.synthetic.main.item_rider.view.*

class OldOrdersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Order> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OldOrderViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_old_order, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is OldOrderViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(oldOrdersList: List<Order>) {
        items = oldOrdersList
    }

    class OldOrderViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {

        private val nOrder: TextView = itemView.numberOrder
        private val date: TextView = itemView.date
        private val total: TextView = itemView.totalPrice

        fun bind(oldOrderCard : Order) {
            nOrder.text = "Ordine #${oldOrderCard.number}"
            date.text = oldOrderCard.date
            total.text = "${oldOrderCard.totalPrice}€"
        }
    }
}