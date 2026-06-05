package com.example.data

import kotlinx.coroutines.flow.Flow

class SecurityRepository(private val securityDao: SecurityDao) {
    val allAuditLogs: Flow<List<AuditLog>> = securityDao.getAllAuditLogs()
    val allPairings: Flow<List<VerifiablePairing>> = securityDao.getAllPairings()

    suspend fun insertAuditLog(log: AuditLog) {
        securityDao.insertAuditLog(log)
    }

    suspend fun deleteAuditLog(id: Int) {
        securityDao.deleteAuditLogById(id)
    }

    suspend fun clearAllAuditLogs() {
        securityDao.clearAuditLogs()
    }

    suspend fun insertPairing(pairing: VerifiablePairing) {
        securityDao.insertPairing(pairing)
    }

    suspend fun deletePairing(id: Int) {
        securityDao.deletePairing(id)
    }
}
