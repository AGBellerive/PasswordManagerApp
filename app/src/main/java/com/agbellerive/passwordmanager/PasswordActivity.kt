package com.agbellerive.passwordmanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
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
    private lateinit var enterButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        masterPassword = findViewById(R.id.masterPassword)

        enterButton = findViewById(R.id.enter_button)
        //By default the button is disabled

        sharedPref = getSharedPreferences("manager-info", Context.MODE_PRIVATE)
        executor = ContextCompat.getMainExecutor(this)

        val biometricManager = BiometricManager.from(this)

        biometricAuth(biometricManager)
        masterPasswordConfig()
    }

    private fun biometricAuth(biometricManager: BiometricManager) {
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                if (sharedPref.getBoolean("biometrics", false)) biometricConfig()

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e("passwordActivity", "No biometric features available on this device.")
        }
    }

    /**
     * This function is called when the user clicks the enter button
     * It will compare the password to what is stored in the shared pref
     */
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

    /**
     * This function configures the biometric prompt for the user to authenticate
     */
    private fun biometricConfig(){
        biometricPrompt = BiometricPrompt(this,executor,
            object : BiometricPrompt.AuthenticationCallback(){
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext,"Authentication error : $errString",Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    startActivity(Intent(applicationContext, VaultActivity::class.java))
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext,"Authentication failed",Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login to access your vault") //Main title of the prompt
            .setSubtitle("Log in using your devices biometrics") //Sub title of the prompt, smaller text
            .setNegativeButtonText("Use Text Password") // Bottom left text
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
    }

    /**
     * This function is called when the user clicks on the hint button if they have a hint configured
     */
    fun onHintClick(view: View) {
        Toast.makeText(this,sharedPref.getString("hint","No hint available"),Toast.LENGTH_SHORT).show()
    }

    /**
     * This function configures the master password field attaching focus listeners, on key listeners
     * and on click. These will bring up the biometrics
     *
     */
    private fun masterPasswordConfig(){
        masterPassword.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && sharedPref.getBoolean("biometrics",false)) {
                biometricConfig()
                biometricPrompt.authenticate(promptInfo)
            }
        }

        masterPassword.setOnKeyListener { view, keycode, keyEvent ->
            if(keyEvent.action == KeyEvent.ACTION_DOWN && keycode == KeyEvent.KEYCODE_ENTER){
                enterVault(view)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        masterPassword.setOnClickListener {
            if (sharedPref.getBoolean("biometrics",false)) {
                biometricConfig()
                biometricPrompt.authenticate(promptInfo)
            }
        }


        masterPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
                // You can use 's' to get the text before the change.
            }

            // This method is called when the text is changing.
            // 's' represents the current text. 'start' is the starting index of the changed text.
            // 'before' is the number of characters that were replaced.  'count' is the number of new characters.
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if(s.isNotEmpty()){
                        enterButton.isEnabled = true
                        enterButton.backgroundTintList = ContextCompat.getColorStateList(this@PasswordActivity,R.color.confirmGreen)
                    }
                    else{
                        enterButton.isEnabled = false
                        enterButton.backgroundTintList = ContextCompat.getColorStateList(this@PasswordActivity,R.color.disabledBtn)
                    }
                }
            }

            // This method is called after the text has been changed.
            override fun afterTextChanged(s: Editable?) {
                // 's' represents the final editable text content.
                // This is often the most useful method for reacting to data changes.
            }
        })
    }
}