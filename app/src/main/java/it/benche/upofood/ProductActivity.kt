package it.benche.upofood

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.activity_product_detail2.*
import java.io.File
import java.io.IOException

class ProductActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var pickerQty: NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail2)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        tvName.text = intent.getStringExtra("NAME")
        tvPrice.text = intent.getStringExtra("PRICE")
        //qty.text = intent.getIntExtra("QTY", 1).toString()
        cat.text = intent.getStringExtra("CATEGORY")
        tvDescription.text = intent.getStringExtra("DESCRIPTION")

        val mStorageReference = FirebaseStorage.getInstance().reference.child("images/${intent.getStringExtra("IMG")}")

        try {
            val localFile: File = createTempFile("img", "jpg")
            mStorageReference.getFile(localFile)
                    .addOnSuccessListener {
                        var bitmap: Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        var height = ivProduct.height
                        var width = ivProduct.width
                        bitmap = createScaledBitmap(bitmap, width, height, true)
                        ivProduct.setImageBitmap(bitmap)
                    }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        var uid = mAuth.uid
        db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for(document in result) {
                        if(document.id == uid.toString()) {
                            val role = document.getString("account")

                            if(role == "Gestore") {
                                forGestors.visibility = LinearLayout.VISIBLE
                            } else if(role == "Cliente") {
                                forClients.visibility = LinearLayout.VISIBLE
                            }
                        }
                    }
                }

        pickerQty = findViewById(R.id.numberPicker)
        pickerQty.minValue = 1
        pickerQty.maxValue = intent.getIntExtra("QTY", 1)
        pickerQty.value = 1


        btnAddCart.onClick {
            addOnCart()
        }
        btnBuyNow.onClick {
            startActivity(Intent(context, ShoppingCartActivity::class.java))
        }
        btnModify.onClick {
            val p = intent.getStringExtra("NAME")
            val intent = Intent(context, AddProductActivity::class.java)
            intent.putExtra("PRODUCT", p)
            startActivity(intent)
        }
    }

    @SuppressLint("ResourceType")
    private fun addOnCart() {
        // Create a product in cart
        val product = hashMapOf(
                "prodotto" to intent.getStringExtra("NAME"),
                "qty" to pickerQty.value,
                "price" to intent.getDoubleExtra("PRICEINT", 1.00)
        )


// Add a new document with a generated ID

// Add a new document with a generated ID
        val TAG: String = "ProductActivity";
        var uid: String? = mAuth.uid
        if (uid != null) {
            db.collection("carts")
                    .document(uid)
                    .collection("cart")
                    .document("${intent.getStringExtra("NAME")}")
                    .set(product)
                    .addOnSuccessListener { Log.d(TAG, "Prodotto aggiunto al carrello correttamente" )
                    Toast.makeText(applicationContext, "Prodotto aggiunto", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
        }
    }
}