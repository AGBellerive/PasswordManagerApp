package com.agbellerive.passwordmanager

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class VaultActivity : AppCompatActivity() {
    private lateinit var sharedPref : SharedPreferences

    private lateinit var allAccountsView : RecyclerView
    private lateinit var siteDisplay : TextView
    private lateinit var usernameDisplay : TextView
    private lateinit var emailDisplay : TextView
    private lateinit var passwordDisplay : TextView
    private lateinit var otherDisplay : TextView

    private lateinit var searchAccount : EditText

    private lateinit var allAccounts : ArrayList<Account>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vault)
        sharedPref = getSharedPreferences("manager-info", Context.MODE_PRIVATE)

        allAccounts = FileManager().readDocumentFromUri(Uri.parse(sharedPref.getString("path", "").toString()), this)

        allAccountsView = findViewById(R.id.allAccounts)
        allAccountsView.layoutManager = LinearLayoutManager(this)
        val adapter = AccountsAdapter(allAccounts)
        allAccountsView.adapter = adapter

        siteDisplay = findViewById(R.id.siteDisplay)
        usernameDisplay = findViewById(R.id.usernameDisplay)
        emailDisplay = findViewById(R.id.emailDisplay)
        passwordDisplay = findViewById(R.id.passwordDisplay)
        otherDisplay = findViewById(R.id.otherDisplay)

        searchAccount = findViewById(R.id.searchAccount)

        siteDisplay.text = ""
        usernameDisplay.text = ""
        emailDisplay.text = ""
        passwordDisplay.text = ""
        otherDisplay.text = ""

        adapter.setOnItemClickListiner(object : AccountsAdapter.onClickListner{
            override fun onItemClick(position: Int) {
                setDisplay(allAccounts[position])
            }

        })

    }

    fun searchOnClick(view: View) {
        var found = false
        for(account in allAccounts){
            if(account.Site.lowercase().contains(searchAccount.text.toString().lowercase())){
                setDisplay(account)
                found = true

                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken,0)

                break
            }
        }
        if(!found) Toast.makeText(this,"Cant find account",Toast.LENGTH_LONG).show()
    }

    private fun setDisplay(account : Account){
        siteDisplay.text = account.Site
        usernameDisplay.text = account.Username
        emailDisplay.text = account.Email
        passwordDisplay.text = account.Password
        otherDisplay.text = account.Others
    }
}