package eu.tutorials.cyberwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class ChatActivity : AppCompatActivity() {
    private lateinit var senderEmail: String
    private lateinit var receiverEmail: String
    private lateinit var senderRef: CollectionReference
    private lateinit var receiverRef: CollectionReference
    private lateinit var adapter: MessageAdapterr
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        val contactName = intent.getStringExtra("contactName")
        val toolbarTitle: TextView = findViewById(R.id.toolbar_title)
        toolbarTitle.text = contactName


        // Get the email addresses from extras
        val extras = intent.extras
        senderEmail = extras?.getString("senderEmail") ?: ""
        receiverEmail = extras?.getString("receiverEmail") ?: ""

        // Initialize Firestore collections for sender and receiver
        senderRef = FirebaseFirestore.getInstance()
            .collection("chat")
            .document(senderEmail)
            .collection("messages")

        receiverRef = FirebaseFirestore.getInstance()
            .collection("chat")
            .document(receiverEmail)
            .collection("messages")

        // Initialize views
        messageEditText = findViewById(R.id.activity_mentor_chat_message_edit_text)
        sendButton = findViewById(R.id.sendButton)
        recyclerView = findViewById(R.id.chat_recycler_view)

        // Initialize message adapter
        // Initialize message adapter
        adapter = MessageAdapterr(this,senderEmail)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>


        // Read previous messages from both sender and receiver collections
        readMessages()

        // Send button click listener
        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                messageEditText.text.clear()
            }
        }
    }

    private fun readMessages() {

        val senderQuery = senderRef.orderBy("timestamp")
        val receiverQuery = receiverRef.orderBy("timestamp")


        val senderMessages = mutableListOf<Message>()
        val receiverMessages = mutableListOf<Message>()

        val senderMessageIds = mutableSetOf<String>()
        val receiverMessageIds = mutableSetOf<String>()

        senderQuery.addSnapshotListener { senderSnapshot, senderException ->
            if (senderException != null) {
                // Handle failure to read sender messages
                return@addSnapshotListener
            }

            senderSnapshot?.let { senderQuerySnapshot ->
                for (document in senderQuerySnapshot.documentChanges) {
                    val messageId = document.document.id
                    if (document.type == DocumentChange.Type.ADDED && !senderMessageIds.contains(messageId)) {
                        val message = document.document.toObject(Message::class.java)
                        senderMessages.add(message)
                        senderMessageIds.add(messageId)
                    }
                }
            }

            // Combine senderMessages and receiverMessages here
            val allMessages = combineMessages(senderMessages, receiverMessages)
            adapter.setMessages(allMessages)
            scrollToLatestMessage()
        }

        receiverQuery.addSnapshotListener { receiverSnapshot, receiverException ->
            if (receiverException != null) {
                // Handle failure to read receiver messages
                return@addSnapshotListener
            }

            receiverSnapshot?.let { receiverQuerySnapshot ->
                for (document in receiverQuerySnapshot.documentChanges) {
                    val messageId = document.document.id
                    if (document.type == DocumentChange.Type.ADDED && !receiverMessageIds.contains(messageId)) {
                        val message = document.document.toObject(Message::class.java)
                        receiverMessages.add(message)
                        receiverMessageIds.add(messageId)
                    }
                }
            }

            // Combine senderMessages and receiverMessages here
            val allMessages = combineMessages(senderMessages, receiverMessages)
            adapter.setMessages(allMessages)
            scrollToLatestMessage()
        }
    }



    private fun combineMessages(
        senderMessages: List<Message>,
        receiverMessages: List<Message>
    ): List<Message> {
        val allMessages = mutableListOf<Message>()

        // Add sender messages
        for (message in senderMessages) {
            if (!allMessages.contains(message)) {
                allMessages.add(message)
            }
        }

        // Add receiver messages
        for (message in receiverMessages) {
            if (!allMessages.contains(message)) {
                allMessages.add(message)
            }
        }

        allMessages.sortBy { it.timestamp }

        Log.d("ChatActivity", "Combined Messages: $allMessages")

        return allMessages
    }





    private fun sendMessage(messageText: String) {
        val timestamp = System.currentTimeMillis()

        val senderMessage = Message(senderEmail, receiverEmail, messageText, timestamp)
        senderRef.document().set(senderMessage)
            .addOnSuccessListener {
                val receiverMessage = Message(senderEmail, receiverEmail, messageText, timestamp)
                receiverRef.document().set(receiverMessage)
                    .addOnSuccessListener {
                        // Message sent successfully to both sender and receiver
                        scrollToLatestMessage()
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure to send receiver message
                    }
            }
            .addOnFailureListener { exception ->
                // Handle failure to send sender message
            }
    }


    private fun scrollToLatestMessage() {
        recyclerView.post {
            recyclerView.scrollToPosition(adapter.itemCount - 1)
        }
    }
}

