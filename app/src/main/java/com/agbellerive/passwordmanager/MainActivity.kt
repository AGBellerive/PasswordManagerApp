package com.agbellerive.passwordmanager

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    private var time : Long = 1500 //3000 3 seconds for splash screen
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(time)

        installSplashScreen()

        val sharedPref = getSharedPreferences("manager-info", Context.MODE_PRIVATE)
        if(sharedPref.contains("path")){ //if there is a path saved in shared pref, open
            startActivity(Intent(this,VaultActivity::class.java))
        }
        else{
            startActivity(Intent(this,SetUpActivity::class.java))
        }
    }
}