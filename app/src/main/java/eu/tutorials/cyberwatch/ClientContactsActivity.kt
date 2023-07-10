package eu.tutorials.cyberwatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ClientContactsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var ifEmpty: LinearLayout
    private lateinit var searchBtn: ImageButton
    private var storageRef = Firebase.storage.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_contacts)

        recyclerView = findViewById(R.id.myContactsRecycle)
        ifEmpty=findViewById(R.id.ifempty)

        searchBtn=findViewById(R.id.searchButton)
        searchBtn.setOnClickListener(View.OnClickListener {
            //FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        })

        firestore = FirebaseFirestore.getInstance()
        contactAdapter = ContactAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactAdapter

        fetchClientsForLawyer()
    }
    private fun fetchClientsForLawyer() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        if (currentUserEmail != null) {
            val requestsCollection = firestore
                .collection("Client")
                .document(currentUserEmail)
                .collection("lawyers")

            requestsCollection.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val requestEmails = querySnapshot.documents.map { document ->
                            document.id
                        }
                        fetchClientDetails(requestEmails)
                        recyclerView.visibility = View.VISIBLE
                        ifEmpty.visibility=View.INVISIBLE

                    } else {
                        recyclerView.visibility = View.GONE
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("RequestActivity", "Error fetching requests", e)
                    recyclerView.visibility = View.GONE
                }
        }
    }

    private fun fetchClientDetails(clientEmails: List<String>) {
        val clientCollection = firestore.collection("Lawyer")
        val clientQuery = clientCollection.whereIn("email", clientEmails)

        clientQuery.get()
            .addOnSuccessListener { querySnapshot ->
                val contacts = querySnapshot.documents.mapNotNull { document ->
                    val name = document.getString("name")
                    val email = document.getString("email")
                    val tel = document.getString("tel")
                    if (name != null && email != null && tel != null) {
                        Contact(name, email, tel)
                    } else {
                        null
                    }
                }

                contactAdapter.setData(contacts)
            }
            .addOnFailureListener { e ->
                Log.e("ContactsActivity", "Error fetching client details", e)
            }
    }

    private inner class ContactAdapter : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
        private val contacts: MutableList<Contact> = mutableListOf()

        fun setData(data: List<Contact>) {
            contacts.clear()
            contacts.addAll(data)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.my_lawyer_item, parent, false)
            return ContactViewHolder(view)
        }

        override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
            val contact = contacts[position]

            holder.nameTextView.text = contact.name
            holder.emailTextView.text = contact.email
            holder.telTextView.text = contact.tel

            val profilePictureRef = storageRef.child("ProfilePictures/${contact.email}")
            profilePictureRef.downloadUrl
                .addOnSuccessListener { uri ->
                    Glide.with(holder.itemView.context)
                        .load(uri)
                        .into(holder.profileImageView)
                }
                .addOnFailureListener { exception ->
                    // Handle error
                    holder.profileImageView.setImageResource(R.drawable.usericon)
                }
            holder.messageBtn.setOnClickListener {
                val intent = Intent(this@ClientContactsActivity, ChatActivity::class.java)
                val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
                intent.putExtra("senderEmail", currentUserEmail)
                intent.putExtra("receiverEmail", contact.email)
                intent.putExtra("contactName", contact.name)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return contacts.size
        }

        inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTextView: TextView = itemView.findViewById(R.id.titleTextView)
            val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
            val telTextView: TextView = itemView.findViewById(R.id.phoneTextView)
            val profileImageView: ImageView = itemView.findViewById(R.id.imageView3)
            val messageBtn : Button = itemView.findViewById(R.id.messageBtn)
        }
    }

    data class Contact(val name: String, val email: String, val tel: String)
}