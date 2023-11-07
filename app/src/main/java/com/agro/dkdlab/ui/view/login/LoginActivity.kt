package com.agro.dkdlab.ui.view.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.custom.validateForError
import com.agro.dkdlab.databinding.ActivityLoginBinding
import com.agro.dkdlab.ui.view.SoilTestListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.inputLayoutAgentName.validateForError()
        binding.inputLayoutPassword.validateForError()
        binding.textViewForgotPassword.setOnClickListener {
//            startActivity(Intent(this, CreateReportActivity::class.java))
        }

        binding.layoutLogin.setOnClickListener {
            when {
                binding.editTextAgentName.text.toString().trim().isEmpty() -> {
                    binding.inputLayoutAgentName.requestFocus()
                    binding.inputLayoutAgentName.error = "Please enter user name"
                }
                binding.editTextPassword.text.toString().trim().isEmpty() -> {
                    binding.inputLayoutPassword.requestFocus()
                    binding.inputLayoutPassword.error = resources.getString(R.string.enter_password)
                }
                binding.editTextAgentName.text.toString().trim()!="Admin"->{
                    binding.inputLayoutAgentName.requestFocus()
                    binding.inputLayoutAgentName.error = "User not exist"
                }
                binding.editTextPassword.text.toString().trim()!="demo"->{
                    binding.inputLayoutPassword.requestFocus()
                    binding.inputLayoutPassword.error = "Incorrect password"
                }
                else -> login()
            }
        }

    }
    private fun login() {
        MyApp.get().setLogin()
        startActivity(Intent(applicationContext, SoilTestListActivity::class.java))
        finish()
    }

    var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.to_exit), Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed(
            { doubleBackToExitPressedOnce = false },
            2000
        )
    }
}