package eu.tutorials.cyberwatch

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var appIcon: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        appIcon = findViewById(R.id.app_icon)

        val slideRightAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right)
        appIcon.startAnimation(slideRightAnimation)

        slideRightAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                val currentEmail = auth.currentUser?.email
                checkUserAuthentication(currentEmail)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun checkUserAuthentication(email:String?) {
        if (email != null) {
            // User is signed in
            val userDocRef =firestore.collection("User").document(email)
            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userType = documentSnapshot.getString("type")
                        userType?.let {
                            when (it) {
                                "Lawyer" -> navigateToLawyerHome()
                                "Client" -> navigateToHome()
                                else -> navigateToHome()
                            }
                        } ?: navigateToHome() // If userType is null, navigate to HomeActivity
                    } else {
                        navigateToHome() // User document doesn't exist, navigate to HomeActivity
                    }
                }
                .addOnFailureListener { exception ->
                    navigateToHome() // Error occurred, navigate to HomeActivity
                }
        } else {
            // User is not logged in
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLawyerHome() {
        val intent = Intent(this, LawyerHome::class.java)
        startActivity(intent)
        finish()
    }
}
