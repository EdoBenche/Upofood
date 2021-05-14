package it.benche.upofood

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.SyncStateContract
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil.computeDistanceBetween
import com.google.type.LatLng
import it.benche.upofood.models.Address
import it.benche.upofood.utils.SimpleLocation
import it.benche.upofood.utils.extensions.checkIsEmpty
import it.benche.upofood.utils.extensions.onClick
import it.benche.upofood.utils.extensions.showError
import it.benche.upofood.utils.Constants
import it.benche.upofood.utils.extensions.isGPSEnable
import kotlinx.android.synthetic.main.activity_active_order.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.added_user_dialog.*
import java.io.IOException
import java.lang.RuntimeException

class AddAddressActivity : AppCompatActivity(), SimpleLocation.Listener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var dialog: Dialog
    private var address: Address? = null
    private var simpleLocation: SimpleLocation? = null
    private var addressId: Int? = -1

    private lateinit var shopAddress: ArrayList<String>
    private lateinit var checkInArray : Array<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance()
        shopAddress = ArrayList()
        checkInArray = Array(1) {false}

        simpleLocation = SimpleLocation(this)
        simpleLocation?.setListener(this)

        if(intent.hasExtra(Constants.KeyIntent.DATA)) {
            address = intent?.getSerializableExtra(Constants.KeyIntent.DATA) as Address
            addressId = intent?.getIntExtra(Constants.KeyIntent.ADDRESS_ID, -1)
        }

        btnSaveAddress.onClick {
            if(validate()) {
                if (address == null) {
                    address = Address()
                    assignData()
                    addAddress()
                }
            }
        }

        rlUseCurrentLocation.onClick {
            if(isGPSEnable()) {
                simpleLocation?.beginUpdates()
            }
        }
    }



   private fun validate(): Boolean {
       checkDistance()
       if(!checkInArray[0]) {
           edtAddress.showError("Distanza maggiore di 10km, si prega di inserire un indirizzo valido")
           edtAddress.requestFocus()
           return false
       }
        when {
            edtPinCode.checkIsEmpty() -> {
                edtPinCode.showError(getString(R.string.error_field_required))
                edtPinCode.requestFocus()
                return false
            }
            edtCity.checkIsEmpty() -> {
                edtCity.showError(getString(R.string.error_field_required))
                edtCity.requestFocus()
                return false
            }
            edtProvincia.checkIsEmpty() -> {
                edtProvincia.showError(getString(R.string.error_field_required))
                edtProvincia.requestFocus()
                return false
            }

            edtAddress.checkIsEmpty() -> {
                edtAddress.showError(getString(R.string.error_field_required))
                edtAddress.requestFocus()
                return false
            }

            else -> return true
        }
    }

    override fun onPositionChanged() {
        val address = simpleLocation?.address
        if (address != null) {
            edtProvincia.setText(address.adminArea)
            edtPinCode.setText(address.postalCode)
            edtCity.setText(address.locality)
            if (address.getAddressLine(0) != null) {
                edtAddress.setText(address.getAddressLine(0))
            }
            simpleLocation?.endUpdates()
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    private fun assignData() {
        if (address !== null) {
            address!!.city = edtCity.text.toString()
            address!!.provincia = edtProvincia.text.toString()
            address!!.pincode = edtPinCode.text.toString()
            address!!.address = edtAddress.text.toString()
        }
    }

    private fun addAddress() {
        val address = hashMapOf(
            "Via" to edtAddress.text.toString(),
            "CAP" to edtPinCode.text.toString(),
            "Citta" to edtCity.text.toString(),
            "Provincia" to edtProvincia.text.toString()
        )


        // Add a new document with a generated ID
        val TAG: String = "AddAddressActivity";
        var uid: String? = mAuth.uid
        if (uid != null) {
            var user1 = db.collection("users").document(uid)
            user1.update("indirizzo", address)
        }
        confirmed()
        //startActivity(Intent(this, DrawerActivityClient::class.java))
    }

    private fun confirmed() {
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.setContentView(R.layout.added_user_dialog)
        dialog.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        dialog.show()

        Handler().postDelayed({
            startActivity(Intent(this, DrawerActivityClient::class.java))
        }, 3000)

    }

    private fun checkDistance() {
        var check = false
        var latClient: Double = 0.0
        var lonClient: Double = 0.0
        lateinit var posShop: com.google.android.gms.maps.model.LatLng
        lateinit var posClient: com.google.android.gms.maps.model.LatLng
        db.collection("shop")
            .document("info")
            .get()
            .addOnSuccessListener { document ->
                shopAddress.add(document.getString("address.Via").toString())
                shopAddress.add(document.getString("address.Citta").toString())
                shopAddress.add(document.getString("address.CAP").toString())

                var geocodeMatches: List<android.location.Address>? = null

                try {
                    geocodeMatches = Geocoder(this.applicationContext).getFromLocationName(
                        "${shopAddress[0]}, ${shopAddress[1]}, ${shopAddress[2]}", 1
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (geocodeMatches != null) {
                    var latitudeShop = geocodeMatches[0].latitude
                    var longitudeShop = geocodeMatches[0].longitude
                    posShop = com.google.android.gms.maps.model.LatLng(latitudeShop, longitudeShop)
                }

                var geocodeMatches1: List<android.location.Address>? = null

                try {
                    geocodeMatches1 = Geocoder(this.applicationContext).getFromLocationName(
                        "${edtAddress.text}, ${edtCity.text}, ${edtPinCode.text}", 1
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (geocodeMatches1 != null) {
                    latClient = geocodeMatches1[0].latitude
                    lonClient = geocodeMatches1[0].longitude
                    posClient = com.google.android.gms.maps.model.LatLng(latClient, lonClient)
                }

                Toast.makeText(applicationContext, computeDistanceBetween(posClient, posShop).toString(), Toast.LENGTH_LONG).show()
                Log.d("wow", "Distanza: ${computeDistanceBetween(posClient, posShop)}")
                check = computeDistanceBetween(posClient, posShop) <= 10000.0
                checkInArray[0] = check
            }
    }
}


