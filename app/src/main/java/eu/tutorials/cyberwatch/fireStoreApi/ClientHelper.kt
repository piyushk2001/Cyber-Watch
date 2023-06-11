package eu.tutorials.cyberwatch.fireStoreApi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import eu.tutorials.cyberwatch.model.Client

class ClientHelper {
    var db = FirebaseFirestore.getInstance()
    var ClientRef = db.collection("Client")

    fun addClient(name: String?, adresse: String?, tel: String?) {
        val client = Client(
            name, adresse, tel, FirebaseAuth.getInstance().currentUser!!
                .email, "aaa", "aaa"
        )
        println("Create object client")
        ClientRef.document(FirebaseAuth.getInstance().currentUser!!.email!!).set(client)
    }
}