package eu.tutorials.cyberwatch

import ChatAdapter
import ChatMessage
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class LawyerChatsActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lawyer_activity_chats)

        database = FirebaseDatabase.getInstance().reference
        val chatroomId = "user_lawyer_chatroom" // Same chatroom ID as UserChatsActivity

        // Initialize RecyclerView and ChatAdapter
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter()
        recyclerView.adapter = chatAdapter

        // Start listening for new chat messages
        database.child(chatroomId).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                chatMessage?.let {
                    chatAdapter.addMessage(it)
                    recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })

        // Send message when the lawyer clicks on the send button
        val btnSend = findViewById<Button>(R.id.btnSend)
        val etMessage = findViewById<EditText>(R.id.etMessage)
        btnSend.setOnClickListener {
            val message = etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(chatroomId, message)
                etMessage.text.clear()
            }
        }
    }

    private fun sendMessage(chatroomId: String, message: String) {
        val chatMessage = ChatMessage(message, System.currentTimeMillis())
        database.child(chatroomId).push().setValue(chatMessage)
    }
}
