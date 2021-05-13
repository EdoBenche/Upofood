package it.benche.upofood.manager

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.R
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.activity_position_riders.*
import java.util.ArrayList
import kotlin.properties.Delegates

class RidersPositionActivity: AppCompatActivity(), OnMapReadyCallback {

    private lateinit var database: FirebaseFirestore
    private lateinit var db: FirebaseDatabase
    private lateinit var arrayList: ArrayList<Rider>

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap

    private var latitudeRider = 0.0
    private var longitudeRider = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_position_riders)

        database = FirebaseFirestore.getInstance()
        db = FirebaseDatabase.getInstance()
        arrayList = ArrayList()

        database.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    for(document in task.result!!) {
                        if(document.getString("account").toString() == "Rider" && document.getString("available").toString() == "yes") {
                            val name = document.getString("nome").toString()
                            val number = document.getLong("numeroRider")?.toInt()
                            val lat = db.getReference("riders").child(document.id).child("lat")
                            lat.addValueEventListener(object: ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()) {
                                        Toast.makeText(applicationContext, snapshot.value.toString(), Toast.LENGTH_SHORT).show()
                                        val data = snapshot.value.toString()
                                        latitudeRider = data.toDouble()
                                    } else {
                                        latitudeRider = 0.0
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO()
                                }
                            })

                            val lon = db.getReference("riders").child(document.id).child("lng")
                            lon.addValueEventListener(object: ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()) {
                                        Toast.makeText(applicationContext, snapshot.value.toString(), Toast.LENGTH_SHORT).show()
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

                            val r = Rider(name, number!!, latitudeRider, longitudeRider)
                            arrayList.add(r)
                        }
                    }
                }
            }

        goBack.onClick {
            onBackPressed()
        }
    }
    override fun onMapReady(googleMap: GoogleMap?) {
        Handler().postDelayed({
            MapsInitializer.initialize(mapFragment.requireContext())
            if (googleMap != null) {
                mMap = googleMap
            }
            mMap.uiSettings.isZoomControlsEnabled = false

            for(rider in arrayList) {
                //if(rider.lat != 0.0 && rider.lng != 0.0) {
                    val lat = rider.lat
                    val lng = rider.lng
                    val bord = LatLng(lat, lng)
                    mMap.addMarker(
                        MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_rider))
                            .position(
                                bord
                            ).title(rider.name)
                    )
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bord, 13.0f))
                //}
            } }, 3000)
    }

}

class Rider(val name: String, val number: Int, val lat: Double, val lng: Double)