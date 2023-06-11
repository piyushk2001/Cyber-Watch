package eu.tutorials.cyberwatch.fireStoreApi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import eu.tutorials.cyberwatch.model.Lawyer

class LawyerHelper {
    var db = FirebaseFirestore.getInstance()
    var LawyerRef: CollectionReference = db.collection("Lawyer")

    public fun addLawyer(name: String?, address: String?, tel: String?, specialite: String?) {
        val lawyer =
            Lawyer(name, address, tel, FirebaseAuth.getInstance().currentUser!!.email, specialite)
        LawyerRef.document(FirebaseAuth.getInstance().currentUser!!.email!!).set(lawyer)
    }
}
