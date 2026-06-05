package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog)

    @Query("DELETE FROM audit_logs WHERE id = :id")
    suspend fun deleteAuditLogById(id: Int)

    @Query("DELETE FROM audit_logs")
    suspend fun clearAuditLogs()

    @Query("SELECT * FROM verifiable_pairings ORDER BY timestamp DESC")
    fun getAllPairings(): Flow<List<VerifiablePairing>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPairing(pairing: VerifiablePairing)

    @Query("DELETE FROM verifiable_pairings WHERE id = :id")
    suspend fun deletePairing(id: Int)
}
