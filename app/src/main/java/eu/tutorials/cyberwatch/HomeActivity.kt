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
    private lateinit var profileIcon: ImageView
    private lateinit var documentsIcon: ImageView
    private lateinit var contactsIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        chatbotIcon = findViewById(R.id.chatbot_icon)
        chatMessage = findViewById(R.id.chat_message)
        profileIcon = findViewById(R.id.profile_icon)
        documentsIcon = findViewById(R.id.documents_icon)
        contactsIcon = findViewById(R.id.contacts_icon)

        chatMessage.visibility = View.INVISIBLE

        chatbotIcon.setOnClickListener {
            navigateToChatbot()
        }

        profileIcon.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        documentsIcon.setOnClickListener{
            val intent = Intent(this, ChatbotActivity::class.java)
            startActivity(intent)
        }

        contactsIcon.setOnClickListener{
            val intent = Intent(this, ChatbotActivity::class.java)
            startActivity(intent)
        }

        // Delay showing the chat message for 1 second (1000 milliseconds)
        chatbotIcon.postDelayed({
            showChatMessage()
        }, 800)
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
