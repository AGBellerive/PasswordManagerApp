package com.agbellerive.passwordmanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import kotlin.system.exitProcess

class PasswordActivity : AppCompatActivity() {

    private lateinit var masterPassword : EditText
    private var passwordAttempts = 0
    private lateinit var sharedPref : SharedPreferences

    //https://developer.android.com/identity/sign-in/biometric-auth
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)


        masterPassword = findViewById(R.id.masterPassword)
        sharedPref = getSharedPreferences("manager-info", Context.MODE_PRIVATE)
        executor = ContextCompat.getMainExecutor(this)

        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                if(sharedPref.getBoolean("biometrics",false)) biometricConfig()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e("passwordActivity", "No biometric features available on this device.")
        }

        masterPassword.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && sharedPref.getBoolean("biometrics",false)) {
                biometricConfig()
                biometricPrompt.authenticate(promptInfo)
            }
        }


    }

    fun enterVault(view: View) {

        val encryptedPassword = sharedPref.getString("password","")

        //val encryptedPassword = sharedPref.getString("password","").hashCode()

        if (encryptedPassword != null) {
            if(encryptedPassword == masterPassword.text.toString()){
                startActivity(Intent(this, VaultActivity::class.java))
            }
            else{
                passwordAttempts++
                if(passwordAttempts == 3){
                    Toast.makeText(this,"Too many attempts, try again later",Toast.LENGTH_SHORT).show()
                    PasswordActivity().finish()
                    exitProcess(0)
                }
                Toast.makeText(this,"Incorrect Password attempt #$passwordAttempts",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun biometricConfig(){
        biometricPrompt = BiometricPrompt(this,executor,
            object : BiometricPrompt.AuthenticationCallback(){
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext,"Authentication error : $errString",Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext,"Authenticated ",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, VaultActivity::class.java))
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext,"Authentication failed",Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login to access your vault")
            .setSubtitle("Log in using your devices biometrics")
            .setNegativeButtonText("Use Text Password")
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
    }

    fun onHintClick(view: View) {
        Toast.makeText(this,sharedPref.getString("hint","No hint available"),Toast.LENGTH_SHORT).show()
    }
}