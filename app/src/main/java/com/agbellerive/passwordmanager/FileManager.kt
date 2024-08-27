package com.agbellerive.passwordmanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.util.Log
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder


class FileManager {

    fun readDocumentFromUri(uri: Uri,context: Context) : ArrayList<Account> {
        val contentResolver = context.contentResolver
        var allText = ""
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    allText += line
                }
            }
        } ?: run {
            Log.e("Error", "Failed to open document.")
        }

        val regex : Regex = """\{\s*"Site":\s*".*?",\s*"Username":\s*".*?",\s*"Email":\s*".*?",\s*"Password":\s*".*?",\s*"Others"\s*:\s*".*?"\s*\}""".toRegex()
        val matches = regex.findAll(allText)

        val allAccounts = ArrayList<Account>()

        for (match in matches) {
            allAccounts.add(Json.decodeFromString<Account>(match.value))
        }
        return allAccounts
    }


}