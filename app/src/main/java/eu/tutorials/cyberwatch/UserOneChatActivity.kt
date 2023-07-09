package eu.tutorials.cyberwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class UserOneChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_one_chat)

        val contactName = intent.getStringExtra("contactName")

        val toolbarTitle: TextView = findViewById(R.id.toolbar_title)
        toolbarTitle.text = contactName
    }
}
