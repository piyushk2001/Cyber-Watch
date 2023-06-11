package eu.tutorials.cyberwatch.model

class User {
    var name: String? = null
    var adresse: String? = null
    var tel: String? = null
    var email: String? = null
    var type: String? = null

    constructor() {
        //need firebase
    }

    constructor(name: String?, adresse: String?, tel: String?, email: String?, type: String?) {
        this.name = name
        this.adresse = adresse
        this.tel = tel
        this.email = email
        this.type = type
    }
}