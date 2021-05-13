package it.benche.upofood.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import it.benche.upofood.Product
import it.benche.upofood.R

class ShoppingCartAdapter(private val context: Activity, val layout: Int, val array: Array<Product>): ArrayAdapter<Product>(context, layout, array) {

    internal class ViewHolder {
        var productName: TextView? = null
        var qty: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var myView = convertView

        if(myView == null) {

            val viewHolder = ViewHolder()

            myView = context.layoutInflater.inflate(R.layout.item_product_cart, null)

            viewHolder.productName = myView!!.findViewById(R.id.product)
            viewHolder.qty = myView!!.findViewById(R.id.qty)

            myView.tag = viewHolder
        } else {
            myView = convertView
        }

        val holder = myView!!.tag as ViewHolder

        holder.productName?.text = array[position].prodotto
        holder.qty?.text = "Quantità: " + array[position].qty.toString()

        return myView!!
    }
}