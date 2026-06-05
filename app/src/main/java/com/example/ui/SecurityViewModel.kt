package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AuditLog
import com.example.data.SecurityRepository
import com.example.data.VerifiablePairing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.random.Random

class SecurityViewModel(private val repository: SecurityRepository) : ViewModel() {

    // Main States
    val auditLogs: StateFlow<List<AuditLog>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val verifiedPairings: StateFlow<List<VerifiablePairing>> = repository.allPairings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Risk Scanner UI States
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _currentScanResult = MutableStateFlow<AuditResult?>(null)
    val currentScanResult: StateFlow<AuditResult?> = _currentScanResult.asStateFlow()

    // DH Crypotgraphic Simulation UI States
    private val _simPrimeValue = MutableStateFlow(23)
    val simPrimeValue: StateFlow<Int> = _simPrimeValue.asStateFlow()

    private val _simGenValue = MutableStateFlow(5)
    val simGenValue: StateFlow<Int> = _simGenValue.asStateFlow()

    private val _alicePrivateKey = MutableStateFlow(6)
    val alicePrivateKey: StateFlow<Int> = _alicePrivateKey.asStateFlow()

    private val _bobPrivateKey = MutableStateFlow(15)
    val bobPrivateKey: StateFlow<Int> = _bobPrivateKey.asStateFlow()

    private val _dhStep = MutableStateFlow(1) // 1: Setup, 2: Calculate public keys, 3: Exchange & Derived Secret
    val dhStep: StateFlow<Int> = _dhStep.asStateFlow()

    // Checklist state
    private val _defenseCheckedItems = MutableStateFlow<Map<String, Boolean>>(
        mapOf(
            "two_factor" to false,
            "security_notifications" to false,
            "disappearing_messages" to false,
            "ic_lock" to false,
            "backup_encrypt" to false,
            "silence_unknown" to false
        )
    )
    val defenseCheckedItems: StateFlow<Map<String, Boolean>> = _defenseCheckedItems.asStateFlow()

    // 1. Risk Scan Simulation
    fun scanPhoneNumber(number: String) {
        if (number.trim().isEmpty()) return
        
        viewModelScope.launch {
            _isScanning.value = true
            _currentScanResult.value = null
            
            // Artificial delay to simulate deep analysis/lookup
            kotlinx.coroutines.delay(2000)

            val cleaned = number.replace(Regex("[^0-9+]"), "")
            
            // Deterministic generation based on number to feel consistent
            val numberSeed = try {
                cleaned.takeLast(4).toLong()
            } catch (e: Exception) {
                Random.nextLong(1000, 9999)
            }
            val rand = Random(numberSeed)

            // Dynamic factors representing real-world messaging vulnerabilities
            val hasTwoStepEnabled = rand.nextBoolean()
            val hasCloudBackupsUnencrypted = rand.nextBoolean()
            val multiDeviceSessionsActive = rand.nextInt(1, 4)
            val SIMSwapRisk = rand.nextInt(10, 85)
            val publicVulnerabilityMatches = rand.nextBoolean()

            // Construct score
            var riskScore = 20 // Baseline vulnerability
            val vulnerabilities = mutableListOf<String>()
            val adviceList = mutableListOf<String>()

            if (!hasTwoStepEnabled) {
                riskScore += 30
                vulnerabilities.add("2-Step Verification (PIN) is NOT active or cannot be confirmed on-chain.")
                adviceList.add("Enable 2-Step Verification inside Settings -> Account -> Two-step verification to protect against unauthorized SIM Swapping resets.")
            } else {
                adviceList.add("Keep your verification code and security PIN strictly confidential.")
            }

            if (hasCloudBackupsUnencrypted) {
                riskScore += 25
                vulnerabilities.add("Cloud Backups may not be cryptographically encrypted.")
                adviceList.add("Navigate to Settings -> Chats -> Chat Backup and ensure End-to-end Encrypted Backups are turned ON.")
            } else {
                adviceList.add("Ensure your password for end-to-end cloud database backups is written down offline.")
            }

            if (multiDeviceSessionsActive > 1) {
                riskScore += 15
                vulnerabilities.add("Multiple linked hardware devices active ($multiDeviceSessionsActive sessions).")
                adviceList.add("Regularly check Linked Devices inside messenger settings. Log out of all unfamiliar web/desktop browser instances.")
            }

            if (SIMSwapRisk > 60) {
                riskScore += 10
                vulnerabilities.add("Elevated carrier social engineering hazard (high SIM swap risk area).")
                adviceList.add("Contact your telecom carrier to lock your SIM card with a custom PIN code or port-protection passwords.")
            }

            if (publicVulnerabilityMatches) {
                riskScore += 10
                vulnerabilities.add("Unpatched dynamic media attachment execution vulnerability possibility.")
                adviceList.add("Always keep your instant messaging client updated to the latest Google Play Store build immediately.")
            }

            val finalScore = riskScore.coerceIn(5, 100)
            val riskLevel = when {
                finalScore < 30 -> "LOW RISK"
                finalScore < 60 -> "MODERATE RISK"
                else -> "HIGH RISK"
            }

            val result = AuditResult(
                phoneNumber = cleaned,
                riskScore = finalScore,
                riskLevel = riskLevel,
                vulnerabilities = vulnerabilities,
                mitigationAdvice = adviceList,
                hasTwoStep = hasTwoStepEnabled,
                hasEncryptedBackup = !hasCloudBackupsUnencrypted,
                linkedDevices = multiDeviceSessionsActive
            )

            _currentScanResult.value = result

            // Write results into Room db history for local auditing records
            val description = """
                Risk Score: $finalScore% ($riskLevel)
                Vulnerabilities Found: ${vulnerabilities.size}
                Linked Devs: $multiDeviceSessionsActive
            """.trimIndent()
            
            repository.insertAuditLog(
                AuditLog(
                    targetNumber = cleaned,
                    riskScore = finalScore,
                    analysisResult = description
                )
            )

            _isScanning.value = false
        }
    }

    fun deleteLog(id: Int) {
        viewModelScope.launch {
            repository.deleteAuditLog(id)
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearAllAuditLogs()
        }
    }

    // 2. Cryptographic Simulator Algorithms (Diffie-Hellman Key Exchange)
    fun setAlicePrivateKey(key: Int) {
        _alicePrivateKey.value = key.coerceIn(2, 50)
    }

    fun setBobPrivateKey(key: Int) {
        _bobPrivateKey.value = key.coerceIn(2, 50)
    }

    fun setPrimeAndGenerator(prime: Int, generator: Int) {
        _simPrimeValue.value = prime.coerceIn(7, 500)
        _simGenValue.value = generator.coerceIn(2, prime - 1)
    }

    fun nextDHStep() {
        if (_dhStep.value < 3) {
            _dhStep.value += 1
        } else {
            // Reset
            _dhStep.value = 1
        }
    }

    fun resetDH() {
        _dhStep.value = 1
    }

    // Mathematical calculations for E2EE demo
    fun calculateAlicePublicKey(): Int {
        val g = _simGenValue.value.toBigInteger()
        val a = _alicePrivateKey.value.toBigInteger()
        val p = _simPrimeValue.value.toBigInteger()
        return g.modPow(a, p).toInt()
    }

    fun calculateBobPublicKey(): Int {
        val g = _simGenValue.value.toBigInteger()
        val b = _bobPrivateKey.value.toBigInteger()
        val p = _simPrimeValue.value.toBigInteger()
        return g.modPow(b, p).toInt()
    }

    fun calculateAliceSharedSecret(): Int {
        val bobPublic = calculateBobPublicKey().toBigInteger()
        val a = _alicePrivateKey.value.toBigInteger()
        val p = _simPrimeValue.value.toBigInteger()
        return bobPublic.modPow(a, p).toInt()
    }

    fun calculateBobSharedSecret(): Int {
        val alicePublic = calculateAlicePublicKey().toBigInteger()
        val b = _bobPrivateKey.value.toBigInteger()
        val p = _simPrimeValue.value.toBigInteger()
        return alicePublic.modPow(b, p).toInt()
    }

    fun savePairing(label: String) {
        viewModelScope.launch {
            val alicePub = calculateAlicePublicKey().toString()
            val bobPub = calculateBobPublicKey().toString()
            val secret = calculateAliceSharedSecret().toString()
            
            // Hash the shared secret to generate key metrics
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(secret.toByteArray())
            val fingerprint = digest.fold("") { str, it -> str + "%02x".format(it) }.take(16).uppercase()

            repository.insertPairing(
                VerifiablePairing(
                    label = label.ifEmpty { "Cipher Lab Pair" },
                    localPublicKey = "g^a mod p = $alicePub",
                    remotePublicKey = "g^b mod p = $bobPub",
                    sharedSecretHash = "FP: " + fingerprint.chunked(4).joinToString("-"),
                    isVerified = true
                )
            )
        }
    }

    fun deletePairing(id: Int) {
        viewModelScope.launch {
            repository.deletePairing(id)
        }
    }

    // 3. Checklist interaction
    fun toggleChecklist(key: String) {
        val current = _defenseCheckedItems.value.toMutableMap()
        current[key] = !(current[key] ?: false)
        _defenseCheckedItems.value = current
    }
}

data class AuditResult(
    val phoneNumber: String,
    val riskScore: Int,
    val riskLevel: String,
    val vulnerabilities: List<String>,
    val mitigationAdvice: List<String>,
    val hasTwoStep: Boolean,
    val hasEncryptedBackup: Boolean,
    val linkedDevices: Int
)

class SecurityViewModelFactory(private val repository: SecurityRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SecurityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SecurityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
