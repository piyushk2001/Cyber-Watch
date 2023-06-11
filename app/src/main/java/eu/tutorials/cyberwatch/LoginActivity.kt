package eu.tutorials.cyberwatch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupTextView: TextView
    private lateinit var forgotpasswordText: TextView
    private var isSignupMode = false
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.login_email)
        passwordEditText = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        signupTextView = findViewById(R.id.signUpRedirectText)
        forgotpasswordText = findViewById(R.id.forgot_password)
        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (isSignupMode) {
                signUp(email, password)
            } else {
                login(email, password)
            }
        }
        signupTextView.setOnClickListener {
            toggleSignupMode()
        }

    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "loginWithEmail:success")
                    Toast.makeText(this, "Successfully logged in!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.w(TAG, "loginWithEmail:failure", task.exception)
                    // Handle login failure
                }
            }
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(this, "Successfully signed up!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, FirstSignUpActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    // Handle signup failure
                    Toast.makeText(this, "Authentication failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun toggleSignupMode() {
        isSignupMode = !isSignupMode

        if (isSignupMode) {
            loginButton.text = "Sign Up"
            signupTextView.text = "Already have an account? Log in"
            //forgotpasswordText.visibility = View.INVISIBLE
            isSignupMode = true

        } else {
            loginButton.text = "Log In"
            signupTextView.text = "Don't have an account? Sign up"
        }
    }


    companion object {
        private const val TAG = "LoginActivity"
    }
}
