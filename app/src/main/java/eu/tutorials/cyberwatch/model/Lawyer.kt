package eu.tutorials.cyberwatch.model
class Lawyer {
    var name: String? = null
    var adresse: String? = null
    var tel: String? = null
    var email: String? = null
    var specialite: String? = null

    constructor() {
        //needed for firebase
    }

    constructor(name: String?, adresse: String?, tel: String?, email: String?, specialite: String?) {
        this.name = name
        this.adresse = adresse
        this.tel = tel
        this.email = email
        this.specialite = specialite
    }
}
