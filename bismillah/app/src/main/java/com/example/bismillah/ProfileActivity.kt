package com.example.bismillah

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPoints: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvDetailAddress: TextView
    private lateinit var btnLogout: Button
    private lateinit var profilePicture: ImageView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Bind views
        tvUsername = findViewById(R.id.tv_username)
        tvPoints = findViewById(R.id.tv_points)
        tvAddress = findViewById(R.id.tv_address)
        tvDetailAddress = findViewById(R.id.tv_detail_address)
        btnLogout = findViewById(R.id.btn_logout)
        profilePicture = findViewById(R.id.profile_picture)
        tvUsername = findViewById(R.id.tv_username)
        tvEmail = findViewById(R.id.tv_email)

        // Fetch the user profile from Firebase
        fetchUserProfile()

        // Handle logout button click
        btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun fetchUserProfile() {
        // Fetch the currently logged-in user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Pengguna tidak terautentikasi", Toast.LENGTH_SHORT).show()
            return
        }

        // Database reference for the user data
        database = FirebaseDatabase.getInstance().getReference("users").child(userId)

        // Fetch the user data from Firebase Realtime Database
        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Fetch user data
                val username = snapshot.child("username").getValue(String::class.java) ?: "Nama tidak ditemukan"
                val email = snapshot.child("email").getValue(String::class.java) ?: "Email tidak ditemukan"
                val points = snapshot.child("poin").getValue(Int::class.java) ?: 0
                val address = snapshot.child("alamat").getValue(String::class.java) ?: "Alamat tidak ditemukan"
                val detailAddress = snapshot.child("detail_alamat").getValue(String::class.java) ?: "Detail alamat tidak ditemukan"

                // Update the UI with the fetched data
                tvUsername.text = username
                tvEmail.text = email
                tvPoints.text = points.toString()
                tvAddress.text = address
                tvDetailAddress.text = detailAddress
            } else {
                Toast.makeText(this, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show()
        }
    }


    private fun logoutUser() {
        // Sign out the user from Firebase Authentication
        FirebaseAuth.getInstance().signOut()

        // Redirect to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
