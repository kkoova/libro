package com.korobeynikova.libro

class User {

    private lateinit var login: String
    private lateinit var email: String
    private lateinit var stars: String

    fun getLogin(): String {
        return login
    }
    fun setLogin(login: String) {
        this.login = login
    }

    fun getEmail(): String {
        return email
    }
    fun setEmail(email: String) {
        this.email = email
    }

    fun getStars(): String {
        return stars
    }
    fun setStars(stars: String) {
        this.stars = stars
    }
    constructor(login: String, email: String, stars: String) {
        this.login = login
        this.email = email
        this.stars = stars
    }

    constructor()

}