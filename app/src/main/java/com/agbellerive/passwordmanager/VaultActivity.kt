package com.agbellerive.passwordmanager

import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.text.method.ScrollingMovementMethod
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility


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

    private lateinit var searchAccountButton : Button

    private lateinit var otherLabel : TextView

    private lateinit var otherScrollView : ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vault)
        sharedPref = getSharedPreferences("manager-info", Context.MODE_PRIVATE)

        allAccounts = FileManager().readDocumentFromUri(Uri.parse(sharedPref.getString("path", "").toString()), this)

        initViews()
        initRecyclerView()

        searchAccount.setOnKeyListener{ view, keycode, keyEvent ->
            if(keyEvent.action == KeyEvent.ACTION_DOWN && keycode == KeyEvent.KEYCODE_ENTER){
                searchAccount(view)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    /**
     * This function initializes the recycler view
     */
    private fun initRecyclerView() {
        allAccountsView.layoutManager = LinearLayoutManager(this)
        val adapter = AccountsAdapter(allAccounts)
        allAccountsView.adapter = adapter

        adapter.setOnItemClickListiner(object : AccountsAdapter.onClickListner {
            override fun onItemClick(position: Int) {
                setDisplay(allAccounts[position])
            }
        })
    }

    /**
     * This function initializes all the views in the layout
     */
    private fun initViews() {
        allAccountsView = findViewById(R.id.allAccounts)

        siteDisplay = findViewById(R.id.siteDisplay)
        usernameDisplay = findViewById(R.id.usernameDisplay)
        emailDisplay = findViewById(R.id.emailDisplay)
        passwordDisplay = findViewById(R.id.passwordDisplay)
        otherDisplay = findViewById(R.id.otherDisplay)

        searchAccount = findViewById(R.id.searchAccount)

        otherScrollView = findViewById(R.id.otherDisplayScrollView)
        otherLabel = findViewById(R.id.otherLabel)


        siteDisplay.text = ""
        usernameDisplay.text = ""
        emailDisplay.text = ""
        passwordDisplay.text = ""
        otherDisplay.text = ""
    }

    fun searchOnClick(view: View) {
        if(searchAccount.text.isEmpty()) return
        searchAccount(view)
    }

    /**
     * When the account is searched, it will linearly search the account list to find the account
     */
    private fun searchAccount( view: View) {
        var found = false
        for (account in allAccounts) {
            if (account.Site.lowercase().contains(searchAccount.text.toString().lowercase())) {
                setDisplay(account)
                found = true
                break
            }
        }
        if (!found) Toast.makeText(this, "Cant find account", Toast.LENGTH_LONG).show()

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0) //Prevents the scroll down when the enter key is pressed
    }

    /**
     * When the account is found, the values are displayed which are saved in the account object
     * Now, if there is an other field (not all account have this field), it will be displayed
     * and removed when not necessary
     */
    private fun setDisplay(account : Account){
        siteDisplay.text = account.Site
        usernameDisplay.text = account.Username
        emailDisplay.text = account.Email
        passwordDisplay.text = account.Password
        otherDisplay.text = account.Other.trimStart()

        otherLabel.visibility = if (account.Other.isNotEmpty()) View.VISIBLE else View.INVISIBLE

        otherScrollView.post {
            if (otherDisplay.lineCount > otherDisplay.maxLines) {

                otherScrollView.smoothScrollBy(0, 75) // Scrolls a small amount to show the user it is scrollable

                otherScrollView.postDelayed({
                    otherScrollView.smoothScrollTo(0, 0)
                }, 500) // then scroll back up after that many milli
            }
        }
    }


    /**
     * When the user clicks on the values, they are copied to the clipboard
     * https://developer.android.com/develop/ui/views/touch-and-input/copy-paste
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun displayValueOnClick(view: View) {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        when(view.id){
            usernameDisplay.id -> clipboardManager.setPrimaryClip(ClipData.newPlainText   ("", usernameDisplay.text))
            emailDisplay.id -> clipboardManager.setPrimaryClip(ClipData.newPlainText   ("", emailDisplay.text))
            passwordDisplay.id -> {
                val clipData = ClipData.newPlainText   ("", passwordDisplay.text)
                clipData.apply {
                    description.extras = PersistableBundle().apply {
                        putBoolean(ClipDescription.EXTRA_IS_SENSITIVE,true)
                        putBoolean("android.content.extra.IS_SENSITIVE", true)
                    }
                }
                clipboardManager.setPrimaryClip(clipData)
            }
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
    }

    /**
     * When the app is paused (running in the background) the app goes back to the master password page
     */
    override fun onPause() {
        super.onPause()
        startActivity(Intent(this,PasswordActivity::class.java))
    }
}