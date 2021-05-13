package it.benche.upofood.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import it.benche.upofood.Product
import it.benche.upofood.R
import kotlinx.android.synthetic.main.item_product_cart.view.*

class CartAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var items: List<Product> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_product_cart, parent, false)
        )
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

    fun submitList(productList: List<Product>) {
        items = productList
    }

    class ProductViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {

        private val productName: TextView = itemView.product
        private val productQty: TextView = itemView.qty

        fun bind(productCard : Product) {
            productName.text = productCard.prodotto
            productQty.text = "Quantità: ${productCard.qty.toString()}"
        }
    }
}