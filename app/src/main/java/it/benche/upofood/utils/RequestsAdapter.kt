package it.benche.upofood.utils

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import it.benche.upofood.R
import it.benche.upofood.Request
import it.benche.upofood.RequestsActivity
import it.benche.upofood.client.ActiveOrderActivity
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.item_request.view.*
import java.io.IOException
import kotlin.coroutines.coroutineContext
import kotlin.properties.Delegates


class RequestsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Request> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RequestViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is RequestViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(requestsList: List<Request>) {
        items = requestsList
    }

    class RequestViewHolder constructor(
            itemView: View
    ): RecyclerView.ViewHolder(itemView), OnMapReadyCallback {

        private val clientName: TextView = itemView.nameClient
        private val clientAddress: TextView = itemView.address
        private val pay: TextView = itemView.toPay
        private val map: MapView = itemView.map
        private lateinit var mMap: GoogleMap
        private lateinit var nameofclient: String
        var latitude by Delegates.notNull<Double>()
        var longitude by Delegates.notNull<Double>()

        fun bind(requestCard: Request) {
            nameofclient = "${requestCard.name} ${requestCard.surname}"
            clientName.text = "${requestCard.name} ${requestCard.surname}"
            clientAddress.text = "${requestCard.via}, ${requestCard.cap}, ${requestCard.city} (${requestCard.provence})"
            pay.text ="Totale da pagare: ${requestCard.price}€"
            var geocodeMatches: List<Address>? = null

            try {
                geocodeMatches = Geocoder(map.context).getFromLocationName(
                    "${requestCard.via}, ${requestCard.city}, ${requestCard.cap}", 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (geocodeMatches != null) {
                latitude = geocodeMatches[0].latitude
                longitude = geocodeMatches[0].longitude
            }
            map.onCreate(null)
            map.onResume()
            map.getMapAsync(this)


            clientName.onClick {
                startActivity(context, Intent(context, ActiveOrderActivity::class.java), null)
            }

        }

        override fun onMapReady(googleMap: GoogleMap) {
            MapsInitializer.initialize(map.context!!)
            mMap = googleMap

            mMap.uiSettings.isZoomControlsEnabled = true
            val lat1 = latitude
            val lng1 = longitude
            val bord = LatLng(lat1, lng1)
            mMap.addMarker(MarkerOptions().position(bord).title("Cliente").snippet("${nameofclient}"))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bord, 13.0f))
        }
    }
}