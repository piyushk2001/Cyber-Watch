package eu.tutorials.cyberwatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userNameTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var emailTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        userNameTextView = findViewById(R.id.userName)
        addressTextView = findViewById(R.id.userAddress)
        phoneTextView = findViewById(R.id.userPhone)
        emailTextView = findViewById(R.id.userEmail)

        if (auth.currentUser == null) {
            navigateToLoginActivity()
        } else {
            val currentEmail = auth.currentUser!!.email
            getUserDataFromFirestore(currentEmail)
        }

    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun getUserDataFromFirestore(email: String?) {
        if (email != null) {
            val userRef = firestore.collection("User").document(email)
            userRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val name = documentSnapshot.getString("name")
                        val address = documentSnapshot.getString("adresse")
                        val phone = documentSnapshot.getString("tel")
                        val userEmail = documentSnapshot.getString("email")

                        runOnUiThread {
                            userNameTextView.text = name
                            addressTextView.text = address
                            phoneTextView.text = phone
                            emailTextView.text = userEmail
                        }
                    } else {
                        // Handle document not found
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle error
                }
        }
    }
}

