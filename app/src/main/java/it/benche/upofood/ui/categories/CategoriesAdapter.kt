package it.benche.upofood.ui.categories

import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import it.benche.upofood.R

class CategoriesAdapter(private val context: FragmentActivity, val layout: Int, val array: Array<Category>): ArrayAdapter<Category>(context, layout, array) {

    internal class ViewHolder {
        var text: TextView? = null
        var img: ImageView? = null
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var myView = convertView

        if (myView == null) {

            //INSERIRE GLI ELEMENTI ALL'INTERNO DI OGNI RIGA DELLA LISTVIEW

            val viewHolder = ViewHolder()

            myView = context.layoutInflater.inflate(R.layout.item_category,null)

            viewHolder.text = myView!!.findViewById(R.id.textCategory)
            viewHolder.img = myView!!.findViewById(R.id.img)

            myView.tag = viewHolder

        } else {
            myView = convertView
        }

        val holder = myView!!.tag as ViewHolder

        holder.text?.text = array[position].name

        return myView!!
    }

}