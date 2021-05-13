package it.benche.upofood.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import it.benche.upofood.R
import it.benche.upofood.message.MessageAdapter.Constants.MSG_TYPE_LEFT
import it.benche.upofood.message.MessageAdapter.Constants.MSG_TYPE_RIGHT
import it.benche.upofood.models.Chat
import it.benche.upofood.utils.CartAdapter
import kotlinx.android.synthetic.main.item_chat_me.*
import kotlin.properties.Delegates

class MessageAdapter constructor(
        mContext: Context, mChat: List<Chat>
): Adapter<RecyclerView.ViewHolder>() {

    object Constants {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }

    private var mChat: List<Chat> = mChat
    private var mContext: Context = mContext
    private lateinit var mAuth: FirebaseUser
    var typeUser by Delegates.notNull<Int>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        if(viewType == MSG_TYPE_RIGHT) {
            typeUser = 0
            val view: View =
                LayoutInflater.from(mContext).inflate(R.layout.item_chat_me, parent, false)
            return MessageAdapter.ViewHolder(view)
        } else {
            typeUser = 1
            val view: View =
                LayoutInflater.from(mContext).inflate(R.layout.item_chat_other, parent, false)
            return MessageAdapter.ViewHolder(view)
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
        private var show_message: TextView = itemView.findViewById(R.id.text_gchat_message)
        private var timeStamp: TextView = itemView.findViewById(R.id.text_gchat_timestamp)
        fun bind(chatCard: Chat) {
            show_message.text = chatCard.getMessage()
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