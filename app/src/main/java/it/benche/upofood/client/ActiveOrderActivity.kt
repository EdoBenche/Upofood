package it.benche.upofood.client

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import it.benche.upofood.DrawerActivityClient
import it.benche.upofood.ProfileActivity
import it.benche.upofood.R
import it.benche.upofood.message.MessageListActivity
import it.benche.upofood.utils.ListOrderAdapter
import it.benche.upofood.utils.TopSpacingItemDecoration
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.activity_active_order.*
import kotlinx.android.synthetic.main.activity_shopping_cart.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.item_request.view.*
import java.io.IOException
import kotlin.properties.Delegates


@Suppress("DEPRECATION")
class ActiveOrderActivity: AppCompatActivity(), OnMapReadyCallback {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var mMap: GoogleMap
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    var latitude by Delegates.notNull<Double>()
    var longitude by Delegates.notNull<Double>()
    var latitudeRider  /*by Delegates.notNull<Double>()*/ = 0.0
    var longitudeRider  /*by Delegates.notNull<Double>()*/ = 0.0
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var arrayList: ArrayList<Product>

    private lateinit var idRider: ArrayList<String>

    private lateinit var listOrderAdapter: ListOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_order)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        val linearLayout = findViewById<LinearLayout>(R.id.design_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout)
        bottomSheetBehavior.peekHeight = 250
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        idRider = ArrayList()
        arrayList = ArrayList()
        val order = intent.getStringExtra("ORDER")
        db.collection("orders")
                .document(order.toString())
                .collection("products")
                .get()
                .addOnCompleteListener { result ->
                    if(result.isSuccessful) {
                        for(document in result.result!!) {
                            val name = document.getString("prodotto").toString()
                            val qty = document.getLong("qty").toString()
                            val p = Product(name, qty)
                            arrayList.add(p)
                        }
                    }
                }
        applyInfoOrder()
        Handler().postDelayed({
            initRecyclerView()
            addDataSet()
        }, 1000)


        refresh.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        home.setOnClickListener {
            val ratingQuality = rating1.rating
            val ratingFast = rating2.rating
            val ratingCourtesy = rating3.rating

            db.collection("orders")
                    .document(intent.getStringExtra("ORDER").toString())
                    .update(mapOf(
                        "ratingQuality" to ratingQuality.toString(),
                        "ratingFast" to ratingFast.toString(),
                        "ratingCourtesy" to ratingCourtesy.toString(),
                        "isDelivered" to "yes"
                    ))
                    .addOnSuccessListener {
                        finish()
                        startActivity(Intent(this, DrawerActivityClient::class.java))
                    }
        }

        homeBad.setOnClickListener {
            db.collection("orders")
                .document(intent.getStringExtra("ORDER").toString())
                .update(mapOf(
                    "isDelivered" to "yes"
                ))
                .addOnSuccessListener {
                    startActivity(Intent(this, DrawerActivityClient::class.java))
                }

        }

        toChat.onClick {
            val ord = intent.getStringExtra("ORDER")
            val client = mAuth.uid
            val rider = idRider[0]
            val intent = Intent(context, MessageListActivity::class.java)
            intent.putExtra("ORDER", ord)
            intent.putExtra("SENDER", client)
            intent.putExtra("RECEIVER", rider)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {

    }

    @SuppressLint("SetTextI18n")
    private fun applyInfoOrder() {
        val client = intent.getStringExtra("CLIENT")
        val order = intent.getStringExtra("ORDER")
        val rider = intent.getStringExtra("RIDER")

        db.collection("users")
                .document(client.toString())
                .get()
                .addOnSuccessListener { document ->
                    val via = document.getString("indirizzo.Via").toString()
                    val cap = document.getString("indirizzo.CAP").toString()
                    val city = document.getString("indirizzo.Citta").toString()

                    var geocodeMatches: List<Address>? = null

                    try {
                        geocodeMatches = Geocoder(map.context).getFromLocationName(
                            "${via}, ${city}, $cap", 1
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    if (geocodeMatches != null) {
                        latitude = geocodeMatches[0].latitude
                        longitude = geocodeMatches[0].longitude
                    }
                    mapFragment = supportFragmentManager
                            .findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.onCreate(null)
                    mapFragment.onResume()
                    mapFragment.getMapAsync(this)
                }

        db.collection("orders")
                .document(order.toString())
                .get()
                .addOnSuccessListener { document ->
                    if(document.getString("rider").toString() == "") {
                        infoRider.visibility = LinearLayout.GONE
                    }
                    totOrdine.text = "Totale ordine: ${document.getDouble("totalPrice")}€"
                    when {
                        document.getString("deliveryState") == "Wait" -> {
                            titleState.text = "Attendi"
                            descriptionState.text = "Il tuo ordine non è ancora stato processato"
                            pBar.progress = 0
                            //lottie.setAnimation("@raw/guy_waiting.json")
                        }
                        document.getString("deliveryState") == "Preso in carico" -> {
                            titleState.text = "Preso in carico"
                            descriptionState.text = "Abbiamo visto il tuo ordine"
                            pBar.progress = 20
                        }
                        document.getString("deliveryState") == "In preparazione" -> {
                            titleState.text = "In preparazione"
                            descriptionState.text = "Stiamo preparando il tuo ordine"
                            pBar.progress = 50
                        }
                        document.getString("deliveryState") == "Consegnato al rider" -> {
                            titleState.text = "Consegnato al rider"
                            descriptionState.text = "Tra pochi minuti il nostro rider sarà da te"
                            pBar.progress = 80
                        }
                        document.getString("deliveryState") == "Consegnato" -> {
                            titleState.text = "Consegnato"
                            descriptionState.text = "L'ordine è stato consegnato con successo!"
                            pBar.progress = 100
                            finish.visibility = LinearLayout.VISIBLE
                        }
                        document.getString("deliveryState") == "Non consegnato" -> {
                            titleState.text = "Non consegnato"
                            descriptionState.text = "Il nostro rider non è riuscito a portare a termine la tua consegna"
                            pBar.progress = 0
                            finishBad.visibility = LinearLayout.VISIBLE
                        }
                    }
                }

        if(rider.toString() != "") {
            db.collection("users")
                    .get()
                    .addOnCompleteListener { result ->
                        if(result.isSuccessful) {
                            for(document: QueryDocumentSnapshot in result.result!!) {
                                if(document.getLong("numeroRider").toString() == rider.toString()) {
                                    nameRider.text = document.getString("nome").toString()

                                    if(document.getString("vehicle").toString() != "null") {
                                        infoVehicle.text = "Il rider arriverà in ${document.getString("vehicle").toString()}"
                                        infoVehicle.visibility = TextView.VISIBLE
                                    }

                                    idRider.add(document.id)
                                    if(document.getLong("numeroRider").toString() != "") {
                                        val database = FirebaseDatabase.getInstance()
                                        val lat = database.getReference("riders").child(idRider[0]).child("lat")
                                        lat.addValueEventListener(object: ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if(snapshot.exists()) {
                                                    val data = snapshot.value.toString()
                                                    latitudeRider = data.toDouble()
                                                    updateMap()
                                                } else {
                                                    latitudeRider = 0.0
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                TODO()
                                            }
                                        })
                                        val lon = database.getReference("riders").child(idRider[0]).child("lng")
                                        lon.addValueEventListener(object: ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if(snapshot.exists()) {
                                                    val data = snapshot.value.toString()
                                                    longitudeRider = data.toDouble()
                                                } else {
                                                    longitudeRider = 0.0
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                TODO()
                                            }
                                        })
                                    }
                                }
                            }
                        }
                    }
        } else {
            latitudeRider = 0.0
            longitudeRider = 0.0
        }



    }

    private fun addDataSet() {
        listOrderAdapter.submitList(arrayList)
    }

    private fun initRecyclerView() {
        recView.apply {
            layoutManager = LinearLayoutManager(
                this@ActiveOrderActivity,
                RecyclerView.HORIZONTAL,
                false
            )
            val topSpacingDecoration = TopSpacingItemDecoration(10, 0, 0)
            addItemDecoration(topSpacingDecoration)
            listOrderAdapter = ListOrderAdapter()
            recView.adapter = listOrderAdapter
        }

    }

    private fun updateMap() {
        Handler().postDelayed({
            val lat1 = latitude
            val lng1 = longitude
            val bord = LatLng(lat1, lng1)

            val lat2 = latitudeRider
            val lng2 = longitudeRider
            val bord2 = LatLng(lat2, lng2)
            mMap.clear()
            mMap.addMarker(
                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).position(
                    bord
                ).title("La tua posizione")
            )
            mMap.addMarker(
                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.dman))
                    .position(
                        bord2
                    ).title("Rider")
            )}, 2000)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        Handler().postDelayed({MapsInitializer.initialize(mapFragment.requireContext())
            mMap = googleMap
            mMap.uiSettings.isZoomControlsEnabled = false

            val lat1 = latitude
            val lng1 = longitude
            val bord = LatLng(lat1, lng1)

            val lat2 = latitudeRider
            val lng2 = longitudeRider

            mMap.addMarker(
                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).position(
                    bord
                ).title("La tua posizione")
            )

            if(lat2 != 0.0 && lng2 != 0.0) {
                val bord2 = LatLng(lat2, lng2)
                mMap.addMarker(
                    MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_rider))
                        .position(
                            bord2
                        ).title("Rider")
                )
            }

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bord, 13.0f))}, 1000)



    }
}

class Product(val name: String, val qty: String)