package eu.tutorials.cyberwatch

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var appIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appIcon = findViewById(R.id.app_icon)

        val slideRightAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right)
        appIcon.startAnimation(slideRightAnimation)

        slideRightAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                /*val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    // User is signed in, log them out
                    FirebaseAuth.getInstance().signOut()
                }*/
                navigateToHome()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }


}
