package it.benche.upofood.rider

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.benche.upofood.R
import kotlinx.android.synthetic.main.item_old_order_manager.view.*

class MyOldTripsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<OldTrips> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OldTripViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_old_order_rider, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is OldTripViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(oldTripsList: List<OldTrips>) {
        items = oldTripsList
    }

    class OldTripViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {

        private val nOrder: TextView = itemView.numberOrder
        private val client: TextView = itemView.client
        private val date: TextView = itemView.date
        private val status: TextView = itemView.isDelivered

        @SuppressLint("SetTextI18n")
        fun bind(oldTripCard : OldTrips) {
            nOrder.text = "Ordine #${oldTripCard.number}"
            date.text = oldTripCard.date
            client.text = oldTripCard.client
            status.text = oldTripCard.status
            if(oldTripCard.status == "Consegnato") {
                status.setTextColor(Color.parseColor("#32CD32"))
            } else if (oldTripCard.status == "Non consegnato") {
                status.setTextColor(Color.parseColor("#FF0000"))
            }
        }
    }
}