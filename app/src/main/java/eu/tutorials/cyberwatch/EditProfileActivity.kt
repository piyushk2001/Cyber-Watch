package eu.tutorials.cyberwatch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {
    private lateinit var updateProfile: Button
    private lateinit var userName: TextInputEditText
    private lateinit var userPhone: TextInputEditText
    private lateinit var userAddress: TextInputEditText
    //private val currentUserUID = FirebaseAuth.getInstance().currentUser?.email.toString()
    private val userID = FirebaseAuth.getInstance().currentUser?.email.toString()
    private lateinit var userRef: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        userRef = FirebaseFirestore.getInstance()

        updateProfile = findViewById(R.id.update)
        userName = findViewById(R.id.nameText)
        userPhone = findViewById(R.id.phoneText)
        userAddress = findViewById(R.id.addressText)

        val intent = intent
        val current_name = intent.getStringExtra("CURRENT_NAME")
        val current_phone = intent.getStringExtra("CURRENT_PHONE")
        val current_address = intent.getStringExtra("CURRENT_ADDRESS")

        userName.setText(current_name)
        userPhone.setText(current_phone)
        userAddress.setText(current_address)

        updateProfile.setOnClickListener {
            val updateAddress = userAddress.text.toString()
            val updateName = userName.text.toString()
            val updatePhone = userPhone.text.toString()
            updateUserInfos(updateName, updateAddress, updatePhone)
        }
    }

    private fun updateUserInfos(name: String, address: String, phone: String) {
        val documentReference: DocumentReference = userRef.collection("User").document(userID)
        documentReference.update("adresse", address)
        documentReference.update("name", name)
        documentReference.update("tel", phone)
            .addOnSuccessListener {
                Toast.makeText(this@EditProfileActivity, "Infos Updated", Toast.LENGTH_LONG).show()
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@EditProfileActivity, e.message, Toast.LENGTH_LONG).show()
                Log.d("Androidview", e.message.toString())
            }
    }
}
