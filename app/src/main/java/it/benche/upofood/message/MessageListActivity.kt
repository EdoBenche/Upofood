package it.benche.upofood.message

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import it.benche.upofood.R
import it.benche.upofood.models.Chat
import it.benche.upofood.notifications.*
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.activity_message_list.*
import kotlinx.android.synthetic.main.activity_message_list.view.*
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MessageListActivity : AppCompatActivity() {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var mChat: List<Chat>

    private lateinit var recyclerView: RecyclerView

    private lateinit var db: FirebaseDatabase
    private lateinit var mFirebaseFirestore: FirebaseFirestore

    private lateinit var snd: String
    private lateinit var rcv: String

    private lateinit var apiService: APIService

    private var notify = false

    private var mMessageRecycler: RecyclerView? = null
    //private var mMessageAdapter: MessageListAdapter? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        apiService = ApiClient.getClient!!.create(APIService::class.java)

        recyclerView = findViewById(R.id.recycler_gchat)
        recyclerView.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager


        snd = intent.getStringExtra("SENDER").toString()
        rcv = intent.getStringExtra("RECEIVER").toString()

        db = FirebaseDatabase.getInstance()
        mFirebaseFirestore = FirebaseFirestore.getInstance()
        mFirebaseFirestore.collection("users").document(rcv).get().addOnSuccessListener { document ->
            username.text = document.getString("nome").toString()
        }

        val text_send: EditText = findViewById(R.id.text_send)!!
        button_gchat_send.onClick {
            notify = true
            val msg = text_send.text.toString()
            if(msg != "") {
                val c = Calendar.getInstance()
                val hour = c.get(Calendar.HOUR_OF_DAY)
                val minutes = c.get(Calendar.MINUTE)
                val time = "$hour:$minutes"
                sendMessage(snd, rcv, msg, time)
            } else {
                Toast.makeText(context, "Non puoi mandare un messaggio vuoto!", Toast.LENGTH_SHORT).show()
            }
            text_send.setText("")
        }

        val reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                readMessage(snd, rcv)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        FirebaseInstanceId.getInstance().token?.let { updateToken(it) }

        mMessageRecycler = findViewById<View>(R.id.recycler_gchat) as RecyclerView
        //mMessageAdapter = MessageListAdapter(this, messageList)
        mMessageRecycler!!.layoutManager = LinearLayoutManager(this)
    }

    private fun sendMessage(sender: String, receiver: String, message: String, time: String) {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference

        val hashMap: HashMap<String, String> = HashMap()

        hashMap.put("sender", sender)
        hashMap.put("receiver", receiver)
        hashMap.put("message", message)
        hashMap.put("time", time)

        reference.child("Chats").push().setValue(hashMap)

        var msg = message
        var mAuth = FirebaseAuth.getInstance().currentUser.uid
        sendNotifiaction(receiver, msg)
        notify = false
    }

    private fun sendNotifiaction(receiver: String, msg: String) {
        var tokens: DatabaseReference = FirebaseDatabase.getInstance().getReference("Tokens")
        var query: Query = tokens.orderByKey().equalTo(receiver)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    var token: Token = data.getValue(Token::class.java) as Token
                    var data: Data = Data(
                        FirebaseAuth.getInstance().currentUser.uid,
                        R.mipmap.ic_launcher,
                        msg,
                        "Nuovo messaggio",
                        "userId"
                    )
                    var sender: Sender = Sender(data, token.getToken())

                    apiService.sendNotification(sender)
                        .enqueue(object : retrofit2.Callback<MyResponse> {
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if(response.code() == 200) {
                                    /*if (response.body()!!.success == 1) {
                                        Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT)
                                                .show()
                                    }*/
                                }

                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                                TODO("Not yet implemented")
                            }

                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun readMessage(myId: String, userId: String) {
        mChat = ArrayList()
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mChat as ArrayList<Chat>).clear()
                for (data in snapshot.children) {
                    val chat: Chat = data.getValue(Chat::class.java) as Chat
                    if ((chat.getReceiver() == myId && chat.getSender() == userId) || (chat.getReceiver() == userId && chat.getSender() == myId)) {
                        (mChat as ArrayList<Chat>).add(chat)
                    }
                    messageAdapter = MessageAdapter(applicationContext, mChat)
                    recyclerView.adapter = messageAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun updateToken(token: String) {
        var mAuth = FirebaseAuth.getInstance().currentUser
        var reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Tokens")
        var token1: Token = Token(token)
        reference.child(mAuth.uid).setValue(token1)
    }
}