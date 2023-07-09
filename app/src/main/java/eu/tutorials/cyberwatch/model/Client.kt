package eu.tutorials.cyberwatch.model

class Client {
    var name: String? = null
    private var adresse: String? = null
    var tel: String? = null
    var email: String? = null
    private var dateNaissance: String? = null
    private var situationFamiliale: String? = null


    fun constructor() {
        //needed for firebase
    }

    constructor(
        name: String?,
        adresse: String?,
        tel: String?,
        email: String?,
        dateNaissance: String?,
        situationFamiliale: String?
    ) {
        this.name = name
        this.adresse = adresse
        this.tel = tel
        this.email = email
        this.dateNaissance = dateNaissance
        this.situationFamiliale = situationFamiliale
    }

}