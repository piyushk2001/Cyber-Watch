package eu.tutorials.cyberwatch.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class Message {
    // --- SETTERS ---
    // --- GETTERS ---
    var message: String? = null

    @get:ServerTimestamp
    var dateCreated: Date? = null
    var userSender: String? = null

    constructor() {}
    constructor(message: String?, userSender: String?) {
        this.message = message
        this.userSender = userSender
    }

}