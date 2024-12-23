package com.example.bismillah

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bismillah.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Tombol Register
        binding.btnRegister1.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val username = binding.inputUser.text.toString()
            val password = binding.inputPassword.text.toString()

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email, Username, dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                // Lakukan proses registrasi
                fRegister(email, username, password)
            }
        }
    }

    // Fungsi untuk registrasi akun ke Firebase
    private fun fRegister(email: String, username: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registrasi berhasil
                    val userId = mAuth.currentUser?.uid ?: ""
                    simpanDataUser(userId, email, username)

                } else {
                    // Registrasi gagal
                    val errorMessage = task.exception?.message ?: "Registrasi gagal. Coba lagi."
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Fungsi untuk menyimpan data pengguna ke Firebase Realtime Database
    private fun simpanDataUser(userId: String, email: String, username: String) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users").child(userId)

        val userData = mapOf(
            "email" to email,
            "username" to username,
            "poin" to 0,
            "badges" to listOf<String>()
        )

        userRef.setValue(userData)
            .addOnSuccessListener {
                // Hapus sesi pengguna dan arahkan ke halaman login
                mAuth.signOut()
                Toast.makeText(this, "Registrasi berhasil. Silakan login.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Gagal menyimpan data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
