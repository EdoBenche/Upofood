package it.benche.upofood.ui.activeTrips

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.R
import it.benche.upofood.message.MessageListActivity
import it.benche.upofood.models.Chat
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.item_active_trip.view.*
import java.io.IOException
import kotlin.properties.Delegates


class ActiveTripsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Trips> = ArrayList()

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var client: TextView? = null
        var addr: TextView? = null
        var toPay: TextView? = null
        var delivered: CardView? = null
        var notDelivered: CardView? = null
        var road: CardView? = null
        var toChat: CardView? = null

        init {
            client = itemView.findViewById(R.id.nameClient)
            addr = itemView.findViewById(R.id.address)
            toPay = itemView.findViewById(R.id.toPay)
            delivered = itemView.findViewById(R.id.delivered)
            notDelivered = itemView.findViewById(R.id.notDelivered)
            road = itemView.findViewById(R.id.road)
            toChat = itemView.findViewById(R.id.toChat)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TripViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_active_trip, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is TripViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(tripsList: List<Trips>) {
        items = tripsList
    }

    class TripViewHolder constructor(
            itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        val client = itemView.nameClient
        val addr = itemView.address
        val toPay = itemView.toPay
        val delivered = itemView.delivered
        val notDelivered = itemView.notDelivered
        val road = itemView.road
        val toChat = itemView.toChat

        fun bind(tripCard: Trips) {
            val db = FirebaseFirestore.getInstance()
            client.text = tripCard.name
            addr.text = tripCard.address
            toPay.text = "Totale da pagare: ${tripCard.toPay}???"


            delivered.onClick {

                //DELETE CHAT WHEN DELIVER IS END
                val mAuth = FirebaseAuth.getInstance().uid
                val ref = FirebaseDatabase.getInstance().getReference("Chats")
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(data in snapshot.children) {
                            val chat: Chat = data.getValue(Chat::class.java) as Chat
                            if((chat.getReceiver() == mAuth && chat.getSender() == tripCard.clientId) || (chat.getReceiver() == tripCard.clientId && chat.getSender() == mAuth)) {
                                data.ref.removeValue()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })


                db.collection("orders")
                        .document(tripCard.order)
                        .update(mapOf(
                                "isDelivered" to "yes",
                                "isPaid" to "yes",
                                "deliveryState" to "Consegnato"
                        ))
                        .addOnSuccessListener {
                            Toast.makeText(context, "Consegnato, swipe to refresh", Toast.LENGTH_LONG).toString()
                        }
            }

            notDelivered.onClick {
                db.collection("orders")
                        .document(tripCard.order)
                        .update(mapOf(
                                "deliveryState" to "Non consegnato",
                                "isDelivered" to "yes"
                        ))
                        .addOnSuccessListener {
                            Toast.makeText(context, "Non consegnato, swipe to refresh", Toast.LENGTH_LONG).toString()

                            db.collection("orders")
                                .document(tripCard.order)
                                .collection("products")
                                .get()
                                .addOnCompleteListener { task ->
                                    if(task.isSuccessful) {
                                        for(document in task.result!!) {
                                            val qty = document.getLong("qty")!!.toInt()
                                            db.collection("products")
                                                .document(document.id)
                                                .get()
                                                .addOnSuccessListener { r ->
                                                    val totalQty: Int = r.getString("quantita")!!.toInt()
                                                    val newQty = totalQty + qty
                                                    db.collection("products")
                                                        .document(document.id)
                                                        .update("quantita", newQty.toString())
                                                }
                                        }
                                    }
                                }
                        }
            }

            road.onClick {
                var latitude by Delegates.notNull<Double>()
                var longitude by Delegates.notNull<Double>()
                db.collection("users")
                        .document(tripCard.clientId)
                        .get()
                        .addOnSuccessListener { document ->
                            val via = document.getString("indirizzo.Via").toString()
                            val cap = document.getString("indirizzo.CAP").toString()
                            val city = document.getString("indirizzo.Citta").toString()

                            var geocodeMatches: List<Address>? = null

                            try {
                                geocodeMatches = Geocoder(context).getFromLocationName(
                                        "${via}, ${city}, $cap", 1
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                            if (geocodeMatches != null) {
                                latitude = geocodeMatches[0].latitude
                                longitude = geocodeMatches[0].longitude
                            }
                            val intent = Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?daddr=${latitude},${longitude}"))
                            startActivity(context, intent, null)
                        }
            }

            toChat.onClick {
                val mAuth = FirebaseAuth.getInstance().uid
                val intent = Intent(context, MessageListActivity::class.java)
                intent.putExtra("SENDER", mAuth.toString())
                intent.putExtra("RECEIVER", tripCard.clientId)
                startActivity(context, intent, null)
            }
        }


    }
}