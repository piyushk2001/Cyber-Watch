package eu.tutorials.cyberwatch


import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.DateFormat
import java.util.*

class DocumentActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var uploadBtn: Button
    private lateinit var registerBtn: Button
    private lateinit var ifEmpty: TextView

    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var documentAdapter: DocumentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        recyclerView = findViewById(R.id.serachPatRecycle)
        uploadBtn = findViewById(R.id.uploadBtn)
        registerBtn = findViewById(R.id.registerBtn)
        ifEmpty = findViewById(R.id.ifempty)


        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        documentAdapter = DocumentAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = documentAdapter

        uploadBtn.setOnClickListener { uploadDocument() }
        registerBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        fetchUserDocuments()
    }

    private fun fetchUserDocuments() {
        val currentUserEmail = auth.currentUser?.email

        if (currentUserEmail != null) {
            val userDocumentsRef = storageRef.child("UserDocuments/$currentUserEmail")
            userDocumentsRef.listAll()
                .addOnSuccessListener { result ->
                    val documents = result.items.map { storageReference ->
                        getDocumentMetadata(storageReference)
                    }
                    documentAdapter.setData(documents)
                    ifEmpty.visibility = if (documents.isEmpty()) View.VISIBLE else View.GONE
                }
                .addOnFailureListener { exception ->
                    Log.e("DocumentActivity", "Error fetching user documents", exception)
                    ifEmpty.visibility = View.VISIBLE
                }
        }
    }

    private fun getDocumentMetadata(storageReference: StorageReference): Document {
        return Document(storageReference.name).apply {
            storageReference.metadata?.addOnSuccessListener { metadata ->
                // Get the file size in bytes from the metadata
                val sizeBytes = metadata.sizeBytes
                // Update the size property of the document
                size = sizeBytes
                // Notify the adapter of the data change
                documentAdapter.notifyDataSetChanged()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_UPLOAD = 1001
    }



    private fun uploadDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, REQUEST_CODE_UPLOAD)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_UPLOAD && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                val currentUserEmail = auth.currentUser?.email

                if (currentUserEmail != null) {
                    val fileName = getFileName(uri)
                    val fileExtension = getFileExtension(fileName)
                    val fileReference =
                        storageRef.child("UserDocuments/$currentUserEmail/$fileName.$fileExtension")

                    val uploadTask = fileReference.putFile(uri)

                    uploadTask.addOnSuccessListener {
                        Log.d("DocumentActivity", "Upload successful")
                        fetchUserDocuments()
                    }.addOnFailureListener { exception ->
                        Log.e("DocumentActivity", "Upload failed", exception)
                    }
                }
            }
        }
    }

    private fun registerDocument() {
        // Handle register document button click
    }

    private fun getFileName(uri: Uri): String {
        val path = uri.path
        val lastSeparatorIndex = path?.lastIndexOf("/")
        val fileNameWithExtension = path?.substring(lastSeparatorIndex!! + 1)

        // Remove the file extension if present
        val dotIndex = fileNameWithExtension?.lastIndexOf(".")
        return if (dotIndex != null && dotIndex >= 0) {
            fileNameWithExtension.substring(0, dotIndex-1)
        } else {
            fileNameWithExtension ?: ""
        }
    }


    private fun getFileExtension(fileName: String): String {
        val dotIndex = fileName.lastIndexOf('.')
        if (dotIndex > 0 && dotIndex < fileName.length - 1) {
            return fileName.substring(dotIndex + 1)
        }
        return ""
    }

    private fun formatSize(size: Long): String {
        if (size <= 0) {
            return "0 B"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format("%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }


    private inner class DocumentAdapter :
        RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {
        private val documents: MutableList<Document> = mutableListOf()

        fun setData(data: List<Document>) {
            documents.clear()
            documents.addAll(data)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.my_doc_item, parent, false)
            return DocumentViewHolder(view)
        }

        override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
            val document = documents[position]

            holder.titleTextView.text = document.name
            holder.dateTextView.text = formatDate(document.uploadDate)
            holder.sizeTextView.text = formatSize(document.size)

            holder.itemView.setOnClickListener {
                val currentUserEmail = auth.currentUser?.email
                if (currentUserEmail != null) {
                    val fileReference =
                        storageRef.child("UserDocuments/$currentUserEmail/${document.name}")
                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        openFile(uri)
                    }.addOnFailureListener { exception ->
                        Log.e("DocumentActivity", "Error downloading file", exception)
                    }
                }
            }
        }
        private fun formatDate(date: Date): String {
            val dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())
            return dateFormat.format(date)
        }


        override fun getItemCount(): Int {
            return documents.size
        }

        inner class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
            val dateTextView: TextView = itemView.findViewById(R.id.emailTextView)
            val sizeTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        }
    }

    data class Document(val name: String, var size: Long=0, val uploadDate: Date = Date())


    private fun openFile(uri: Uri) {
        val intent = Intent(this, DisplayFileActivity::class.java)
        intent.putExtra("fileUri", uri.toString())
        startActivity(intent)
    }


}

