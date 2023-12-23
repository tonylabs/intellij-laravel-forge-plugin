package com.tonylabs.forge.ui

import com.intellij.openapi.project.Project

import com.sun.java.accessibility.util.AWTEventMonitor.addActionListener
import java.awt.BorderLayout
import java.util.prefs.Preferences
import javax.swing.*

class TokenWindowContent(project: Project, private val onTokenSubmit: (String) -> Unit) : JPanel() {
    private val tokenTextArea: JTextArea
    private val preferences: Preferences = Preferences.userNodeForPackage(TokenWindowContent::class.java)

    companion object {
        private const val TOKEN_KEY = "forgeToken"
    }

    init {
        layout = BorderLayout()

        val label = JLabel("Enter Forge Token:")
        add(label, BorderLayout.NORTH)

        // Create a JTextArea for token input
        tokenTextArea = JTextArea(5, 10)
        tokenTextArea.wrapStyleWord = true
        tokenTextArea.lineWrap = true
        val scrollPane = JScrollPane(tokenTextArea)
        add(scrollPane, BorderLayout.CENTER)

        // Optionally, add a submit button
        val submitButton = JButton("Submit").apply {
            addActionListener {
                val token = tokenTextArea.text.trim()
                if (token.isNotEmpty()) {
                    onTokenSubmit(token)
                }
            }
        }
        add(submitButton, BorderLayout.SOUTH)
    }

    private fun getTokenFromLocalStorage(): String? {
        return preferences.get(TOKEN_KEY, null)
    }

    private fun saveTokenToPreferences(token: String) {
        preferences.put(TOKEN_KEY, token)
    }

    fun loadToken() {
        val storedToken = getTokenFromLocalStorage()
        if (storedToken != null) {
            tokenTextArea.text = storedToken
        }
    }

    fun submitToken() {
        val token = tokenTextArea.text.trim()
        if (token.isNotEmpty()) {
            saveTokenToPreferences(token)
            onTokenSubmit(token)
        }
    }
}