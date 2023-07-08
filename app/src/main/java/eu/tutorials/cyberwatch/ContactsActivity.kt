package eu.tutorials.cyberwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class ContactsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var ifEmpty: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        recyclerView = findViewById(R.id.myContactsRecycle)
        ifEmpty=findViewById(R.id.ifempty)

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
                .collection("Lawyer")
                .document(currentUserEmail)
                .collection("clients")

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
        val clientCollection = firestore.collection("Client")
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
        }

        override fun getItemCount(): Int {
            return contacts.size
        }

        inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTextView: TextView = itemView.findViewById(R.id.titleTextView)
            val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
            val telTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        }
    }

    data class Contact(val name: String, val email: String, val tel: String)
}
