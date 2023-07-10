package eu.tutorials.cyberwatch

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
        val url = "http://localhost:5000/chat"
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
            }
        )

        requestQueue.add(jsonObjectRequest)
    }
}
