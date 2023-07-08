package eu.tutorials.cyberwatch


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class DocumentActivity : AppCompatActivity() {
    private lateinit var registerBtn: Button
    private lateinit var uploadBtn: Button
    // creating a storage reference
    private var storageRef = Firebase.storage.reference

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
    // lambda expression to receive a result back, here we
        // receive single item(photo) on selection
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // getting URI of selected Image
                val imageUri: Uri? = result.data?.data

                // extract the file name with extension
                val sd = getFileName(applicationContext, imageUri!!)

                // Upload Task with upload to directory 'file'
                // and name of the file remains same
                val uploadTask = storageRef.child("file/$sd").putFile(imageUri)

                // On success, show toast message
                uploadTask.addOnSuccessListener {
                    Toast.makeText(this, "Successfully uploaded", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Log.e("Firebase", "Image Upload fail")
                }
            }
        }

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
        setContentView(R.layout.activity_document)

        registerBtn = findViewById(R.id.registerBtn)
        registerBtn.setOnClickListener(View.OnClickListener {
            //FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        })

        uploadBtn = findViewById(R.id.uploadBtn)
        uploadBtn.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            imagePickerActivityResult.launch(galleryIntent)
        }
    }
}

