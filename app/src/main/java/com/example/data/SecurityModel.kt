package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val targetNumber: String,
    val riskScore: Int, // 0 to 100
    val analysisResult: String, // JSON or descriptive string
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "verifiable_pairings")
data class VerifiablePairing(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String,
    val localPublicKey: String,
    val remotePublicKey: String,
    val sharedSecretHash: String,
    val isVerified: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
