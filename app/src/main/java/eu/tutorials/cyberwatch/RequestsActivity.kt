package eu.tutorials.cyberwatch


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
            val requestsCollection = firestore
                .collection("Lawyer")
                .document(currentUserEmail)
                .collection("requests")

            requestsCollection.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val requestEmails = querySnapshot.documents.map { document ->
                            document.id
                        }
                        fetchUsersFromClientsCollection(requestEmails)
                        recyclerView.visibility = View.VISIBLE
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
                setupRequestItemClickListeners(users)

            }
            .addOnFailureListener { e ->
                Log.e("RequestActivity", "Error fetching users", e)
            }
    }

    private fun setupRequestItemClickListeners(users: List<User>) {
        requestAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onAcceptClick(position: Int) {
                val user = users[position]
                val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

                if (currentUserEmail != null) {
                    addClientToLawyer(currentUserEmail, user.email)
                    removeRequest(position)
                    fetchRequestsForLawyer()
                }
            }

            override fun onCancelClick(position: Int) {
                removeRequest(position)
            }
        })
    }

    private fun addClientToLawyer(lawyerEmail: String, clientEmail: String) {
        val clientsCollection = firestore
            .collection("Lawyer")
            .document(lawyerEmail)
            .collection("clients")
        val lawyersCollection = firestore
            .collection("Client")
            .document(clientEmail)
            .collection("lawyers")

        val clientDocument = clientsCollection.document(clientEmail)
        clientDocument
            .set(mapOf<String, Any>())
            .addOnSuccessListener {
                Toast.makeText(this@RequestsActivity, "Client added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@RequestsActivity, "Failed to add client", Toast.LENGTH_SHORT).show()
                Log.e("RequestsActivity", "Error adding client", e)
            }

        val lawyerDocument = lawyersCollection.document(lawyerEmail)
        lawyerDocument
            .set(mapOf<String, Any>())
            .addOnSuccessListener {
                //Toast.makeText(this@RequestsActivity, "Client added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                //Toast.makeText(this@RequestsActivity, "Failed to add client", Toast.LENGTH_SHORT).show()
                Log.e("RequestsActivity", "Error adding client", e)
            }

    }

    private fun removeRequest(position: Int) {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        if (currentUserEmail != null) {
            val requestEmail = requestAdapter.getUserEmail(position)

            firestore.collection("Lawyer")
                .document(currentUserEmail)
                .collection("requests")
                .document(requestEmail)
                .delete()
                .addOnSuccessListener {
                    //Toast.makeText(this@RequestsActivity, "Request removed successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    //Toast.makeText(this@RequestsActivity, "Failed to remove request", Toast.LENGTH_SHORT).show()
                    Log.e("RequestsActivity", "Error removing request", e)
                }
        }
    }

    data class User(val name: String, val email: String)

    private interface OnItemClickListener {
        fun onAcceptClick(position: Int)
        fun onCancelClick(position: Int)
    }

    private inner class RequestAdapter : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {
        private val users: MutableList<User> = mutableListOf()
        private var onItemClickListener: OnItemClickListener? = null

        fun getUserEmail(position: Int): String {
            return users[position].email
        }

        fun setOnItemClickListener(listener: OnItemClickListener) {
            onItemClickListener = listener
        }

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
            val acceptBtn: Button = itemView.findViewById(R.id.acceptBtn)
            val cancelBtn: Button = itemView.findViewById(R.id.cancelBtn)

            init {
                acceptBtn.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener?.onAcceptClick(position)
                    }
                }

                cancelBtn.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener?.onCancelClick(position)
                    }
                }
            }
        }
    }
}

