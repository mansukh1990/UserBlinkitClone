package com.example.userblinkitclone.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.userblinkitclone.databinding.ActivityMainBinding

class AuthMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}