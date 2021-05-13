package it.benche.upofood.manager

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.benche.upofood.R
import it.benche.upofood.client.Product
import it.benche.upofood.message.MessageListActivity
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.item_manager.view.*

class ListManagersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Manager> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ManagerViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_manager, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ManagerViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(managerList: List<Manager>) {
        items = managerList
    }

    class ManagerViewHolder constructor(
            itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        val nameManager = itemView.nameManager
        val card = itemView.card

        fun bind(managerCard: Manager) {
            nameManager.text = "${managerCard.name} ${managerCard.surname}"

            card.onClick {
                val mAuth = FirebaseAuth.getInstance().currentUser.uid
                val intent: Intent = Intent(context, MessageListActivity::class.java)
                intent.putExtra("SENDER", mAuth)
                intent.putExtra("RECEIVER", managerCard.id)
                startActivity(context, intent, null)
            }
        }
    }
}