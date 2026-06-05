package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ShieldChatApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ShieldChatApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { SecurityRepository(database.securityDao()) }
    val factory = remember { SecurityViewModelFactory(repository) }
    val viewModel: SecurityViewModel = viewModel(factory = factory)

    var currentTab by remember { mutableStateOf(0) } // 0: Scanner, 1: E2EE CrypLab, 2: Self Defense, 3: Logs

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Header
        AppHeaderSection()

        // Tab Navigation Bar
        CustomTabBar(
            selectedTab = currentTab,
            onTabSelected = { currentTab = it }
        )

        Divider(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            thickness = 1.dp
        )

        // Tab Content with elegant Crossfade animation
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentTab) {
                0 -> ScannerTabScreen(viewModel)
                1 -> CryptoLabTabScreen(viewModel)
                2 -> SelfDefenseTabScreen(viewModel)
                3 -> AuditHistoryTabScreen(viewModel)
            }
        }
    }
}

@Composable
fun AppHeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        DarkSurface
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Shield Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ShieldCyan.copy(alpha = 0.15f))
                    .border(1.5.dp, ShieldCyan, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Shield Logo",
                    tint = ShieldCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SHIELD",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "CHAT",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        color = ShieldCyan,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
                Text(
                    text = "Secure Communication Defense Suite",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Verified Status Beacon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SafetyTeal.copy(alpha = 0.12f))
                    .border(1.dp, SafetyTeal.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(SafetyTeal)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ACTIVE AUDIT",
                    fontSize = 9.sp,
                    color = SafetyTeal,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CustomTabBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf(
        TabItem("Audit Scanner", Icons.Default.Lock, "scan_tab"),
        TabItem("Crypto Lab", Icons.Default.Face, "crypto_tab"),
        TabItem("Defense Prep", Icons.Default.Star, "defense_tab"),
        TabItem("Audit Log", Icons.Default.List, "log_tab")
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = selectedTab == index
            val tintColor = if (isSelected) ShieldCyan else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            val bgAlpha = if (isSelected) 0.12f else 0.0f
            val borderAlpha = if (isSelected) 0.3f else 0.0f

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ShieldCyan.copy(alpha = bgAlpha))
                    .border(1.dp, ShieldCyan.copy(alpha = borderAlpha), RoundedCornerShape(10.dp))
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 10.dp, horizontal = 4.dp)
                    .testTag(tab.testTag),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = tintColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tab.label,
                        fontSize = 10.sp,
                        color = tintColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

data class TabItem(val label: String, val icon: ImageVector, val testTag: String)

// -----------------------------------------------------
// SCREEN 1: AUDIT SCANNER (PHONE NUMBER DIAGNOSTIC)
// -----------------------------------------------------
@Composable
fun ScannerTabScreen(viewModel: SecurityViewModel) {
    var phoneNumber by remember { mutableStateOf("") }
    val isScanning by viewModel.isScanning.collectAsState()
    val scanResult by viewModel.currentScanResult.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, ShieldCyan.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PHONE NUMBER SEGREGATION AUDITOR",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShieldCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Why? Remotely hacking a chat history just 'from a phone number' is impossible in modern encrypted setups. It is technically prevented by cryptographic locks, unless specific account vector configurations are left open. Input any number below to execute a simulated vulnerability audit.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("phone_input_field"),
                        label = { Text("Target Phone Number", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) },
                        placeholder = { Text("e.g. +1 555-019-2834") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Phone Prefix",
                                tint = ShieldCyan
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ShieldCyan,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            focusedContainerColor = DarkBackground,
                            unfocusedContainerColor = DarkBackground
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.scanPhoneNumber(phoneNumber) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("scan_trigger_button"),
                        enabled = phoneNumber.isNotBlank() && !isScanning,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ShieldCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Analyzing Security Matrix...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Audit Icon")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Run Defense Vulnerability Audit", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (isScanning) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Analyzing cellular routing vulnerabilities...",
                            fontSize = 12.sp,
                            color = ShieldCyan,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Inspecting 2FA status, dynamic ratchets and cloud backup protocols...",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        scanResult?.let { result ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (result.riskScore > 60) RiskRed.copy(alpha = 0.3f) else SafetyTeal.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "AUDIT FOR: ${result.phoneNumber}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Simulated Account Profile Diagnostics",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }

                            // Dynamic Risk Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (result.riskScore > 60) RiskRed.copy(alpha = 0.15f)
                                        else SafetyTeal.copy(alpha = 0.15f)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (result.riskScore > 60) RiskRed else SafetyTeal,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = result.riskLevel,
                                    color = if (result.riskScore > 60) RiskRed else SafetyTeal,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Score Visualizer Row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Account Vulnerability Score: ${result.riskScore}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = result.riskScore / 100f,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (result.riskScore > 60) RiskRed else WarningOrange,
                                    trackColor = MaterialTheme.colorScheme.background
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Vulnerabilities breakdown list
                        Text(
                            text = "IDENTIFIED RISK ACCESSIBILITY VECTORS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (result.riskScore > 60) RiskRed else WarningOrange,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (result.vulnerabilities.isEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Safe",
                                    tint = SafetyTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "No open vulnerabilities detected. Outstanding baseline protection.",
                                    fontSize = 11.sp,
                                    color = SafetyTeal
                                )
                            }
                        } else {
                            result.vulnerabilities.forEach { v ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Vulnerability detected",
                                        tint = if (result.riskScore > 60) RiskRed else WarningOrange,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = v,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Defense Countermeasures
                        Text(
                            text = "SECURITY COUNTERMEASURES & MITIGATIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShieldCyan,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        result.mitigationAdvice.forEach { advice ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Mitigation",
                                    tint = SafetyTeal,
                                    modifier = Modifier
                                        .size(15.dp)
                                        .padding(top = 1.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = advice,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------
// SCREEN 2: INTERACTIVE CRYPTO LAB (E2EE DEMO)
// -----------------------------------------------------
@Composable
fun CryptoLabTabScreen(viewModel: SecurityViewModel) {
    val p by viewModel.simPrimeValue.collectAsState()
    val g by viewModel.simGenValue.collectAsState()
    val a by viewModel.alicePrivateKey.collectAsState()
    val b by viewModel.bobPrivateKey.collectAsState()
    val step by viewModel.dhStep.collectAsState()

    var customPairLabel by remember { mutableStateOf("") }

    val alicePub = viewModel.calculateAlicePublicKey()
    val bobPub = viewModel.calculateBobPublicKey()
    val aliceSecret = viewModel.calculateAliceSharedSecret()
    val bobSecret = viewModel.calculateBobSharedSecret()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, ShieldCyan.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CRYPTOGRAPHIC KEY EXCHANGE LAB",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShieldCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "This lab visualizes Diffie-Hellman (used in Signal Protocol E2EE). It proves that Alice and Bob can generate a secure shared secret over an insecure channel, preventing hackers from snooping, even if they monitor the cellular line.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Prime & Generator Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "SHARED ARGUMENTS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShieldCyan
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Prime Modulus p = $p", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Base Generator g = $g", fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Button(
                                onClick = { viewModel.setPrimeAndGenerator(23, 5) },
                                contentPadding = PaddingValues(2.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(28.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background)
                            ) {
                                Text("Set [23, 5]", fontSize = 9.sp, color = ShieldCyan)
                            }
                            Button(
                                onClick = { viewModel.setPrimeAndGenerator(97, 5) },
                                contentPadding = PaddingValues(2.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(28.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background)
                            ) {
                                Text("Set [97, 5]", fontSize = 9.sp, color = ShieldCyan)
                            }
                        }
                    }
                }

                // Private Keys Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "SECRET PRIVATE KEYS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningOrange
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Alice's key (a): $a", fontSize = 11.sp, modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = { viewModel.setAlicePrivateKey(a - 1) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Text("-", color = WarningOrange, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                            IconButton(
                                onClick = { viewModel.setAlicePrivateKey(a + 1) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Text("+", color = WarningOrange, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Bob's key (b): $b", fontSize = 11.sp, modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = { viewModel.setBobPrivateKey(b - 1) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Text("-", color = WarningOrange, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                            IconButton(
                                onClick = { viewModel.setBobPrivateKey(b + 1) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Text("+", color = WarningOrange, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, ShieldCyan.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "STEP $step RESULT ENGINES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShieldCyan
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    when (step) {
                        1 -> {
                            Text(
                                text = "Setup Stage: Base Parameters Selected",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "The values (p = $p and g = $g) are shared publicly. Alice keeps her private 'a = $a' locked in secure device hardware, and Bob keeps 'b = $b' similarly isolated.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                        2 -> {
                            Text(
                                text = "Calculation Stage: Public Key Derivation",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(DarkBackground)
                                        .border(1.dp, ShieldCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text("ALICE PUBLIC KEY (A)", fontSize = 9.sp, color = ShieldCyan, fontWeight = FontWeight.Bold)
                                        Text("A = g^a mod p", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                        Text("$g^$a mod $p = $alicePub", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(DarkBackground)
                                        .border(1.dp, ShieldCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text("BOB PUBLIC KEY (B)", fontSize = 9.sp, color = ShieldCyan, fontWeight = FontWeight.Bold)
                                        Text("B = g^b mod p", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                        Text("$g^$b mod $p = $bobPub", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Alice transmits her key ($alicePub) and Bob transmits his key ($bobPub) openly. Even if intercepted by an active number eavesdropper, finding private keys remains computationally infeasible in full scale.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        3 -> {
                            Text(
                                text = "Exchange Stage: Derived Common Secret",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(DarkBackground)
                                        .border(1.dp, SafetyTeal.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text("ALICE COMPUTES SECRET", fontSize = 9.sp, color = SafetyTeal, fontWeight = FontWeight.Bold)
                                        Text("s = B^a mod p", fontSize = 10.sp, color = Color.LightGray)
                                        Text("$bobPub^$a mod $p = $aliceSecret", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SafetyTeal)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(DarkBackground)
                                        .border(1.dp, SafetyTeal.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text("BOB COMPUTES SECRET", fontSize = 9.sp, color = SafetyTeal, fontWeight = FontWeight.Bold)
                                        Text("s = A^b mod p", fontSize = 10.sp, color = Color.LightGray)
                                        Text("$alicePub^$b mod $p = $bobSecret", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SafetyTeal)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Both parties reached the EXACT SAME key ($aliceSecret) without ever sharing it! This proves nobody else on the line can deduce this key without having the private exponents.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.nextDHStep() },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("step_dh_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ShieldCyan, contentColor = Color.Black)
                        ) {
                            Text(
                                text = if (step == 3) "Restart Demonstration" else "Calculate Next Step",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        if (step == 3) {
                            Button(
                                onClick = {
                                    viewModel.savePairing(customPairLabel)
                                    customPairLabel = ""
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("save_identity_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = SafetyTeal, contentColor = Color.White)
                            ) {
                                Text("Verify & Save Identity", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    if (step == 3) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = customPairLabel,
                            onValueChange = { customPairLabel = it },
                            placeholder = { Text("Identity Label, e.g. Phone Link A") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ShieldCyan,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                focusedContainerColor = DarkBackground,
                                unfocusedContainerColor = DarkBackground
                            )
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------
// SCREEN 3: DEFENSE SELF-AUDITING PREPARATION
// -----------------------------------------------------
@Composable
fun SelfDefenseTabScreen(viewModel: SecurityViewModel) {
    val itemsChecked by viewModel.defenseCheckedItems.collectAsState()

    val checklistDetails = listOf(
        ChecklistSetup(
            key = "two_factor",
            title = "Two-Step Authentication Pin",
            description = "Crucial! Prevents malicious SIM swaps or port-outs from hijackers registers.",
            stepText = "Go to Settings -> Account -> Two-Step Verification -> Enable and register a highly robust 6-digit PIN."
        ),
        ChecklistSetup(
            key = "security_notifications",
            title = "Security & Identity Notifications",
            description = "Triggers immediate device alerts if your contact's end-to-end cryptographic secret key changes.",
            stepText = "Go to Settings -> Account -> Security Notifications -> Turn on the notification slider."
        ),
        ChecklistSetup(
            key = "disappearing_messages",
            title = "Disappearing Messages Strategy",
            description = "Ensures chats auto-dissolve after a custom timeframe to limit exposure if physics theft occurs.",
            stepText = "Go to Settings -> Privacy -> Default Message Timer -> Choose a window (e.g. 24 Hours or 7 Days)."
        ),
        ChecklistSetup(
            key = "ic_lock",
            title = "Messenger App Screen Lock",
            description = "Blocks unauthorized access if your unlocked Android device is compromised or physically nabbed.",
            stepText = "Go to Settings -> Privacy -> App Lock -> Enable and select Biometrics or standard device credentials."
        ),
        ChecklistSetup(
            key = "backup_encrypt",
            title = "End-to-End Encrypted Backups",
            description = "Prevents global network cloud companies or sub-carriers from viewing chat files stored remotely.",
            stepText = "Go to Settings -> Chats -> Chat Backup -> End-to-end Encrypted Backups -> Enable with offline password."
        ),
        ChecklistSetup(
            key = "silence_unknown",
            title = "Silence Telephony Spammer Attacks",
            description = "Mitigates zero-click automated malware vulnerabilities executing over dial-in protocols.",
            stepText = "Go to Settings -> Privacy -> Calls -> Enable \"Silence Unknown Callers\" slider toggle."
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, ShieldCyan.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "YOUR PHONE'S DEFENSE CONFIGURATOR",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShieldCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Implement these critical defense changes within your actual messaging application to permanently isolate your private parameters and fully shut down remote exploit hazards.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        items(checklistDetails) { item ->
            val checked = itemsChecked[item.key] ?: false
            Card(
                colors = CardDefaults.cardColors(containerColor = if (checked) ShieldCyan.copy(alpha = 0.05f) else DarkSurface),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (checked) SafetyTeal.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                ),
                onClick = { viewModel.toggleChecklist(item.key) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checklist_item_${item.key}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { viewModel.toggleChecklist(item.key) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = SafetyTeal,
                            uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            checkmarkColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = item.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (checked) SafetyTeal else Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkBackground.copy(alpha = 0.5f))
                                .border(0.5.dp, ShieldCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Countermeasures setup steps: ${item.stepText}",
                                fontSize = 10.sp,
                                color = ShieldCyan.copy(alpha = 0.9f),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

data class ChecklistSetup(val key: String, val title: String, val description: String, val stepText: String)

// -----------------------------------------------------
// SCREEN 4: AUDIT HISTORY RECORDS (ROOM DATA OBSERVATION)
// -----------------------------------------------------
@Composable
fun AuditHistoryTabScreen(viewModel: SecurityViewModel) {
    val logs by viewModel.auditLogs.collectAsState()
    val pairings by viewModel.verifiedPairings.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pairings Section
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Keys",
                    tint = SafetyTeal,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ESTABLISHED TRUST IDENTITIES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SafetyTeal,
                    letterSpacing = 1.sp
                )
            }
        }

        if (pairings.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "No identity fingerprint verified",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Zero verified key pairings preserved.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Run calculations in the Crypto Lab to verify a pairing.",
                                fontSize = 10.sp,
                                color = ShieldCyan.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        } else {
            items(pairings) { pair ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, SafetyTeal.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, "verified", tint = SafetyTeal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = pair.label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "delete pairing",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { viewModel.deletePairing(pair.id) }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Fingerprint: ${pair.sharedSecretHash}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SafetyTeal,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Local DH component: ${pair.localPublicKey}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Remote DH component: ${pair.remotePublicKey}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // Logs Section
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Logs",
                    tint = ShieldCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TARGET LOG HISTORICAL AUDITS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShieldCyan,
                    letterSpacing = 1.sp,
                    modifier = Modifier.weight(1f)
                )
                if (logs.isNotEmpty()) {
                    Text(
                        text = "WIPE HISTORY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RiskRed,
                        modifier = Modifier.clickable { viewModel.clearAllLogs() }
                    )
                }
            }
        }

        if (logs.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "No records",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Zero telephone audits stored in local database.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        } else {
            items(logs) { log ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, ShieldCyan.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Target: ${log.targetNumber}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "delete log",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { viewModel.deleteLog(log.id) }
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = log.analysisResult,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        java.text.DateFormat.getDateTimeInstance().format(java.util.Date(log.timestamp))?.let { dateStr ->
                            Text(
                                text = "Audit recorded: $dateStr",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4e-1f.plus(0.40f)),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
