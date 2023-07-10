package eu.tutorials.cyberwatch
import android.content.Intent

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import eu.tutorials.cyberwatch.databinding.ActivityChatbotBinding
import org.json.JSONObject

class ChatbotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatbotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
            }
        }
    }

    private fun sendMessage(message: String) {
        val url = "http://0.0.0.0:5000/chat"
        val requestQueue = Volley.newRequestQueue(this)

        val requestBody = JSONObject()
        requestBody.put("message", message)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response ->
                val botResponse = response.getString("response")
                Log.d("Bot Response", botResponse)
                binding.tvResponse.text = botResponse
            },
            { error ->
                Log.e("Chatbot Error", error.toString())

                // Display apology message and lawyer chat option
                val apologyMessage = "Apologies! We can't resolve your query."
                val lawyerOption = "Chat with a lawyer"
                val errorMessage = "$apologyMessage\n\n$error\n\n$lawyerOption"

                binding.tvResponse.text = errorMessage

                binding.tvResponse.setOnClickListener {
                    // TODO: Redirects to user chat functionality
                    val intent = Intent(this, UserChatsActivity::class.java)
                    startActivity(intent)
                }
            }
        )

        requestQueue.add(jsonObjectRequest)
    }

}
