package com.agbellerive.passwordmanager

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader


class FileManager {

    /**
     * This function is the core of reading and parsing the password file
     */
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

        val regex : Regex = """\{\s*"Site":\s*".*?",\s*"Username":\s*".*?",\s*"Email":\s*".*?",\s*"Password":\s*".*?",\s*"Other":\s*".*?"\s*\}""".toRegex()

        val matches = regex.findAll(allText.trimIndent())
        val allAccounts = ArrayList<Account>()

        for (match in matches) {
            allAccounts.add(Json.decodeFromString<Account>(match.value))
        }
        return allAccounts
    }
}