package it.benche.upofood.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import it.benche.upofood.R
import it.benche.upofood.client.Product
import kotlinx.android.synthetic.main.item_product.view.*
import kotlin.coroutines.coroutineContext

class ListOrderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Product> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
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

        val productName = itemView.tvProductName
        val qty = itemView.tvDiscountPrice
        val card = itemView.card

        fun bind(productCard: Product) {
            productName.text = productCard.name
            qty.text = "Quantità: ${productCard.qty}"
            productName.setTextColor(Color.WHITE)
            qty.setTextColor(Color.WHITE)
            card.setCardBackgroundColor(Color.GRAY)
            card.layoutParams = ConstraintLayout.LayoutParams(300, RelativeLayout.LayoutParams.MATCH_PARENT)
        }
    }
}