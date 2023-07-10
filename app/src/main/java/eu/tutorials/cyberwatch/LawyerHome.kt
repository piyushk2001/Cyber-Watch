package eu.tutorials.cyberwatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class LawyerHome : AppCompatActivity() {
    //private lateinit var lawyerChats: ImageView
    private lateinit var profileIcon: ImageView
    private lateinit var contactsIcon: ImageView
    private lateinit var requestsIcon: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lawyer_home)

        //lawyerChats=findViewById(R.id.lawyers_chat)
        profileIcon=findViewById(R.id.profile_icon)
        contactsIcon=findViewById(R.id.contacts_icon)
        requestsIcon=findViewById(R.id.requests_icon)


        profileIcon.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        contactsIcon.setOnClickListener{
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }

        requestsIcon.setOnClickListener{
            val intent = Intent(this, RequestsActivity::class.java)
            startActivity(intent)
        }



    }
}