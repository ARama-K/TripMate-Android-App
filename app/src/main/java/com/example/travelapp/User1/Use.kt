package com.example.travelapp.User1

object Use {
    private var email: String = ""

    fun setEmail(email: String) {
        this.email = email
    }

    fun getUserEmail(): String {
        return email
    }

    fun isUserEmailSet(): Boolean {
        return email.isNotBlank()
    }
}
