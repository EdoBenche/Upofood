package it.benche.upofood.ui.products

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import it.benche.upofood.ProductActivity
import it.benche.upofood.R
import it.benche.upofood.utils.extensions.onClick
import java.io.File
import java.io.IOException
import kotlin.io.*
import kotlinx.android.synthetic.main.item_product.view.*

class ProductsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<Product> = ArrayList()

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameProduct: TextView? = null
        var priceProduct: TextView? = null
        var imageProduct: ImageView? = null
        var qtyProduct: TextView? = null
        init {
            nameProduct = itemView.findViewById(R.id.tvProductName)
            priceProduct = itemView.findViewById(R.id.tvDiscountPrice)
            imageProduct = itemView.findViewById(R.id.ivProduct)
            qtyProduct = itemView.findViewById(R.id.qty)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ProductViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(productsList: List<Product>) {
        items = productsList
    }

    class ProductViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        val productName = itemView.tvProductName
        val productPrice = itemView.tvDiscountPrice
        val productImage = itemView.ivProduct
        val clickable: CardView = itemView.card
        val productQty = itemView.qty
        val esaurito = itemView.esaurito
        val rimanenti = itemView.qtyCard
        val prezzo = itemView.priceCard

        fun bind(productCard: Product) {
            productName.text = productCard.name
            productPrice.text = "${productCard.price}€"
            productQty.text = "${productCard.qty} rimanenti"

            val mStorageReference = FirebaseStorage.getInstance().reference.child("images/${productCard.img}")

            try {
                val localFile: File = createTempFile("img", "jpg")
                mStorageReference.getFile(localFile)
                    .addOnSuccessListener {
                        var bitmap: Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        bitmap = Bitmap.createScaledBitmap(bitmap, 512, 512, true)
                        productImage.setImageBitmap(bitmap)
                    }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val pricex = productCard.price

            val qty = productCard.qty
            val category = productCard.category
            val description = productCard.description
            val img = productCard.img

            clickable.onClick {
                val intent: Intent = Intent(context, ProductActivity::class.java)
                intent.putExtra("NAME", productName.text)
                intent.putExtra("PRICE", productPrice.text)
                intent.putExtra("PRICEINT", pricex)
                intent.putExtra("QTY", qty)
                intent.putExtra("DESCRIPTION", description)
                intent.putExtra("IMG", img)
                intent.putExtra("CATEGORY", category)
                startActivity(context, intent, null)
            }

            if(qty == 0) {
                esaurito.visibility = CardView.VISIBLE
                rimanenti.visibility = CardView.GONE
                prezzo.visibility = CardView.GONE

                val db = FirebaseFirestore.getInstance()
                val mAuth = FirebaseAuth.getInstance()
                db.collection("users")
                    .document(mAuth.currentUser.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if(document.getString("account").toString() != "Gestore") {
                            clickable.isClickable = false
                        }
                    }
            }
        }
    }
}