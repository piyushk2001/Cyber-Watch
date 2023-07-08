package eu.tutorials.cyberwatch

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class ProfileActivity : AppCompatActivity() {
    private var auth: FirebaseAuth= FirebaseAuth.getInstance()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userNameTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var signOutBtn: Button
    private lateinit var editProfileBtn: ImageButton
    private lateinit var uploadBtn: ImageView
    private lateinit var imageview: ImageView

    private var storageRef = Firebase.storage.reference
    private var userEmail = auth.currentUser!!.email
    //private var imageview:ImageView= findViewById(R.id.imageView3)



    private fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        uri.path?.let { path ->
            val file = File(path)
            fileName = file.name
        }
        return fileName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        firestore = FirebaseFirestore.getInstance()
        userNameTextView = findViewById(R.id.userName)
        addressTextView = findViewById(R.id.userAddress)
        phoneTextView = findViewById(R.id.userPhone)
        emailTextView = findViewById(R.id.userEmail)
        imageview = findViewById(R.id.imageView3)


        if (auth.currentUser == null) {
            navigateToLoginActivity()
        } else {
            val currentEmail = auth.currentUser!!.email
            getUserDataFromFirestore(currentEmail)
        }
        signOutBtn = findViewById<Button>(R.id.signOutBtn)
        signOutBtn.setOnClickListener(View.OnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        })

        editProfileBtn = findViewById(R.id.editButton)
        editProfileBtn.setOnClickListener(View.OnClickListener {
            //FirebaseAuth.getInstance().signOut()
            //val intent = Intent(applicationContext, EditProfileActivity::class.java)
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startEditActivity()
        })
        var imagePickerActivityResult: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val imageUri: Uri? = result.data?.data

                    val sd = getFileName(applicationContext, imageUri!!)

                    val uploadTask = storageRef.child("ProfilePictures/$userEmail").putFile(imageUri)

                    // On success, download the file URL and display it
                    uploadTask.addOnSuccessListener {
                        // using glide library to display the image
                        storageRef.child("ProfilePictures/$userEmail").downloadUrl.addOnSuccessListener { downloadUrl ->
                            Glide.with(this)
                                .load(downloadUrl)
                                .into(imageview)

                            Log.e("Firebase", "download passed")
                        }.addOnFailureListener {
                            Log.e("Firebase", "Failed in downloading")
                        }
                    }.addOnFailureListener {
                        Log.e("Firebase", "Image Upload fail")
                    }
                }
            }
        uploadBtn = findViewById(R.id.uploadBtn)
        uploadBtn.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            imagePickerActivityResult.launch(galleryIntent)
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

    private fun startEditActivity() {
        val intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra("CURRENT_NAME", userNameTextView.text.toString())
        intent.putExtra("CURRENT_PHONE", phoneTextView.text.toString())
        intent.putExtra("CURRENT_ADDRESS", addressTextView.text.toString())
        startActivity(intent)
        //finish()
    }

}

