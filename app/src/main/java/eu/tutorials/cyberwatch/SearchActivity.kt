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

class SearchActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var lawyerAdapter: LawyerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.serachPatRecycle)

        firestore = FirebaseFirestore.getInstance()
        lawyerAdapter = LawyerAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = lawyerAdapter

        fetchLawyersFromFirestore()
    }

    private fun fetchLawyersFromFirestore() {
        firestore.collection("Lawyer")
            .get()
            .addOnSuccessListener { result ->
                val lawyers = ArrayList<Lawyer>()

                for (document in result) {
                    val email = document.getString("email")
                    val name = document.getString("name")
                    val tel = document.getString("tel")
                    val specialite = document.getString("specialite")

                    val lawyer = Lawyer(name, tel, email, specialite)
                    lawyers.add(lawyer)
                }

                lawyerAdapter.setData(lawyers)
            }
            .addOnFailureListener { e ->
                Log.e("SearchLawyerActivity", "Error fetching lawyers", e)
            }
    }


    private inner class LawyerAdapter : RecyclerView.Adapter<LawyerAdapter.LawyerViewHolder>() {
        private val lawyers: MutableList<Lawyer> = mutableListOf()

        fun setData(data: List<Lawyer>) {
            lawyers.clear()
            lawyers.addAll(data)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LawyerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.my_search_item, parent, false)
            return LawyerViewHolder(view)
        }

        override fun onBindViewHolder(holder: LawyerViewHolder, position: Int) {
            val lawyer = lawyers[position]

            holder.addBtn.setOnClickListener {
                val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
                val lawyerEmail = lawyer.email

                if (currentUserEmail != null && lawyerEmail != null) {
                    addRequestToLawyer(currentUserEmail, lawyerEmail, holder)
                }
            }

            holder.titleTextView.text = lawyer.name
            //holder.phoneTextView.text = lawyer.phone
            holder.emailTextView.text = lawyer.email
            holder.specialiteTextView.text=lawyer.specialite
        }

        private fun addRequestToLawyer(userEmail : String, lawyerEmail: String, holder: LawyerViewHolder) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            //val userEmail = currentUser?.email

            if (userEmail != null) {
                val requestsCollection = firestore
                    .collection("Lawyer")
                    .document(lawyerEmail)
                    .collection("requests")

                val requestDocument = requestsCollection.document(userEmail)
                requestDocument
                    .set(mapOf<String, Any>())
                    .addOnSuccessListener {
                        Toast.makeText(this@SearchActivity, "Request sent successfully", Toast.LENGTH_SHORT).show()
                        holder.addBtn.text = "Requested"
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@SearchActivity, "Failed to send request", Toast.LENGTH_SHORT).show()
                        Log.e("SearchActivity", "Error adding request", e)
                    }
            }
        }


        override fun getItemCount(): Int {
            return lawyers.size
        }

        inner class LawyerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
            //val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
            val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
            val specialiteTextView: TextView = itemView.findViewById(R.id.specialistTextView)
            val addBtn: Button =itemView.findViewById(R.id.addBtn)
        }
    }

    data class Lawyer(val name: String?, val phone: String?, val email: String?, val specialite: String?)
}
