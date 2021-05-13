package it.benche.upofood.notifications

class Token {

    private lateinit var token: String

    constructor(token: String) {
        this.token = token
    }

    constructor() {
    }

    fun getToken(): String {
        return token
    }

    fun setToken(token: String) {
        this.token = token
    }
}