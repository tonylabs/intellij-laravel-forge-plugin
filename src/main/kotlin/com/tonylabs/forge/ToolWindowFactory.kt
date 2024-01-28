package com.tonylabs.forge

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel

import com.tonylabs.forge.model.Server

import okhttp3.OkHttpClient
import okhttp3.Request
import java.awt.BorderLayout
import java.util.prefs.Preferences
import java.util.concurrent.Executors

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.JsonParser

class ToolWindowFactory : ToolWindowFactory {

    private val httpClient = OkHttpClient()
    private val executorService = Executors.newSingleThreadExecutor()

    companion object {
        private const val TOKEN_KEY = "forgeToken"
        private val preferences: Preferences = Preferences.userNodeForPackage(ToolWindowFactory::class.java)
    }

    init {
        thisLogger().warn("TokenWindowFactory initialized.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()
        val token = getTokenFromLocalStorage()
        val onTokenSubmit: (String) -> Unit = { submittedToken ->
            saveTokenToLocalStorage(submittedToken)
            fetchServers(submittedToken) { servers ->
                val serverListContent = ServerListContent(project, servers)
                val content = contentFactory.createContent(serverListContent, "", false)
                toolWindow.contentManager.removeAllContents(true)
                toolWindow.contentManager.addContent(content)
            }
        }

        if (token.isNullOrEmpty()) {
            val tokenContent = contentFactory.createContent(ToolWindowContent(project, onTokenSubmit), "", false)
            toolWindow.contentManager.addContent(tokenContent)
        } else {
            // If token is available, fetch servers and show server list
            thisLogger().warn("TokenWindowFactory: Token is available - $token")
            fetchServers(token.toString()) { servers ->
                if (servers.isNotEmpty()) {
                    val serverListContent = ServerListContent(project, servers)
                    val content = contentFactory.createContent(serverListContent, "", false)
                    toolWindow.contentManager.removeAllContents(true)
                    toolWindow.contentManager.addContent(content)
                } else {
                    val messageContent = contentFactory.createContent(JBPanel<JBPanel<*>>(BorderLayout()).apply {
                        add(JBLabel("No servers found."), BorderLayout.CENTER)
                    }, "", false)
                    toolWindow.contentManager.removeAllContents(true)
                    toolWindow.contentManager.addContent(messageContent)
                }
            }
        }
    }

    private fun getTokenFromLocalStorage(): String? {
        return preferences.get(TOKEN_KEY, null)
    }

    private fun saveTokenToLocalStorage(token: String) {
        preferences.put(TOKEN_KEY, token)
    }

    private fun fetchServers(token: String, callback: (List<Server>) -> Unit) {
        executorService.submit {
            try {
                val request = Request.Builder()
                    .url("https://forge.laravel.com/api/v1/servers")
                    .header("Authorization", "Bearer $token")
                    .build()
                val response = httpClient.newCall(request).execute()
                thisLogger().warn("fetchServers Response - $response")
                thisLogger().warn("fetchServers Response Body - ${response.body?.string()}")
                val servers = parseServerList(response.body?.string() ?: "")
                callback(servers)
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    private fun parseServerList(json: String): List<Server> {
        val jsonObject = JsonParser.parseString(json).asJsonObject
        val serversJson = jsonObject.getAsJsonArray("servers")
        val gson = Gson()
        val type = object : TypeToken<List<Server>>() {}.type
        return gson.fromJson(serversJson, type)
    }
}