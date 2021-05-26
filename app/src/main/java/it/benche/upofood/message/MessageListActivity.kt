@file:Suppress("DEPRECATION")

package it.benche.upofood.message

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_message_list.*
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
    @SuppressLint("CutPasteId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            finish()
            super.onBackPressed() }

        apiService = ApiClient.getClient!!.create(APIService::class.java)

        recyclerView = findViewById(R.id.recycler_gchat)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager


        snd = intent.getStringExtra("SENDER").toString()
        rcv = intent.getStringExtra("RECEIVER").toString()

        db = FirebaseDatabase.getInstance()
        mFirebaseFirestore = FirebaseFirestore.getInstance()
        mFirebaseFirestore.collection("users").document(rcv).get().addOnSuccessListener { document ->
            username.text = document.getString("nome").toString()
        }

        val textSend: EditText = findViewById(R.id.text_send)!!
        button_gchat_send.setOnClickListener {
            notify = true
            val msg = textSend.text.toString()
            if(msg != "") {
                val c = Calendar.getInstance()
                val hour = c.get(Calendar.HOUR_OF_DAY)
                val minutes = c.get(Calendar.MINUTE)
                val time = "$hour:$minutes"
                sendMessage(snd, rcv, msg, time)
            } else {
                Toast.makeText(this, "Non puoi mandare un messaggio vuoto!", Toast.LENGTH_SHORT).show()
            }
            textSend.setText("")
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

        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["message"] = message
        hashMap["time"] = time

        Log.d("CIAO", "***$reference")

        reference.child("Chats").push().setValue(hashMap)

        FirebaseAuth.getInstance().currentUser.uid
        sendNotifiaction(receiver, message)
        notify = false
    }

    private fun sendNotifiaction(receiver: String, msg: String) {
        val tokens: DatabaseReference = FirebaseDatabase.getInstance().getReference("Tokens")
        val query: Query = tokens.orderByKey().equalTo(receiver)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val token: Token = data.getValue(Token::class.java) as Token
                    val data = Data(
                        FirebaseAuth.getInstance().currentUser.uid,
                        R.mipmap.ic_launcher,
                        msg,
                        "Nuovo messaggio",
                        "userId"
                    )
                    val sender = Sender(data, token.getToken())

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
        val mAuth = FirebaseAuth.getInstance().currentUser
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Tokens")
        val token1 = Token(token)
        reference.child(mAuth.uid).setValue(token1)
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}