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

@Suppress("DEPRECATION")
class RidersPositionActivity: AppCompatActivity(), OnMapReadyCallback {

    private lateinit var database: FirebaseFirestore
    private lateinit var db: FirebaseDatabase
    private lateinit var arrayList: ArrayList<Rider>

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap

    var latitudeRider  by Delegates.notNull<Double>()
    var longitudeRider  by Delegates.notNull<Double>()

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
                                        val data = snapshot.value.toString()
                                        val lat = data.toDouble()
                                        val lon = db.getReference("riders").child(document.id).child("lng")
                                        lon.addValueEventListener(object: ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if(snapshot.exists()) {
                                                    val data = snapshot.value.toString()
                                                    val lon = data.toDouble()
                                                    val r = Rider(name, number!!, lat, lon)
                                                    arrayList.add(r)
                                                } else {
                                                    longitudeRider = 0.0
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                TODO()
                                            }
                                        })
                                    } else {
                                        latitudeRider = 0.0
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

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapRiders) as SupportMapFragment
        mapFragment.onCreate(null)
        mapFragment.onResume()
        mapFragment.getMapAsync(this)

        goBack.onClick {
            onBackPressed()
        }
        refresh.onClick {
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        Handler().postDelayed({
            MapsInitializer.initialize(mapFragment.requireContext())
            if (googleMap != null) {
                mMap = googleMap
            }
            mMap.uiSettings.isZoomControlsEnabled = false
            for(i in 0 until arrayList.size) {
                //if(rider.lat != 0.0 && rider.lng != 0.0) {
                    val lat = arrayList[i].lat
                    val lng = arrayList[i].lng
                    val bord = LatLng(lat, lng)
                    mMap.addMarker(
                        MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_rider))
                            .position(
                                bord
                            ).title(arrayList[i].name)
                    )
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bord, 13.0f))
                //}
            } }, 2500)
    }

}

class Rider(val name: String, val number: Int, val lat: Double, val lng: Double)