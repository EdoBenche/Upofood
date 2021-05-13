package it.benche.upofood

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.SyncStateContract
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.benche.upofood.models.Address
import it.benche.upofood.utils.SimpleLocation
import it.benche.upofood.utils.extensions.checkIsEmpty
import it.benche.upofood.utils.extensions.onClick
import it.benche.upofood.utils.extensions.showError
import it.benche.upofood.utils.Constants
import it.benche.upofood.utils.extensions.isGPSEnable
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.added_user_dialog.*
import java.lang.RuntimeException

class AddAddressActivity : AppCompatActivity(), SimpleLocation.Listener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var dialog: Dialog
    private var address: Address? = null
    private var simpleLocation: SimpleLocation? = null
    private var addressId: Int? = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance()

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
}


