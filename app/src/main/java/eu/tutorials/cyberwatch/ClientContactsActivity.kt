package eu.tutorials.cyberwatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton

class ClientContactsActivity : AppCompatActivity() {
    private lateinit var searchBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_contacts)

        searchBtn=findViewById(R.id.searchButton)
        searchBtn.setOnClickListener(View.OnClickListener {
            //FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        })
    }
}