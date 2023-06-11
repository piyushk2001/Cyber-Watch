package eu.tutorials.cyberwatch.fireStoreApi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import eu.tutorials.cyberwatch.model.User

class UserHelper {
    var db = FirebaseFirestore.getInstance()
    var UsersRef: CollectionReference = db.collection("User")

    fun addUser(name: String?, adresse: String?, tel: String?, type: String?) {
        val user = User(name, adresse, tel, FirebaseAuth.getInstance().currentUser!!.email, type)
        UsersRef.document(FirebaseAuth.getInstance().currentUser!!.email!!).set(user)
    }
}