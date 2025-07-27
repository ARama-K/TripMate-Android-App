package com.example.travelapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travelapp.User1.Use
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_login)

        db = FirebaseFirestore.getInstance()

        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val registerButton = findViewById<MaterialButton>(R.id.signupButton)
        val loginButton = findViewById<Button>(R.id.signinButton)

        registerButton.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        loginButton.setOnClickListener {
            val txtEmail = emailEditText.text.toString().trim()
            val txtPwd = passwordEditText.text.toString()

            when {
                TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPwd) -> {
                    toastMsg("Empty Username or Password")
                    setValidationFlag("EmptyUsernameOrPassword")
                }
                txtPwd.length < 6 -> {
                    toastMsg("Password must be at least 6 characters.")
                    setValidationFlag("PasswordTooShort")
                }
                !Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches() -> {
                    toastMsg("Invalid email address.")
                    setValidationFlag("InvalidEmailAddress")
                }
                !txtPwd.matches("(.*[A-Z].*)".toRegex()) -> {
                    toastMsg("Password must contain at least one capital letter.")
                    setValidationFlag("PasswordNoCapitalLetter")
                }
                else -> loginUser(txtEmail, txtPwd)
            }
        }
    }

    private fun loginUser(txtEmail: String, txtPwd: String) {
        db.collection("users").document(txtEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val storedPassword = document.getString("password")
                    if (storedPassword == txtPwd) {
                        Use.setEmail(txtEmail)
                        toastMsg("Login Successful")
                        setValidationFlag("LoginSuccessful")
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        toastMsg("The password or email address is incorrect.")
                        setValidationFlag("LoginFailed")
                    }
                } else {
                    toastMsg("The password or email address is incorrect.")
                    setValidationFlag("LoginFailed")
                }
            }
            .addOnFailureListener {
                toastMsg("Login failed. Please try again.")
                setValidationFlag("LoginFailed")
            }
    }

    private fun toastMsg(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setValidationFlag(flag: String) {
        val sharedPref = getSharedPreferences("LoginActivityPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean(flag, true)
            apply()
        }
    }
}
