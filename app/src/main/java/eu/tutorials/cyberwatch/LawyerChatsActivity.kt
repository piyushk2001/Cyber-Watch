package eu.tutorials.cyberwatch

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class LawyerChatsActivity : AppCompatActivity() {
    private val httpClient = OkHttpClient()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lawyer_activity_chats)

        val sendMessageButton = findViewById<Button>(R.id.send_button)
        sendMessageButton.setOnClickListener {
            val messageInput = findViewById<EditText>(R.id.message_input)
            val message = messageInput.text.toString()
            val receiverIP = "192.168.1.34" // Replace with the user's IP address

            GlobalScope.launch(Dispatchers.IO) {
                sendMessage(receiverIP, message)
            }
        }
    }

    private fun sendMessage(receiverIP: String, message: String) {
        val url = "http://0.0.0.0:5000/send_message"
        val json = """{"sender_ip": "$receiverIP", "receiver_ip": "$receiverIP", "message": "$message"}"""
        val body = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = httpClient.newCall(request).execute()
        // Handle the response as needed
    }
}
