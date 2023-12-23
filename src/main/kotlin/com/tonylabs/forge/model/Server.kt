package com.tonylabs.forge.model

data class Server(
    val id: Int,
    val credentialId: Int,
    val name: String,
    val size: String,
    val region: String,
    val phpVersion: String,
    val phpCliVersion: String,
    val opcacheStatus: String?,
    val databaseType: String,
    val ipAddress: String,
    val privateIpAddress: String?,
    val blackfireStatus: String?,
    val papertrailStatus: String?,
    val revoked: Boolean,
    val createdAt: String,
    val isReady: Boolean,
    val network: List<Any>  // Update this based on the actual network object structure
)