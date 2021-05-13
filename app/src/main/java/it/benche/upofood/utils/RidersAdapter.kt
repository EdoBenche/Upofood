package it.benche.upofood.utils

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.*
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.item_product_cart.view.*
import kotlinx.android.synthetic.main.item_rider.view.*

class RidersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Rider> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RiderViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_rider, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is RiderViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(riderList: List<Rider>) {
        items = riderList
    }

    class RiderViewHolder constructor(
            itemView: View
    ): RecyclerView.ViewHolder(itemView) {

        private val riderName: TextView = itemView.nameRider
        private val riderNumber: TextView = itemView.numberRider
        private val card: LinearLayout = itemView.card

        fun bind(riderCard : Rider) {
            riderName.text = "${riderCard.nome} ${riderCard.cognome}"
            riderNumber.text = riderCard.numeroRider

            card.onClick {
                Toast.makeText(context, "La richiesta è stata mandata al rider, attendi che quest'ultimo confermi", Toast.LENGTH_LONG).show()
                val db: FirebaseFirestore = FirebaseFirestore.getInstance()
                val rnds = (10000000..999999999).random()
                val infos = mapOf(
                        "order" to riderCard.ordine,
                        "rider" to riderCard.numeroRider,
                        "numberRequest" to rnds.toString()
                )
                db.collection("requests")
                        .document(rnds.toString())
                        .set(infos)

                (context as RidersActivity).finish()
            }
        }
    }
}