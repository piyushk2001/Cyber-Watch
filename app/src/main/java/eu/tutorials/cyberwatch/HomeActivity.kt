package eu.tutorials.cyberwatch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var chatbotIcon: ImageView
    private lateinit var chatMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        chatbotIcon = findViewById(R.id.chatbot_icon)
        chatMessage = findViewById(R.id.chat_message)

        chatMessage.visibility = View.INVISIBLE

        chatbotIcon.setOnClickListener {
            navigateToChatbot()
        }

        // Delay showing the chat message for 1 second (1000 milliseconds)
        chatbotIcon.postDelayed({
            showChatMessage()
        }, 1000)
    }

    private fun showChatMessage() {
        chatMessage.visibility = View.VISIBLE
        chatMessage.setOnClickListener {
            navigateToChatbot()
        }
    }

    private fun navigateToChatbot() {
        val intent = Intent(this, ChatbotActivity::class.java)
        startActivity(intent)
    }
}
