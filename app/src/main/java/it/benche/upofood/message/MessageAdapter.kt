package it.benche.upofood.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import it.benche.upofood.R
import it.benche.upofood.message.MessageAdapter.Constants.MSG_TYPE_LEFT
import it.benche.upofood.message.MessageAdapter.Constants.MSG_TYPE_RIGHT
import it.benche.upofood.models.Chat
import kotlin.properties.Delegates

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MessageAdapter constructor(
    private var mContext: Context, private var mChat: List<Chat>
): Adapter<RecyclerView.ViewHolder>() {

    object Constants {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }

    private lateinit var mAuth: FirebaseUser
    private var typeUser by Delegates.notNull<Int>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if(viewType == MSG_TYPE_RIGHT) {
            typeUser = 0
            val view: View =
                LayoutInflater.from(mContext).inflate(R.layout.item_chat_me, parent, false)
            ViewHolder(view)
        } else {
            typeUser = 1
            val view: View =
                LayoutInflater.from(mContext).inflate(R.layout.item_chat_other, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ViewHolder -> {
                holder.bind(mChat[position])
            }
        }
    }

    class ViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        private var showMessage: TextView = itemView.findViewById(R.id.text_gchat_message)
        private var timeStamp: TextView = itemView.findViewById(R.id.text_gchat_timestamp)
        fun bind(chatCard: Chat) {
            showMessage.text = chatCard.getMessage()
            timeStamp.text = chatCard.getTime()
        }


    }

    override fun getItemCount(): Int {
        return mChat.size
    }

    override fun getItemViewType(position: Int): Int {
        mAuth = FirebaseAuth.getInstance().currentUser
        return if(mChat[position].getSender() == mAuth.uid) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }
}