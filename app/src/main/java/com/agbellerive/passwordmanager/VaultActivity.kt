package com.agbellerive.passwordmanager

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class VaultActivity : AppCompatActivity() {
    private lateinit var sharedPref : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vault)
        sharedPref = getSharedPreferences("manager-info", Context.MODE_PRIVATE)

        var allAccounts = FileManager().readDocumentFromUri(Uri.parse(sharedPref.getString("path", "").toString()), this )


    }
}