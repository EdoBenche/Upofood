package it.benche.upofood.manager

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.benche.upofood.R
import kotlinx.android.synthetic.main.item_old_order_manager.view.*

class OldOrdersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Order> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OldOrderViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_old_order_manager, parent, false)
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
        private val client: TextView = itemView.client
        private val date: TextView = itemView.date
        private val total: TextView = itemView.price
        private val status: TextView = itemView.isDelivered
        private val r1: TextView = itemView.ratingQuality
        private val r2: TextView = itemView.ratingFast
        private val r3: TextView = itemView.ratingCourtesy

        @SuppressLint("SetTextI18n")
        fun bind(oldOrderCard : Order) {
            nOrder.text = "Ordine #${oldOrderCard.number}"
            date.text = oldOrderCard.date
            client.text = oldOrderCard.client
            total.text = "${oldOrderCard.totalPrice}€"
            status.text = oldOrderCard.status
            if(oldOrderCard.status == "Consegnato") {
                status.setTextColor(Color.parseColor("#32CD32"))
            } else if (oldOrderCard.status == "Non consegnato") {
                status.setTextColor(Color.parseColor("#FF0000"))
            }
            r1.text = "Qualità merce: ${oldOrderCard.rating1}"
            r2.text = "Velocità consegna: ${oldOrderCard.rating2}"
            r3.text = "Cortesia rider: ${oldOrderCard.rating3}"
        }
    }
}