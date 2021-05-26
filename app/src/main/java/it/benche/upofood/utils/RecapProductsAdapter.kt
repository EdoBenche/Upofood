package it.benche.upofood.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.benche.upofood.Product
import it.benche.upofood.R
import kotlinx.android.synthetic.main.item_product_cart.view.*

class RecapProductsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


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

        val productName = itemView.product
        val productQty = itemView.qty

        @SuppressLint("SetTextI18n")
        fun bind(productCard : Product) {
            productName.text = productCard.prodotto
            productQty.text = "Quantità: ${productCard.qty}"
        }
    }
}