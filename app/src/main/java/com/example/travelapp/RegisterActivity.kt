package com.example.travelapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db = FirebaseFirestore.getInstance()

        val registerButton = findViewById<MaterialButton>(R.id.addButton)
        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<TextInputEditText>(R.id.confirmPasswordEditText)

        registerButton.setOnClickListener {
            val email_txt = emailEditText.text.toString()
            val password_txt = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            handleRegistration(email_txt, password_txt, confirmPassword)
        }
    }

    private fun handleRegistration(email_txt: String, password_txt: String, confirmPassword: String) {
        when {
            TextUtils.isEmpty(email_txt) || TextUtils.isEmpty(password_txt) -> setToastMsg("Empty Username or Password")
            password_txt.length < 6 -> setToastMsg("Password must be at least 6 characters.")
            !Patterns.EMAIL_ADDRESS.matcher(email_txt).matches() -> setToastMsg("Invalid email address.")
            !password_txt.matches("(.*[A-Z].*)".toRegex()) -> setToastMsg("Password must contain at least one capital letter.")
            password_txt != confirmPassword -> setToastMsg("Passwords must match.")
            else -> registerUser(email_txt, password_txt)
        }
    }

    private fun registerUser(email_txt: String, password_txt: String) {
        val user = hashMapOf("password" to password_txt)

        db.collection("users").document(email_txt)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    setToastMsg("User already exists.")
                } else {
                    db.collection("users").document(email_txt)
                        .set(user)
                        .addOnSuccessListener {
                            setToastMsg("Registration Successful")
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        }
                        .addOnFailureListener {
                            setToastMsg("Registration failed. Try again.")
                        }
                }
            }
            .addOnFailureListener {
                setToastMsg("Error accessing database.")
            }
    }

    private fun setToastMsg(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
