package it.benche.upofood.ui.activeTrips

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.Coordinate
import it.benche.upofood.R
import it.benche.upofood.utils.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_shopping_cart.*
import kotlinx.android.synthetic.main.activity_shopping_cart.myListView
import kotlinx.android.synthetic.main.fragment_list_trips.*

@Suppress("DEPRECATION")
open class ActiveTripsFragment : Fragment(), OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var activeTripsAdapter : ActiveTripsAdapter
    private lateinit var arrayList: ArrayList<Trips>
    private lateinit var numberRider: ArrayList<String>
    private lateinit var client: ArrayList<String>
    private lateinit var price: ArrayList<String>
    private lateinit var order: ArrayList<String>
    private var mGoogleApiClient: GoogleApiClient? = null
    private lateinit var mLocationRequest: LocationRequest
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    //private var mLocationCallback: LocationCallback = LocationCallback()
    private var mMap: GoogleMap? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_list_trips, container, false)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        arrayList = ArrayList()
        numberRider = ArrayList()
        client = ArrayList()
        price = ArrayList()
        order = ArrayList()

        db.collection("users")
            .document(mAuth.uid.toString())
            .get()
            .addOnSuccessListener { document ->
                numberRider.add(document.getLong("numeroRider").toString())

                db.collection("orders")
                    .get()
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            for(document in task.result!!) {
                                if(document.getString("rider") == numberRider[0] && document.getString("isPaid") == "no" && document.getString("deliveryState") != "Non consegnato") {
                                    client.add(document.getString("client").toString())
                                    price.add(document.getDouble("totalPrice").toString())
                                    order.add(document.id)
                                }
                            }
                            for(i in 0 until client.size) {
                                db.collection("users")
                                    .document(client[i])
                                    .get()
                                    .addOnSuccessListener { document ->
                                        val name = "${document.getString("nome").toString()} ${document.getString("cognome").toString()}"
                                        val via = document.getString("indirizzo.Via").toString()
                                        val cap = document.getString("indirizzo.CAP").toString()
                                        val citta = document.getString("indirizzo.Citta").toString()
                                        val addr = "${via}, $cap $citta"
                                        val pay = price[i]
                                        val ord = order[i]
                                        val id = client[i]

                                        val t = Trips(name, addr, pay, ord, id)
                                        arrayList.add(t)
                                    }

                            }
                        } else {
                            Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                        }
                    }
            }

        swipeRefreshLayout = view.findViewById(R.id.refresh)
        swipeRefreshLayout.setOnRefreshListener(this)

        /*val mapFragment: MapView = view.findViewById(R.id.map)
        mapFragment.onCreate(null)
        mapFragment.onResume()
        mapFragment.getMapAsync(this)*/

        Handler().postDelayed({
            loadingPanel.visibility = RelativeLayout.GONE
            initRecyclerView()
            addDataSet()
        }, 4000)

        return view
    }

    override fun onRefresh() {
        refresh.isRefreshing = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fragmentManager?.beginTransaction()?.detach(this)?.commitNow();
            fragmentManager?.beginTransaction()?.attach(this)?.commitNow();
        } else {
            fragmentManager?.beginTransaction()?.detach(this)?.attach(this)?.commit();
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        addDataSet()
    }

    private fun addDataSet() {
        activeTripsAdapter.submitList(arrayList)
    }

    private fun initRecyclerView() {
        myListView.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(10, 0, 0)
            addItemDecoration(topSpacingDecoration)
            activeTripsAdapter = ActiveTripsAdapter()
            myListView.adapter = activeTripsAdapter
        }
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(requireActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient()
                mMap!!.isMyLocationEnabled = true
            }
        } else {
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled = true
        }
    }

    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (ContextCompat.checkSelfPermission(requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            //mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
        }
        //mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    override fun onLocationChanged(location: Location) {

        var latitude = location!!.latitude
        var longitude = location!!.longitude
        val c = Coordinate(latitude, longitude)
        Toast.makeText(requireContext(), "Ciao!!!", Toast.LENGTH_SHORT).show()
        Toast.makeText(context, c.toString(), Toast.LENGTH_SHORT).show()
        var database = FirebaseDatabase.getInstance();
        mAuth.uid?.let { database.getReference("riders").child(it).setValue(c) };

        //stop location updates
        if (mGoogleApiClient != null) {
            //mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }
}

class Trips(val name: String, val address: String, val toPay: String, val order: String, val clientId: String)