package com.agbellerive.passwordmanager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat


class SetUpActivity : AppCompatActivity() {

    private lateinit var fileUri : String

    private lateinit var password : EditText
    private lateinit var passwordConfirm : EditText
    private lateinit var passwordHint : EditText

    private lateinit var matchingWarning : TextView
    private lateinit var lengthWarning : TextView

    private lateinit var biometric: SwitchCompat

    private lateinit var confirmButton : Button

    private var passwordCheck = false
    private var fileCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_up)

        password = findViewById(R.id.password)
        passwordConfirm = findViewById(R.id.confirmedPassword)
        passwordHint = findViewById(R.id.passwordHint)

        matchingWarning = findViewById(R.id.matchWarning)
        lengthWarning = findViewById(R.id.lengthWarning)

        biometric = findViewById(R.id.biometricToggle)

        confirmButton = findViewById(R.id.confirm_button)

        //https://medium.com/@mdayanc/how-to-use-on-focus-change-to-format-edit-text-on-android-studio-bf59edf66161
        password.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                if(password.length() <= 8){
                    lengthWarning.visibility = View.VISIBLE;
                }
                else{
                    lengthWarning.visibility = View.INVISIBLE;
                }
            }
        }

        passwordConfirm.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                if(password.text.trim() != passwordConfirm.text.trim()){
                    matchingWarning.visibility = View.VISIBLE;
                }
                else{
                    matchingWarning.visibility = View.INVISIBLE;
                    passwordCheck = (password.text.trim() == passwordConfirm.text.trim())
                    buttonCheck()
                }
            }
        }
    }

    fun browseOnClick(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type ="application/json" // this will search specifically for document type of .json
        }
        startActivityForResult(intent, 460)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 460 && resultCode == Activity.RESULT_OK && data != null){
            val uri : Uri? = data.data

            if (uri != null) {
                contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            fileUri = uri.toString()
            fileCheck = fileUri.isNotBlank()
            buttonCheck();
        }
    }

    private fun buttonCheck(){
        confirmButton.isEnabled = passwordCheck && fileCheck
        if(confirmButton.isEnabled){
            confirmButton.backgroundTintList = ContextCompat.getColorStateList(this,R.color.confirmGreen)
        }
        else{
            confirmButton.backgroundTintList = ContextCompat.getColorStateList(this,R.color.disabledBtn)
        }
    }
    fun confirmOnClick(view: View) {
        val sharedPref = getSharedPreferences("manager-info", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            //key, value
            putBoolean("biometrics",biometric.isChecked)
            putString("password",password.text.toString())
            //putString("password",password.text.toString().hashCode())
            putString("hint",passwordHint.text.toString())
            putString("path",fileUri)
            apply()
        }
        startActivity(Intent(this,VaultActivity::class.java))
        finish()
    }
}