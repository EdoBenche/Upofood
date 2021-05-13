package it.benche.upofood.ui.categories

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import it.benche.upofood.R


class CategoriesFragment: Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_categories, container, false)

        db = FirebaseFirestore.getInstance()

        var categories: Array<Category>
        var arrayList = ArrayList<Category>()
        db.collection("categories")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            Log.d(TAG, document.id + " => " + document.data)
                            val str: String = document.getString("nome").toString()
                            val r = Category(str)
                            arrayList.add(r)
                        }


                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }

        Handler().postDelayed({categories = arrayList.toTypedArray()
            Log.d(TAG, categories.toString())

            val myList = view.findViewById<ListView>(R.id.myListView)

            myList.adapter = activity?.let { CategoriesAdapter(it, R.layout.item_category, categories) }!!}, 2000)


        return view
    }
}

class Category(val name: String)