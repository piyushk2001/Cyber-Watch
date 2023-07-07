package eu.tutorials.cyberwatch


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RequestsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var requestAdapter: RequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests)

        recyclerView = findViewById(R.id.requestRecyclerView)

        firestore = FirebaseFirestore.getInstance()
        requestAdapter = RequestAdapter()

        recyclerView.adapter = requestAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchRequestsForLawyer()
    }

    private fun fetchRequestsForLawyer() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        if (currentUserEmail != null) {
            firestore.collection("Lawyer")
                .document(currentUserEmail)
                .collection("requests")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val requestEmails = querySnapshot.documents.map { document ->
                        document.id
                    }
                    fetchUsersFromClientsCollection(requestEmails)
                }
                .addOnFailureListener { e ->
                    Log.e("RequestActivity", "Error fetching requests", e)
                }
        }
    }


    private fun fetchUsersFromClientsCollection(userEmails: List<String>) {
        val usersCollection = firestore.collection("Client")
        val usersQuery = usersCollection.whereIn("email", userEmails)

        usersQuery.get()
            .addOnSuccessListener { querySnapshot ->
                val users = querySnapshot.documents.mapNotNull { document ->
                    val name = document.getString("name")
                    val email = document.getString("email")
                    if (name != null && email != null) {
                        User(name, email)
                    } else {
                        null
                    }
                }

                requestAdapter.setData(users)
            }
            .addOnFailureListener { e ->
                Log.e("RequestActivity", "Error fetching users", e)
            }
    }

    private inner class RequestAdapter : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {
        private val users: MutableList<User> = mutableListOf()

        fun setData(data: List<User>) {
            users.clear()
            users.addAll(data)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.my_request_item, parent, false)
            return RequestViewHolder(view)
        }

        override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
            val user = users[position]

            holder.nameTextView.text = user.name
            holder.emailTextView.text = user.email
        }

        override fun getItemCount(): Int {
            return users.size
        }

        inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTextView: TextView = itemView.findViewById(R.id.titleTextView)
            val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        }
    }

    data class User(val name: String, val email: String)
}
