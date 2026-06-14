package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: TournamentViewModel) {
    val context = LocalContext.current
    var mainTabSelected by remember { mutableStateOf(0) } // 0 = Dashboard, 1 = Live Simulator, 2 = Guide & Formulas

    val athletes by viewModel.allAthletes.collectAsState()
    val teams by viewModel.allTeams.collectAsState()
    val matches by viewModel.allMatches.collectAsState()
    val currentSettings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Volei",
                            tint = SportGold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column {
                            Text(
                                "Superliga Voleibol de Arena",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = TextCrispWhite
                            )
                            Text(
                                "Gestión Centralizada y Plantillas",
                                fontSize = 12.sp,
                                color = TextMutedGray
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SportDarkSurface
                )
            )
        },
        containerColor = SportDarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Top Navigation Row
            TabRow(
                selectedTabIndex = mainTabSelected,
                containerColor = SportDarkSurface,
                contentColor = SportGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[mainTabSelected]),
                        color = SportGold
                    )
                }
            ) {
                Tab(
                    selected = (mainTabSelected == 0),
                    onClick = { mainTabSelected = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Send, contentDescription = "Dashboard", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("DASHBOARD", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                )
                Tab(
                    selected = (mainTabSelected == 1),
                    onClick = { mainTabSelected = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, contentDescription = "Simulador", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("SIMULADOR EN VIVO", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                )
                Tab(
                    selected = (mainTabSelected == 2),
                    onClick = { mainTabSelected = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = "Guía", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("GUÍA & FÓRMULAS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                )
            }

            AnimatedContent(
                targetState = mainTabSelected,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "main_tabs"
            ) { targetTab ->
                when (targetTab) {
                    0 -> MainConsolidatedDashboard(
                        athletes = athletes,
                        teams = teams,
                        matches = matches,
                        settings = currentSettings
                    )
                    1 -> SimulatorTabContent(
                        viewModel = viewModel,
                        athletes = athletes,
                        teams = teams,
                        matches = matches,
                        settings = currentSettings,
                        context = context
                    )
                    2 -> GuideTabContent(context)
                }
            }
        }
    }
}

// ------------------------------------
// GUIDE & FORMULAS COMPONENT VIEW
// ------------------------------------
@Composable
fun GuideTabContent(context: Context) {
    var subTabSelected by remember { mutableStateOf(0) }
    val sections = CodeAssets.sheetStructureAndFormulas

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Configuration Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.List, contentDescription = "Form", tint = SportOrange)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "1. Puesta en Marcha: Google Forms",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = SportGold
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = CodeAssets.googleFormsGuideMarkdown,
                        color = TextCrispWhite,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Formula Selector Tabs Header
        item {
            Text(
                "2. Fórmulas de Sheets Listas para Copiar",
                color = SportGold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                "Toca una pestaña para ver los encabezados recomendados de la fila 1 y las fórmulas dinámicas automatizadas.",
                color = TextMutedGray,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Horizontal Category Selector buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sections.forEachIndexed { index, sec ->
                    val isSel = subTabSelected == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) SportGold else SportDarkSurface)
                            .clickable { subTabSelected = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Short name helper
                        val label = sec.tabName.split(". ").lastOrNull() ?: sec.tabName
                        Text(
                            text = label,
                            color = if (isSel) SportDarkBg else TextCrispWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Active Sheet Tab Information
        val activeSection = sections.getOrNull(subTabSelected)
        if (activeSection != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, SportGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "📖 Estructura de: ${activeSection.tabName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SportGold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Escribe exactamente esto en la Fila 1 de la pestaña:",
                            color = TextMutedGray,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SportDarkBg, RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = activeSection.row1Headers,
                                modifier = Modifier.weight(1f),
                                color = TextCrispWhite,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            IconButton(onClick = {
                                copyToClipboard(context, activeSection.row1Headers)
                                Toast.makeText(context, "Encabezados copiados", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Copy",
                                    tint = SportGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Formulas list
            items(activeSection.formulas) { formula ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = formula.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = SportOrange
                        )
                        Text(
                            text = formula.desc,
                            fontSize = 12.sp,
                            color = TextMutedGray,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(Modifier.height(8.dp))

                        // Formulas copy actions
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            FormulaBox(
                                label = "Versión Español (Guiones / Punto y Coma)",
                                code = formula.spanish,
                                context = context
                            )
                            FormulaBox(
                                label = "Versión Inglés (Comas)",
                                code = formula.english,
                                context = context
                            )
                        }
                    }
                }
            }
        }

        // Google Apps Script Section
        item {
            var appsScriptTypeSelection by remember { mutableStateOf(0) } // 0 = Round-Robin Estándar, 1 = Planificador Doble Cancha (40 Juegos)
            
            val selectedScriptCode = if (appsScriptTypeSelection == 0) {
                CodeAssets.googleAppsScriptSource
            } else {
                CodeAssets.googleAppsScriptDoubleCourtSource
            }
            
            val selectedScriptDesc = if (appsScriptTypeSelection == 0) {
                "Pega esto en la sección de Extensiones -> Apps Script de tu libro para habilitar la generación de Fixture rotativo por Jornadas con un clic de botón."
            } else {
                "Pega esto en la sección de Extensiones -> Apps Script para generar automáticamente una plantilla estructurada de 40 partidos semanales/diarios distribuidos equitativamente (cerca de 20 por cancha), con menús desplegables de duplas confirmadas, categorías y un panel automático lateral de métricas claras financieras de arbitraje."
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Settings, contentDescription = "Apps Script", tint = SportBlue)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "3. Código Google Apps Script",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = SportGold
                            )
                        }
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = SportBlue),
                            shape = RoundedCornerShape(6.dp),
                            onClick = {
                                copyToClipboard(context, selectedScriptCode)
                                Toast.makeText(context, "Código Apps Script copiado con éxito", Toast.LENGTH_LONG).show()
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Copiar Todo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    // Toggle selector for script type
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (appsScriptTypeSelection == 0) SportGold else SportDarkBg)
                                .clickable { appsScriptTypeSelection = 0 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Round-Robin Estándar",
                                color = if (appsScriptTypeSelection == 0) SportDarkBg else TextCrispWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (appsScriptTypeSelection == 1) SportOrange else SportDarkBg)
                                .clickable { appsScriptTypeSelection = 1 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Doble Cancha (40 Juegos) 🆕",
                                color = if (appsScriptTypeSelection == 1) SportDarkBg else TextCrispWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = selectedScriptDesc,
                        color = TextMutedGray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(SportDarkBg, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Text(
                                    text = selectedScriptCode,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = TextCrispWhite
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormulaBox(label: String, code: String, context: Context) {
    Column {
        Text(label, fontSize = 11.sp, color = TextMutedGray, fontWeight = FontWeight.Bold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(SportDarkBg.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                .border(0.5.dp, TextMutedGray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                .padding(8.dp)
        ) {
            Text(
                text = code,
                modifier = Modifier.weight(1f),
                color = SportGoldLight,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            IconButton(
                onClick = {
                    copyToClipboard(context, code)
                    Toast.makeText(context, "Copiado: $code", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = TextMutedGray,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}


// ------------------------------------
// SIMULATOR INTERACTIVE ROOT PANEL
// ------------------------------------
@Composable
fun SimulatorTabContent(
    viewModel: TournamentViewModel,
    athletes: List<Athlete>,
    teams: List<Team>,
    matches: List<ScheduledMatch>,
    settings: TournamentSettings,
    context: Context
) {
    var simTab by remember { mutableStateOf(0) } // 0=Inscripciones, 1=Equipos, 2=Fixture, 3=Arbitraje, 4=Presupuesto, 5=Categorías

    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Scrollable Tab List for simulator sheets
        ScrollableTabRow(
            selectedTabIndex = simTab,
            containerColor = SportDarkBg,
            contentColor = SportGold,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[simTab]),
                    color = SportGold
                )
            }
        ) {
            val tabs = listOf("1. Inscripciones", "2. Control Equipos", "3. Fixture & Clasif", "4. Arbitraje Diario", "5. Presupuesto", "6. Categorías")
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = (simTab == idx),
                    onClick = { simTab = idx },
                    text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }
        }

        // Render current selected Simulator tab
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (simTab) {
                0 -> SimInscripciones(viewModel, athletes, settings)
                1 -> SimControlEquipos(viewModel, teams)
                2 -> SimFixture(viewModel, teams, matches)
                3 -> SimArbitraje(viewModel, matches)
                4 -> SimPresupuesto(viewModel, settings, matches)
                5 -> SimCategorias(viewModel)
            }
        }
    }
}

// ------------------------------------
// SIM TABS IMPLEMENTATIONS
// ------------------------------------

@Composable
fun SimInscripciones(
    viewModel: TournamentViewModel,
    athletes: List<Athlete>,
    settings: TournamentSettings
) {
    val context = LocalContext.current
    val categoriesFull by viewModel.allCategories.collectAsState()
    val categoriesStrList = remember(categoriesFull) {
        categoriesFull.map { "${it.gender} - ${it.name}" }
    }
    val filterOptions = remember(categoriesStrList) {
        listOf("Todos") + categoriesStrList
    }
    var filterCategory by remember { mutableStateOf("Todos") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingAthlete by remember { mutableStateOf<Athlete?>(null) }

    // Form and Sheets Sync States
    var formUrlInput by remember(settings.googleSheetsCsvUrl) {
        mutableStateOf(settings.googleSheetsCsvUrl.ifBlank { "https://forms.gle/SuperligaArenaReg" })
    }
    var sheetCsvInput by remember(settings.googleSheetsCsvUrl) {
        mutableStateOf(settings.googleSheetsCsvUrl)
    }
    var isSyncing by remember { mutableStateOf(false) }
    var syncMessage by remember { mutableStateOf<String?>(null) }
    var syncSuccessCount by remember { mutableStateOf<Int?>(null) }

    // Live Submission Animation Notification State
    var latestSubmittedName by remember { mutableStateOf("") }
    var showNotification by remember { mutableStateOf(false) }

    // Calculated metrics
    val validRegistrationFee = 50.0 // assume $50 per athlete registration
    val totalRevenue = athletes.size * validRegistrationFee
    val premiacionAmount = totalRevenue * (settings.premiacionPerc / 100.0)
    val logisticaAmount = totalRevenue * (settings.logisticaPerc / 100.0)

    val filteredList = if (filterCategory == "Todos") athletes else athletes.filter { it.category == filterCategory }

    // Coroutine scope for auto-expiring notification
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {

        // Dynamic Live Submission Notification Banner!
        AnimatedVisibility(
            visible = showNotification,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SportOrange),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = SportDarkBg, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "📲 ¡Formulario Recibido en Tiempo Real!",
                            fontWeight = FontWeight.Bold,
                            color = SportDarkBg,
                            fontSize = 13.sp
                        )
                        Text(
                            "Atleta: $latestSubmittedName se inscribió correctamente y se cargó en la base de datos.",
                            color = SportDarkBg.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = { showNotification = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = SportDarkBg)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Stats Panel Item
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SportGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("AUTOMATIZACIÓN EN TIEMPO REAL: RECAUDACIÓN", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SportGold)
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Monto Total Recaudado", fontSize = 12.sp, color = TextMutedGray)
                                Text("$${String.format("%.2f", totalRevenue)} USD", fontSize = 20.sp, fontWeight = FontWeight.Black, color = SportGold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Total Atletas", fontSize = 12.sp, color = TextMutedGray)
                                Text("${athletes.size} Inscritos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextCrispWhite)
                            }
                        }
                        Divider(color = TextMutedGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 10.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("💵 Premiación (${settings.premiacionPerc.toInt()}%)", fontSize = 11.sp, color = TextMutedGray)
                                Text("$${String.format("%.2f", premiacionAmount)} USD", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SportOrange)
                            }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                Text("⚙ Logística (${settings.logisticaPerc.toInt()}%)", fontSize = 11.sp, color = TextMutedGray)
                                Text("$${String.format("%.2f", logisticaAmount)} USD", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SportBlue)
                            }
                        }
                    }
                }
            }

            // Section 1: Send Registration Form to Athletes Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = SportOrange, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "📩 Campaña por Mensajería (Atletas)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = SportGold
                            )
                        }
                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Configura el enlace de tu Google Form o portal de registro, copia el mensaje e inscríbelos mediante WhatsApp o Telegram.",
                            color = TextMutedGray,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = formUrlInput,
                            onValueChange = {
                                formUrlInput = it
                                viewModel.saveCsvUrl(it) // local config save
                            },
                            label = { Text("Enlace para tu Formulario de Inscripción", fontSize = 11.sp) },
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextCrispWhite),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SportGold,
                                unfocusedBorderColor = TextMutedGray.copy(alpha = 0.3f),
                                focusedLabelColor = SportGold,
                                unfocusedLabelColor = TextMutedGray
                            )
                        )
                        Spacer(Modifier.height(12.dp))

                        val shareMessage = "🏐 *¡INSCRIPCIONES ABIERTAS: SUPERLIGA DE VOLEIBOL DE ARENA!* 🏆\n\nHola deportista. Ya puedes registrarte para participar en la nueva jornada del torneo de voleibol de arena.\n\nPor favor, ingresa en el siguiente formulario para llenar tus datos y adjuntar tu comprobante de pago:\n\n👉 $formUrlInput\n\n¡Te esperamos en la arena! 💪🔥"

                        Text(
                            "Mensaje Copiable para WhatsApp / Mensajería:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMutedGray
                        )
                        Spacer(Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SportDarkBg, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = shareMessage,
                                color = TextCrispWhite,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.SansSerif,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = SportOrange),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                onClick = {
                                    copyToClipboard(context, shareMessage)
                                    Toast.makeText(context, "📱 ¡Mensaje copiado! Listo para enviar por WhatsApp", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Copiar Mensaje", fontSize = 12.sp)
                            }

                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                onClick = {
                                    try {
                                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(android.content.Intent.EXTRA_TEXT, shareMessage)
                                        }
                                        context.startActivity(android.content.Intent.createChooser(intent, "Compartir con atletas de volei"))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error al compartir: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Compartir Enlace", fontSize = 12.sp, color = SportDarkBg)
                            }
                        }
                    }
                }
            }

            // Section 2: Real Google Sheets Live CSV Synchronizer Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = SportBlue, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "🔄 Sincronizador Automático de Sheets",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = SportGold
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Carga automáticamente todos los envíos de tus atletas insertando la URL CSV pública de tu Google Sheet:",
                            color = TextMutedGray,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = sheetCsvInput,
                            onValueChange = {
                                sheetCsvInput = it
                                viewModel.saveCsvUrl(it) // local save inside settings
                            },
                            placeholder = { Text("https://docs.google.com/spreadsheets/d/.../pub?output=csv", fontSize = 11.sp, color = TextMutedGray) },
                            label = { Text("URL de Google Sheets Publicada como CSV", fontSize = 11.sp) },
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextCrispWhite),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SportBlue,
                                unfocusedBorderColor = TextMutedGray.copy(alpha = 0.3f),
                                focusedLabelColor = SportBlue,
                                unfocusedLabelColor = TextMutedGray
                            )
                        )
                        Spacer(Modifier.height(12.dp))

                        // Trigger sync button containing progress states
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                            if (isSyncing) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(vertical = 12.dp)
                                ) {
                                    CircularProgressIndicator(color = SportBlue, modifier = Modifier.size(24.dp))
                                    Text("Estableciendo conexión y descargando registros...", color = SportBlue, fontSize = 13.sp)
                                }
                            } else {
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = SportBlue),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    onClick = {
                                        if (sheetCsvInput.isBlank()) {
                                            Toast.makeText(context, "Ingresa primero un enlace CSV publicado de Google Sheets", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        isSyncing = true
                                        syncMessage = null
                                        syncSuccessCount = null
                                        viewModel.syncFromSheets(sheetCsvInput) { count, err ->
                                            isSyncing = false
                                            if (err != null) {
                                                syncMessage = err
                                                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                                            } else {
                                                syncSuccessCount = count
                                                Toast.makeText(context, "¡Sincronización exitosa! $count registros cargados.", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Sincronizar Respuestas de Sheets", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Feedbacks of syncing
                        syncSuccessCount?.let { count ->
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "✔ Sincronización realizada correctamente. $count registros cargados en tu app.",
                                color = Color.Green.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        syncMessage?.let { err ->
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "❌ $err",
                                color = SportOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Simple expandable guide
                        var showGuideSteps by remember { mutableStateOf(false) }
                        Text(
                            "¿Cómo obtener esta URL en Google Sheets? " + if (showGuideSteps) "▲" else "▼",
                            color = SportGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { showGuideSteps = !showGuideSteps }
                                .padding(vertical = 4.dp)
                        )
                        if (showGuideSteps) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "1. En tu Google Sheets de respuestas, ve a **Archivo -> Compartir -> Publicar en la Web**.\n" +
                                "2. Elige la pestaña **'Inscripciones'** (o respuestas de formulario 1).\n" +
                                "3. Cambia el formato de 'Página web' a **'Valores separados por comas (.csv)'**.\n" +
                                "4. Haz clic en **Publicar** y copia el enlace generado arriba.",
                                color = TextMutedGray,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Section 3: Simulated Live Background Submission (For local tests!)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, SportGold.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Simulate", tint = SportGold, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "🧪 Probar Sincronización (Simulado)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = SportGold
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Haz clic abajo para simular que un atleta acaba de enviar el formulario en vivo desde su celular y observa la carga instantánea en tu app:",
                            color = TextMutedGray,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(12.dp))

                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = SportGold.copy(alpha = 0.15f)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                val randomNames = listOf("Matias", "Valentina", "Gabriel", "Lucía", "Alejandro", "Camila", "Fernando", "Sofía", "Eduardo", "Isabella")
                                val randomSurnames = listOf("Méndez", "Ortega", "Castillo", "Vargas", "Sánchez", "Rojas", "Díaz", "Romero", "Martínez", "Pérez")
                                val randomClubs = listOf("Club Playa Arena", "Titanes Voley", "Playa Libre", "Súper Dupla", "Voley Pro")

                                val fName = randomNames.random()
                                val lName = randomSurnames.random()
                                val cat = if (fName.endsWith("a")) "Femenino" else if (Math.random() < 0.3) "Mixto" else "Masculino"
                                val age = (18..38).random()
                                val ref = "REF" + (100000..999999).random()
                                val clb = randomClubs.random()
                                val phone = "+58414" + (1000000..9999999).random()

                                viewModel.addAthlete(
                                    name = fName,
                                    surname = lName,
                                    category = cat,
                                    age = age,
                                    payProof = "https://drive.google.com/file/mock_proof",
                                    payRef = ref,
                                    club = clb,
                                    phone = phone
                                )

                                // Show successful entry banner!
                                latestSubmittedName = "$fName $lName"
                                showNotification = true

                                // Auto close banner after 5.5 seconds
                                coroutineScope.launch {
                                    delay(5500)
                                    showNotification = false
                                }
                            }
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = SportGold, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Simular Envío de Atleta en Vivo", color = SportGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Headings of registered athletes list
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Inscritos en la Base de Datos (${athletes.size})", color = TextCrispWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                            shape = RoundedCornerShape(6.dp),
                            onClick = { showAddDialog = true },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = SportDarkBg)
                            Spacer(Modifier.width(4.dp))
                            Text("Inscribir", fontSize = 11.sp, color = SportDarkBg, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = { viewModel.resetAthletes() }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Vaciar", tint = Color.Red.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            // Filtering chips item
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterOptions.forEach { cat ->
                        val active = filterCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (active) SportGold else SportDarkSurface)
                                .clickable { filterCategory = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (active) SportDarkBg else TextCrispWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // List of athlete cards
            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay atletas inscritos en esta categoría.", color = TextMutedGray, fontSize = 13.sp)
                    }
                }
            } else {
                items(filteredList) { athlete ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { editingAthlete = athlete }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${athlete.name} ${athlete.surname}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextCrispWhite
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .background(SportOrange.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(athlete.category, color = SportOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text("Club: ${athlete.club}", color = TextMutedGray, fontSize = 11.sp)
                                }
                                Spacer(Modifier.height(4.dp))
                                Text("Ref Pago: ${athlete.paymentRef} | Telf: ${athlete.phone}", fontSize = 11.sp, color = TextMutedGray)
                            }

                            IconButton(
                                onClick = { editingAthlete = athlete },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar Atleta", tint = SportBlue, modifier = Modifier.size(18.dp))
                            }

                            IconButton(
                                onClick = { viewModel.deleteAthlete(athlete) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Athlete Mock Dialog
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var surname by remember { mutableStateOf("") }
        val initialCategory = remember(categoriesStrList) { categoriesStrList.firstOrNull() ?: "Femenino - Novatas" }
        var category by remember { mutableStateOf(initialCategory) }
        var categoryDropdownOpen by remember { mutableStateOf(false) }
        var age by remember { mutableStateOf("22") }
        var payRef by remember { mutableStateOf("") }
        var club by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Simular Inscripción de Formulario", color = SportGold, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = surname,
                        onValueChange = { surname = it },
                        label = { Text("Apellido") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Category Selection Row
                    Text("Categoría", fontSize = 12.sp, color = TextMutedGray)
                    Box {
                        OutlinedButton(
                            onClick = { categoryDropdownOpen = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = SportDarkBg, contentColor = TextCrispWhite),
                            border = BorderStroke(1.dp, SportGold.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(category, fontSize = 12.sp)
                                Text("▼", color = SportGold, fontSize = 10.sp)
                            }
                        }
                        DropdownMenu(
                            expanded = categoryDropdownOpen,
                            onDismissRequest = { categoryDropdownOpen = false },
                            modifier = Modifier.background(SportDarkSurface).fillMaxWidth(0.8f)
                        ) {
                            if (categoriesStrList.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No hay categorías creadas", color = TextMutedGray, fontSize = 12.sp) },
                                    onClick = {}
                                )
                            } else {
                                categoriesStrList.forEach { catStr ->
                                    DropdownMenuItem(
                                        text = { Text(catStr, color = TextCrispWhite, fontSize = 12.sp) },
                                        onClick = {
                                            category = catStr
                                            categoryDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    TextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Edad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = payRef,
                        onValueChange = { payRef = it },
                        label = { Text("Referencia de Pago") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = club,
                        onValueChange = { club = it },
                        label = { Text("Club") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Teléfono") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                    onClick = {
                        if (name.isNotEmpty() && surname.isNotEmpty() && payRef.isNotEmpty()) {
                            viewModel.addAthlete(
                                name = name,
                                surname = surname,
                                category = category,
                                age = age.toIntOrNull() ?: 20,
                                payProof = "https://drive.google.com/file/d/mock_" + Math.random().toString().substring(2, 8),
                                payRef = payRef,
                                club = club.ifEmpty { "Independiente" },
                                phone = phone.ifEmpty { "+584120000000" }
                            )
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Inscribir", color = SportDarkBg)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar", color = TextMutedGray)
                }
            },
            containerColor = SportDarkSurface
        )
    }

    // Edit Athlete Dialog for full manual editing of absolute everything
    if (editingAthlete != null) {
        val athleteToEdit = editingAthlete!!
        var editName by remember { mutableStateOf(athleteToEdit.name) }
        var editSurname by remember { mutableStateOf(athleteToEdit.surname) }
        var editCategory by remember { mutableStateOf(athleteToEdit.category) }
        var categoryDropdownOpen by remember { mutableStateOf(false) }
        var editAge by remember { mutableStateOf(athleteToEdit.age.toString()) }
        var editPayRef by remember { mutableStateOf(athleteToEdit.paymentRef) }
        var editClub by remember { mutableStateOf(athleteToEdit.club) }
        var editPhone by remember { mutableStateOf(athleteToEdit.phone) }

        AlertDialog(
            onDismissRequest = { editingAthlete = null },
            title = { Text("Editar Atleta Inscrito", color = SportGold, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        TextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        TextField(
                            value = editSurname,
                            onValueChange = { editSurname = it },
                            label = { Text("Apellido") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    // Category Selection Row
                    item {
                        Text("Categoría", fontSize = 12.sp, color = TextMutedGray)
                        Box {
                            OutlinedButton(
                                onClick = { categoryDropdownOpen = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = SportDarkBg, contentColor = TextCrispWhite),
                                border = BorderStroke(1.dp, SportGold.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(editCategory, fontSize = 12.sp)
                                    Text("▼", color = SportGold, fontSize = 10.sp)
                                }
                            }
                            DropdownMenu(
                                expanded = categoryDropdownOpen,
                                onDismissRequest = { categoryDropdownOpen = false },
                                modifier = Modifier.background(SportDarkSurface).fillMaxWidth(0.8f)
                            ) {
                                if (categoriesStrList.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No hay categorías", color = TextMutedGray, fontSize = 12.sp) },
                                        onClick = {}
                                    )
                                } else {
                                    categoriesStrList.forEach { catStr ->
                                        DropdownMenuItem(
                                            text = { Text(catStr, color = TextCrispWhite, fontSize = 12.sp) },
                                            onClick = {
                                                editCategory = catStr
                                                categoryDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        TextField(
                            value = editAge,
                            onValueChange = { editAge = it },
                            label = { Text("Edad") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        TextField(
                            value = editPayRef,
                            onValueChange = { editPayRef = it },
                            label = { Text("Referencia de Pago") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        TextField(
                            value = editClub,
                            onValueChange = { editClub = it },
                            label = { Text("Club / Delegación") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        TextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text("Teléfono") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                    onClick = {
                        if (editName.isNotBlank() && editSurname.isNotBlank()) {
                            viewModel.updateAthlete(
                                athleteToEdit.copy(
                                    name = editName.trim(),
                                    surname = editSurname.trim(),
                                    category = editCategory,
                                    age = editAge.toIntOrNull() ?: athleteToEdit.age,
                                    paymentRef = editPayRef.trim(),
                                    club = editClub.trim(),
                                    phone = editPhone.trim()
                                )
                            )
                            editingAthlete = null
                        }
                    }
                ) {
                    Text("Guardar", color = SportDarkBg)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingAthlete = null }) {
                    Text("Cancelar", color = TextMutedGray)
                }
            },
            containerColor = SportDarkSurface
        )
    }
}


@Composable
fun SimControlEquipos(
    viewModel: TournamentViewModel,
    teams: List<Team>
) {
    val categories by viewModel.allCategories.collectAsState()
    val categoriesStrList = remember(categories) {
        categories.map { "${it.gender} - ${it.name}" }
    }
    var showAddTeamDialog by remember { mutableStateOf(false) }
    var editingTeam by remember { mutableStateOf<Team?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Equipos Confirmados", color = TextCrispWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${teams.size} Duplas Registradas", color = TextMutedGray, fontSize = 12.sp)
            }
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                shape = RoundedCornerShape(8.dp),
                onClick = { showAddTeamDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Team", modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Registrar Dupla", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Teams display
        if (teams.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Text("No hay equipos confirmados en juego.", color = TextMutedGray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Group teams by Category
                val groupedByCat = teams.groupBy { it.category }

                groupedByCat.forEach { (categoryName, teamsInCat) ->
                    item {
                        Text(
                            text = "Categoría: $categoryName",
                            fontWeight = FontWeight.Black,
                            color = SportGold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                        )
                    }

                    items(teamsInCat) { team ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { editingTeam = team }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(team.name, fontWeight = FontWeight.Bold, color = TextCrispWhite, fontSize = 14.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(SportBlue.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(team.groupName, color = SportBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text(
                                            text = "JJ: ${team.jj} | JG: ${team.jg} | JP: ${team.jp}",
                                            color = TextMutedGray,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "SG: ${team.sg} | SP: ${team.sp}",
                                            color = TextMutedGray,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(SportGold.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "PTOS: ${team.ptos}",
                                                color = SportGold,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }

                                // Interactive detailed outcomes adjusters for easy simulation!
                                 Row(
                                     verticalAlignment = Alignment.CenterVertically,
                                     horizontalArrangement = Arrangement.spacedBy(4.dp)
                                 ) {
                                     listOf(
                                         Triple("2-0", team.pg2) { delta: Int -> viewModel.adjustTeamDetailedStats(team, pg2Delta = delta, pg3Delta = 0, pp3Delta = 0, pp2Delta = 0) },
                                         Triple("2-1", team.pg3) { delta: Int -> viewModel.adjustTeamDetailedStats(team, pg2Delta = 0, pg3Delta = delta, pp3Delta = 0, pp2Delta = 0) },
                                         Triple("1-2", team.pp3) { delta: Int -> viewModel.adjustTeamDetailedStats(team, pg2Delta = 0, pg3Delta = 0, pp3Delta = delta, pp2Delta = 0) },
                                         Triple("0-2", team.pp2) { delta: Int -> viewModel.adjustTeamDetailedStats(team, pg2Delta = 0, pg3Delta = 0, pp3Delta = 0, pp2Delta = delta) }
                                     ).forEach { (label, value, onAdjust) ->
                                         Column(
                                             horizontalAlignment = Alignment.CenterHorizontally,
                                             modifier = Modifier
                                                 .background(SportDarkBg, RoundedCornerShape(6.dp))
                                                 .padding(horizontal = 4.dp, vertical = 2.dp)
                                         ) {
                                             Text(label, fontSize = 8.sp, color = SportGold, fontWeight = FontWeight.Black)
                                             Row(
                                                 verticalAlignment = Alignment.CenterVertically,
                                                 horizontalArrangement = Arrangement.Center
                                             ) {
                                                 IconButton(
                                                     onClick = { onAdjust(-1) },
                                                     modifier = Modifier.size(18.dp)
                                                 ) {
                                                     Icon(Icons.Default.KeyboardArrowDown, contentDescription = "dec", tint = SportOrange, modifier = Modifier.size(14.dp))
                                                 }
                                                 Text(
                                                     text = "$value",
                                                     color = TextCrispWhite,
                                                     fontWeight = FontWeight.Bold,
                                                     fontSize = 11.sp,
                                                     modifier = Modifier.widthIn(min = 12.dp),
                                                     textAlign = TextAlign.Center
                                                 )
                                                 IconButton(
                                                     onClick = { onAdjust(1) },
                                                     modifier = Modifier.size(18.dp)
                                                 ) {
                                                     Icon(Icons.Default.KeyboardArrowUp, contentDescription = "inc", tint = SportBlue, modifier = Modifier.size(14.dp))
                                                 }
                                             }
                                         }
                                     }

                                     Spacer(Modifier.width(2.dp))

                                     IconButton(
                                         onClick = { editingTeam = team },
                                         modifier = Modifier.size(32.dp)
                                     ) {
                                         Icon(Icons.Default.Edit, contentDescription = "Editar", tint = SportBlue, modifier = Modifier.size(16.dp))
                                     }

                                     IconButton(
                                         onClick = { viewModel.deleteTeam(team) },
                                         modifier = Modifier.size(32.dp)
                                     ) {
                                         Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                     }
                                 }

                                 if (false) {
                                 // Interactive PG and PP adjusters for direct simulation!
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("PG", fontSize = 10.sp, color = TextMutedGray, fontWeight = FontWeight.Bold)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = { viewModel.updateTeamStats(team, team.pg - 1, team.pp) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "PG decrementar", tint = SportOrange, modifier = Modifier.size(16.dp))
                                            }
                                            Text("${team.pg}", color = TextCrispWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            IconButton(
                                                onClick = { viewModel.updateTeamStats(team, team.pg + 1, team.pp) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "PG up", tint = SportGold, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }

                                    Spacer(Modifier.width(8.dp))

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("PP", fontSize = 10.sp, color = TextMutedGray, fontWeight = FontWeight.Bold)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = { viewModel.updateTeamStats(team, team.pg, team.pp - 1) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "PP down", tint = SportOrange, modifier = Modifier.size(16.dp))
                                            }
                                            Text("${team.pp}", color = TextCrispWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            IconButton(
                                                onClick = { viewModel.updateTeamStats(team, team.pg, team.pp + 1) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "PP up", tint = SportGold, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = { editingTeam = team },
                                        modifier = Modifier.padding(start = 8.dp).size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = SportBlue, modifier = Modifier.size(18.dp))
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteTeam(team) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                                     }
                                 }
                                 }
                                 if (true) {
                                     Box(modifier = Modifier.size(0.dp)) {
                                         Text("")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Team Dialog
    if (showAddTeamDialog) {
        var teamName by remember { mutableStateOf("") }
        val initialCategory = remember(categoriesStrList) { categoriesStrList.firstOrNull() ?: "Femenino - Novatas" }
        var category by remember { mutableStateOf(initialCategory) }
        var categoryDropdownOpen by remember { mutableStateOf(false) }
        var group by remember { mutableStateOf("Grupo A") }

        AlertDialog(
            onDismissRequest = { showAddTeamDialog = false },
            title = { Text("Registrar nueva Dupla / Equipo", color = SportGold, fontWeight = FontWeight.Bold, fontSize = 17.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TextField(
                        value = teamName,
                        onValueChange = { teamName = it },
                        label = { Text("Nombre de la Dupla (ej: Ortega / Pérez)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Categoría", fontSize = 12.sp, color = TextMutedGray)
                    Box {
                        OutlinedButton(
                            onClick = { categoryDropdownOpen = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = SportDarkBg, contentColor = TextCrispWhite),
                            border = BorderStroke(1.dp, SportGold.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(category, fontSize = 12.sp)
                                Text("▼", color = SportGold, fontSize = 10.sp)
                            }
                        }
                        DropdownMenu(
                            expanded = categoryDropdownOpen,
                            onDismissRequest = { categoryDropdownOpen = false },
                            modifier = Modifier.background(SportDarkSurface).fillMaxWidth(0.8f)
                        ) {
                            if (categoriesStrList.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No hay categorías creadas", color = TextMutedGray, fontSize = 12.sp) },
                                    onClick = {}
                                )
                            } else {
                                categoriesStrList.forEach { catStr ->
                                    DropdownMenuItem(
                                        text = { Text(catStr, color = TextCrispWhite, fontSize = 12.sp) },
                                        onClick = {
                                            category = catStr
                                            categoryDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Text("Grupo", fontSize = 12.sp, color = TextMutedGray)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Grupo A", "Grupo B", "Grupo Único").forEach { grp ->
                            val isSel = grp == group
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) SportGold else SportDarkSurface)
                                    .clickable { group = grp }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(grp, color = if (isSel) SportDarkBg else TextCrispWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                    onClick = {
                        if (teamName.isNotEmpty()) {
                            viewModel.addTeam(teamName, group, category)
                            showAddTeamDialog = false
                        }
                    }
                ) {
                    Text("Agregar", color = SportDarkBg)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTeamDialog = false }) {
                    Text("Cancelar", color = TextMutedGray)
                }
            },
            containerColor = SportDarkSurface
        )
    }

    // Edit Team Dialog for full manual editing of absolutely everything
    if (editingTeam != null) {
        val teamToEdit = editingTeam!!
        var editTeamName by remember { mutableStateOf(teamToEdit.name) }
        var editGroupName by remember { mutableStateOf(teamToEdit.groupName) }
        var editCategory by remember { mutableStateOf(teamToEdit.category) }
        var editPg2Str by remember { mutableStateOf(teamToEdit.pg2.toString()) }
        var editPg3Str by remember { mutableStateOf(teamToEdit.pg3.toString()) }
        var editPp3Str by remember { mutableStateOf(teamToEdit.pp3.toString()) }
        var editPp2Str by remember { mutableStateOf(teamToEdit.pp2.toString()) }
        var categoryDropdownOpen by remember { mutableStateOf(false) }

        val editPg2 = editPg2Str.toIntOrNull() ?: 0
        val editPg3 = editPg3Str.toIntOrNull() ?: 0
        val editPp3 = editPp3Str.toIntOrNull() ?: 0
        val editPp2 = editPp2Str.toIntOrNull() ?: 0

        val computedPg = editPg2 + editPg3
        val computedPp = editPp2 + editPp3
        val computedSg = (editPg2 * 2) + (editPg3 * 2) + (editPp3 * 1)
        val computedSp = (editPg3 * 1) + (editPp3 * 2) + (editPp2 * 2)
        val computedPoints = (editPg2 * 3) + (editPg3 * 2) + (editPp3 * 1)
        val computedJj = computedPg + computedPp

        AlertDialog(
            onDismissRequest = { editingTeam = null },
            title = { Text("Editar Dupla / Estadísticas", color = SportGold, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        TextField(
                            value = editTeamName,
                            onValueChange = { editTeamName = it },
                            label = { Text("Nombre de la Dupla") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        TextField(
                            value = editGroupName,
                            onValueChange = { editGroupName = it },
                            label = { Text("Grupo / Zona") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Category Selector
                    item {
                        Text("Categoría", fontSize = 11.sp, color = TextMutedGray)
                        Box {
                            OutlinedButton(
                                onClick = { categoryDropdownOpen = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = SportDarkBg, contentColor = TextCrispWhite),
                                border = BorderStroke(1.dp, SportGold.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(editCategory, fontSize = 12.sp)
                                    Text("▼", color = SportGold, fontSize = 10.sp)
                                }
                            }
                            DropdownMenu(
                                expanded = categoryDropdownOpen,
                                onDismissRequest = { categoryDropdownOpen = false },
                                modifier = Modifier.background(SportDarkSurface).fillMaxWidth(0.8f)
                            ) {
                                if (categoriesStrList.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No hay categorías", color = TextMutedGray, fontSize = 12.sp) },
                                        onClick = {}
                                    )
                                } else {
                                    categoriesStrList.forEach { catStr ->
                                        DropdownMenuItem(
                                            text = { Text(catStr, color = TextCrispWhite, fontSize = 12.sp) },
                                            onClick = {
                                                editCategory = catStr
                                                categoryDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text("Edición Manual de Partidos (Modifica sets y puntos):", color = SportGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // PG 2-0
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ganados 2-0 (3 pts)", color = TextCrispWhite, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { editPg2Str = (editPg2 - 1).coerceAtLeast(0).toString() }) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "down", tint = SportOrange)
                                }
                                TextField(
                                    value = editPg2Str,
                                    onValueChange = { editPg2Str = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.width(60.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center, color = TextCrispWhite)
                                )
                                IconButton(onClick = { editPg2Str = (editPg2 + 1).toString() }) {
                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "up", tint = SportGold)
                                }
                            }
                        }
                    }

                    // PG 2-1
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ganados 2-1 (2 pts)", color = TextCrispWhite, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { editPg3Str = (editPg3 - 1).coerceAtLeast(0).toString() }) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "down", tint = SportOrange)
                                }
                                TextField(
                                    value = editPg3Str,
                                    onValueChange = { editPg3Str = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.width(60.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center, color = TextCrispWhite)
                                )
                                IconButton(onClick = { editPg3Str = (editPg3 + 1).toString() }) {
                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "up", tint = SportGold)
                                }
                            }
                        }
                    }

                    // PP 1-2
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Perdidos 1-2 (1 pt)", color = TextCrispWhite, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { editPp3Str = (editPp3 - 1).coerceAtLeast(0).toString() }) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "down", tint = SportOrange)
                                }
                                TextField(
                                    value = editPp3Str,
                                    onValueChange = { editPp3Str = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.width(60.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center, color = TextCrispWhite)
                                )
                                IconButton(onClick = { editPp3Str = (editPp3 + 1).toString() }) {
                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "up", tint = SportGold)
                                }
                            }
                        }
                    }

                    // PP 0-2
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Perdidos 0-2 (0 pts)", color = TextCrispWhite, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { editPp2Str = (editPp2 - 1).coerceAtLeast(0).toString() }) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "down", tint = SportOrange)
                                }
                                TextField(
                                    value = editPp2Str,
                                    onValueChange = { editPp2Str = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.width(60.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center, color = TextCrispWhite)
                                )
                                IconButton(onClick = { editPp2Str = (editPp2 + 1).toString() }) {
                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "up", tint = SportGold)
                                }
                            }
                        }
                    }

                    // Live calculation summary
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SportDarkBg),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, SportGold.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Totales Calculados:", color = SportGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("JJ: $computedJj | JG: $computedPg | JP: $computedPp", color = TextCrispWhite, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text("SG: $computedSg | SP: $computedSp | PTOS: $computedPoints", color = TextCrispWhite, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                    onClick = {
                        if (editTeamName.isNotBlank()) {
                            viewModel.updateTeam(
                                teamToEdit.copy(
                                    name = editTeamName.trim(),
                                    groupName = editGroupName.trim(),
                                    category = editCategory,
                                    pg2 = editPg2,
                                    pg3 = editPg3,
                                    pp3 = editPp3,
                                    pp2 = editPp2,
                                    pg = computedPg,
                                    pp = computedPp
                                )
                            )
                            editingTeam = null
                        }
                    }
                ) {
                    Text("Guardar", color = SportDarkBg)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTeam = null }) {
                    Text("Cancelar", color = TextMutedGray)
                }
            },
            containerColor = SportDarkSurface
        )
    }
}


@Composable
fun SimFixture(
    viewModel: TournamentViewModel,
    teams: List<Team>,
    matches: List<ScheduledMatch>
) {
    var standingsSubTab by remember { mutableStateOf(0) } // 0 = Standings (Clasificación), 1 = Calendar (Fixture/Jornadas)
    val context = LocalContext.current

    // Custom configurations & scheduler state
    val categories by viewModel.allCategories.collectAsState()
    val categoriesStrList = remember(categories) {
        categories.map { "${it.gender} - ${it.name}" }
    }
    var isConfigExpanded by remember { mutableStateOf(false) }
    var configCategory by remember(categoriesStrList) { mutableStateOf(categoriesStrList.firstOrNull() ?: "Femenino - Novatas") }
    var configJourney by remember { mutableStateOf(1) }
    var userMaxJourney by remember { mutableStateOf(5) }
    var configMatchCount by remember { mutableStateOf(4) }
    var selectedGroups by remember { mutableStateOf(setOf<String>()) }

    // Dialog state
    var editingMatch by remember { mutableStateOf<ScheduledMatch?>(null) }
    var showAddManualDialog by remember { mutableStateOf(false) }
    var manualJourneySelected by remember { mutableStateOf(1) }

    // Automatically synchronize available groups for the selected category
    val groupsForCategory = remember(configCategory, teams) {
        teams.filter { it.category == configCategory }.map { it.groupName }.distinct().sorted()
    }
    LaunchedEffect(groupsForCategory) {
        selectedGroups = groupsForCategory.toSet()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Toggle header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .background(SportDarkSurface, RoundedCornerShape(10.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (standingsSubTab == 0) SportGold else Color.Transparent)
                    .clickable { standingsSubTab = 0 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("🏆 Clasificación Automática", color = if (standingsSubTab == 0) SportDarkBg else TextCrispWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (standingsSubTab == 1) SportGold else Color.Transparent)
                    .clickable { standingsSubTab = 1 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("📆 Fixture por Jornadas", color = if (standingsSubTab == 1) SportDarkBg else TextCrispWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        if (standingsSubTab == 0) {
            // Dropdown selection to filter by Category
            val categories by viewModel.allCategories.collectAsState()
            val categoriesStrList = remember(categories) {
                categories.map { "${it.gender} - ${it.name}" }
            }
            var selectedStandingsCategory by remember(categoriesStrList) {
                mutableStateOf(categoriesStrList.firstOrNull() ?: "Masculino - Novatos")
            }
            var categorySelectDropdownOpen by remember { mutableStateOf(false) }
            var editingStandingsTeam by remember { mutableStateOf<Team?>(null) }

            // Filter teams by Selected Category
            val categoryTeams = remember(teams, selectedStandingsCategory) {
                teams.filter { it.category == selectedStandingsCategory }
            }

            // Group teams by groupName
            val teamsByGroup = remember(categoryTeams) {
                categoryTeams.groupBy { it.groupName }
            }

            // Total groups in this category
            val totalGroupsInCat = remember(teamsByGroup) {
                teamsByGroup.keys.size
            }

            Column(modifier = Modifier.fillMaxSize().weight(1f)) {
                // Category Filter Card Selector
                Card(
                    colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Filtrar por Categoría:", color = TextMutedGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(selectedStandingsCategory, color = TextCrispWhite, fontSize = 13.sp, fontWeight = FontWeight.Black)
                        }
                        Box {
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                                shape = RoundedCornerShape(8.dp),
                                onClick = { categorySelectDropdownOpen = true }
                            ) {
                                Text("Cambiar", color = SportDarkBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(4.dp))
                                Text("▼", color = SportDarkBg, fontSize = 9.sp)
                            }
                            DropdownMenu(
                                expanded = categorySelectDropdownOpen,
                                onDismissRequest = { categorySelectDropdownOpen = false },
                                modifier = Modifier
                                    .background(SportDarkSurface)
                                    .fillMaxWidth(0.8f)
                            ) {
                                if (categoriesStrList.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No hay categorías creadas", color = TextMutedGray, fontSize = 12.sp) },
                                        onClick = {}
                                    )
                                } else {
                                    categoriesStrList.forEach { catStr ->
                                        DropdownMenuItem(
                                            text = { Text(catStr, color = TextCrispWhite, fontSize = 12.sp) },
                                            onClick = {
                                                selectedStandingsCategory = catStr
                                                categorySelectDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Info banner about dynamic playoffs qualification rules
                Card(
                     colors = CardDefaults.cardColors(containerColor = SportOrange.copy(alpha = 0.12f)),
                     shape = RoundedCornerShape(8.dp),
                     modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                ) {
                     Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                         Text("Reglas de Clasificación Automática de la Superliga (Edición Habilitada):", color = SportOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                         Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                             Text("•", color = SportOrange, fontSize = 11.sp)
                             Text("Si son 2 grupos: Clasifican los 2 primeros directamente a Semifinales.", color = TextCrispWhite, fontSize = 11.sp)
                         }
                         Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                             Text("•", color = SportOrange, fontSize = 11.sp)
                             Text("Si son 3 grupos: Clasifican los 2 primeros a 6tos de Final.", color = TextCrispWhite, fontSize = 11.sp)
                         }
                         Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                             Text("•", color = SportOrange, fontSize = 11.sp)
                             Text("Si son 4 grupos: Clasifican los 2 primeros a 4tos de Final.", color = TextCrispWhite, fontSize = 11.sp)
                         }
                         Text(
                             text = "Actualmente se detectan -> ${totalGroupsInCat} grupos en esta categoría.",
                             color = SportGold,
                             fontWeight = FontWeight.Bold,
                             fontSize = 11.sp,
                             modifier = Modifier.padding(top = 4.dp)
                         )
                         Text("💡 Toca cualquier dupla para editar sus sets y estadísticas.", color = TextMutedGray, fontSize = 10.sp)
                     }
                }

                if (teamsByGroup.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        Text("No hay duplas en esta categoría aún.", color = TextMutedGray)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        teamsByGroup.forEach { (groupName, groupTeamsList) ->
                            // Sort teams inside group by points desc, then pg desc, then pp asc
                            val sortedGroupTeams = groupTeamsList.sortedWith(
                                compareByDescending<Team> { it.points }
                                    .thenByDescending { it.pg }
                                    .thenBy { it.pp }
                            )

                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SportBlue.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "GRUPO: $groupName (${sortedGroupTeams.size} duplas)",
                                        color = SportBlue,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            itemsIndexed(sortedGroupTeams) { idx, team ->
                                val pos = idx + 1
                                val isTop2 = pos <= 2
                                val qualificationBadgeText = if (isTop2) {
                                    when (totalGroupsInCat) {
                                        2 -> "🟢 Semifinales"
                                        3 -> "🟢 6tos de Final"
                                        4 -> "🟢 4tos de Final"
                                        else -> "🟢 Clasificado"
                                    }
                                } else null

                                val medalColor = when (pos) {
                                    1 -> Color(0xFFFBBF24) // Gold
                                    2 -> Color(0xFF94A3B8) // Silver
                                    3 -> Color(0xFFB45309) // Bronze
                                    else -> Color.Transparent
                                }

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (pos == 1) SportGold.copy(alpha = 0.04f) else SportDarkSurface),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { editingStandingsTeam = team }
                                        .border(
                                            width = if (pos <= 3) 1.dp else 0.5.dp,
                                            color = if (pos <= 3) medalColor.copy(alpha = 0.5f) else TextMutedGray.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(if (pos <= 3) medalColor else SportDarkBg, RoundedCornerShape(6.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "#$pos",
                                                fontWeight = FontWeight.Bold,
                                                color = if (pos <= 3) SportDarkBg else TextCrispWhite,
                                                fontSize = 12.sp
                                            )
                                        }

                                        Spacer(Modifier.width(10.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(team.name, fontWeight = FontWeight.Bold, color = TextCrispWhite, fontSize = 13.sp)
                                            Spacer(Modifier.height(4.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Pts: ${team.points}",
                                                    color = SportGold,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 11.sp
                                                )
                                                Text("|", color = TextMutedGray.copy(alpha = 0.2f), fontSize = 11.sp)
                                                Text(
                                                    text = "${team.pg2}g(2-0) | ${team.pg3}g(2-1) | ${team.pp3}p(1-2) | ${team.pp2}p(0-2)",
                                                    color = TextMutedGray,
                                                    fontSize = 10.sp
                                                )
                                            }
                                            
                                            if (qualificationBadgeText != null) {
                                                Spacer(Modifier.height(4.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(qualificationBadgeText, color = Color(0xFF34D399), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("PG", fontSize = 9.sp, color = TextMutedGray, fontWeight = FontWeight.Bold)
                                                Text("${team.pg}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = SportGold)
                                            }
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("PP", fontSize = 9.sp, color = TextMutedGray, fontWeight = FontWeight.Bold)
                                                Text("${team.pp}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextCrispWhite)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Edit Standings Team Dialog
            if (editingStandingsTeam != null) {
                val teamToEdit = editingStandingsTeam!!
                var editTeamName by remember { mutableStateOf(teamToEdit.name) }
                var editGroupName by remember { mutableStateOf(teamToEdit.groupName) }
                var editCategory by remember { mutableStateOf(teamToEdit.category) }
                var editPg2 by remember { mutableStateOf(teamToEdit.pg2) }
                var editPg3 by remember { mutableStateOf(teamToEdit.pg3) }
                var editPp3 by remember { mutableStateOf(teamToEdit.pp3) }
                var editPp2 by remember { mutableStateOf(teamToEdit.pp2) }
                var categoryDropdownOpen by remember { mutableStateOf(false) }

                val computedPg = editPg2 + editPg3
                val computedPp = editPp2 + editPp3
                val computedPoints = (editPg2 * 3) + (editPg3 * 2) + (editPp3 * 1)

                AlertDialog(
                    onDismissRequest = { editingStandingsTeam = null },
                    title = { Text("Editar Rendimiento y Datos", color = SportGold, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TextField(
                                value = editTeamName,
                                onValueChange = { editTeamName = it },
                                label = { Text("Nombre de la Dupla") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextField(
                                value = editGroupName,
                                onValueChange = { editGroupName = it },
                                label = { Text("Grupo / Zona (ej: Grupo A)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Category dropdown selector
                            Column {
                                Text("Categoría", color = TextMutedGray, fontSize = 11.sp)
                                Box {
                                    OutlinedButton(
                                        onClick = { categoryDropdownOpen = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.outlinedButtonColors(containerColor = SportDarkBg, contentColor = TextCrispWhite),
                                        border = BorderStroke(1.dp, SportGold.copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(editCategory, fontSize = 12.sp)
                                            Text("▼", color = SportGold, fontSize = 10.sp)
                                        }
                                    }
                                    DropdownMenu(
                                        expanded = categoryDropdownOpen,
                                        onDismissRequest = { categoryDropdownOpen = false },
                                        modifier = Modifier.background(SportDarkSurface).fillMaxWidth(0.8f)
                                    ) {
                                        if (categoriesStrList.isEmpty()) {
                                            DropdownMenuItem(
                                                text = { Text("No hay categorías", color = TextMutedGray, fontSize = 12.sp) },
                                                onClick = {}
                                            )
                                        } else {
                                            categoriesStrList.forEach { catStr ->
                                                DropdownMenuItem(
                                                    text = { Text(catStr, color = TextCrispWhite, fontSize = 12.sp) },
                                                    onClick = {
                                                        editCategory = catStr
                                                        categoryDropdownOpen = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Scoreboard points breakdown details
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SportDarkBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Puntuación Resumen Live", color = SportGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Partidos Ganados (PG): $computedPg", color = TextCrispWhite, fontSize = 11.sp)
                                    Text("Partidos Perdidos (PP): $computedPp", color = TextCrispWhite, fontSize = 11.sp)
                                    Text("Puntos Clasificación: $computedPoints pts", color = SportOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            // Sets editors
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Detalle de Resultados (Estadísticas)", color = TextMutedGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                                // PG 2 sets
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Ganado 2-0 sets (3 pts)", color = TextCrispWhite, fontSize = 12.sp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(onClick = { if (editPg2 > 0) editPg2-- }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "down", tint = SportOrange)
                                        }
                                        Text("$editPg2", color = TextCrispWhite, fontWeight = FontWeight.Bold)
                                        IconButton(onClick = { editPg2++ }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "up", tint = SportGold)
                                        }
                                    }
                                }

                                // PG 3 sets
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Ganado 2-1 sets (2 pts)", color = TextCrispWhite, fontSize = 12.sp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(onClick = { if (editPg3 > 0) editPg3-- }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "down", tint = SportOrange)
                                        }
                                        Text("$editPg3", color = TextCrispWhite, fontWeight = FontWeight.Bold)
                                        IconButton(onClick = { editPg3++ }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "up", tint = SportGold)
                                        }
                                    }
                                }

                                // PP 3 sets
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Perdido 1-2 sets (1 pt)", color = TextCrispWhite, fontSize = 12.sp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(onClick = { if (editPp3 > 0) editPp3-- }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "down", tint = SportOrange)
                                        }
                                        Text("$editPp3", color = TextCrispWhite, fontWeight = FontWeight.Bold)
                                        IconButton(onClick = { editPp3++ }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "up", tint = SportGold)
                                        }
                                    }
                                }

                                // PP 2 sets
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Perdido 0-2 sets (0 pts)", color = TextCrispWhite, fontSize = 12.sp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(onClick = { if (editPp2 > 0) editPp2-- }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "down", tint = SportOrange)
                                        }
                                        Text("$editPp2", color = TextCrispWhite, fontWeight = FontWeight.Bold)
                                        IconButton(onClick = { editPp2++ }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "up", tint = SportGold)
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                            onClick = {
                                viewModel.updateTeam(
                                    teamToEdit.copy(
                                        name = editTeamName,
                                        groupName = editGroupName,
                                        category = editCategory,
                                        pg2 = editPg2,
                                        pg3 = editPg3,
                                        pp3 = editPp3,
                                        pp2 = editPp2,
                                        pg = computedPg,
                                        pp = computedPp
                                    )
                                )
                                editingStandingsTeam = null
                            }
                        ) {
                            Text("Guardar", color = SportDarkBg)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { editingStandingsTeam = null }) {
                            Text("Cancelar", color = TextMutedGray)
                        }
                    },
                    containerColor = SportDarkSurface
                )
            }
        } else {
            // Calendar & Scheduling view
            Column(modifier = Modifier.fillMaxSize().weight(1f)) {
                
                // Advanced Controls Section
                Card(
                    colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .border(1.dp, SportGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "⚡ PLANIFICADOR DE JORNADAS Y GRUPOS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = SportGold
                            )
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = if (isConfigExpanded) SportOrange else SportBlue),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                onClick = { isConfigExpanded = !isConfigExpanded }
                            ) {
                                Text(if (isConfigExpanded) "Ocultar" else "Configurar Juegos", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (isConfigExpanded) {
                            Divider(color = TextMutedGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 10.dp))

                            // 1. SELECT JOURNEY (JORNADA)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val maxJourneyFromDb = matches.maxOfOrNull { it.journey } ?: 1
                                val currentLimit = maxOf(userMaxJourney, maxJourneyFromDb)
                                (1..currentLimit).forEach { jNum ->
                                    val isSel = configJourney == jNum
                                    Box(
                                        modifier = Modifier
                                            .size(width = 75.dp, height = 36.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSel) SportBlue else SportDarkBg)
                                            .clickable { configJourney = jNum },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "Jor. $jNum",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) SportDarkBg else TextCrispWhite
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(width = 120.dp, height = 36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SportGold.copy(alpha = 0.2f))
                                        .clickable {
                                            userMaxJourney = currentLimit + 1
                                            configJourney = currentLimit + 1
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "+ Nueva Jornada",
                                        color = SportGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (false) {
                            // 1. SELECT JOURNEY (JORNADA)
                            Text("1. Selecciona la Jornada a programar:", color = TextCrispWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                (1..5).forEach { jNum ->
                                    val isSel = configJourney == jNum
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSel) SportBlue else SportDarkBg)
                                            .clickable { configJourney = jNum }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "Jor. $jNum",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) SportDarkBg else TextCrispWhite
                                        )
                                    }
                                }
                            }

                            }
                            // 2. SELECT CATEGORY
                            Spacer(Modifier.height(8.dp))
                            Text("2. Selecciona la Categoría:", color = TextCrispWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                categoriesStrList.forEach { cat ->
                                    val isSel = configCategory == cat
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSel) SportOrange else SportDarkBg)
                                            .clickable { configCategory = cat }
                                            .padding(horizontal = 14.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            cat,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) SportDarkBg else TextCrispWhite
                                        )
                                    }
                                }
                            }

                            // 3. CHOOSE QUANTITY OF MATCHES
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "3. Facilidad de juegos por jornada (Elige un preset o escribe):",
                                color = TextCrispWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(10, 20, 30, 40, 50).forEach { presetNum ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (configMatchCount == presetNum) SportGold else SportDarkBg)
                                            .clickable { configMatchCount = presetNum }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "$presetNum",
                                            color = if (configMatchCount == presetNum) SportDarkBg else TextCrispWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                            
                            OutlinedTextField(
                                value = configMatchCount.toString(),
                                onValueChange = {
                                    val parsed = it.toIntOrNull() ?: 1
                                    configMatchCount = parsed
                                },
                                label = { Text("Otras cantidades de juegos") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SportGold,
                                    unfocusedBorderColor = TextMutedGray.copy(alpha = 0.4f),
                                    focusedLabelColor = SportGold,
                                    unfocusedLabelColor = TextMutedGray
                                )
                            )

                            if (false) {
                            // 3. CHOOSE QUANTITY OF MATCHES
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "3. Cantidad de juegos en esta jornada:",
                                    color = TextCrispWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (configMatchCount > 1) configMatchCount-- },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("-", color = SportGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(SportDarkBg, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 14.dp, vertical = 4.dp)
                                    ) {
                                        Text("$configMatchCount", color = TextCrispWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                    IconButton(
                                        onClick = { configMatchCount++ },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("+", color = SportGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            }
                            // 4. ACTIVE GROUPS CHECKBOXES
                            Spacer(Modifier.height(8.dp))
                            Text("4. Grupos de $configCategory que jugarán:", color = TextCrispWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            
                            if (groupsForCategory.isEmpty()) {
                                Text("No hay equipos ni grupos registrados en esta categoría.", color = SportOrange, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                            } else {
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    groupsForCategory.forEach { groupName ->
                                        val isChecked = selectedGroups.contains(groupName)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedGroups = if (isChecked) {
                                                        selectedGroups - groupName
                                                    } else {
                                                        selectedGroups + groupName
                                                    }
                                                }
                                                .padding(vertical = 2.dp)
                                        ) {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = null, // handled by clickable Row
                                                colors = CheckboxDefaults.colors(checkedColor = SportGold, uncheckedColor = TextMutedGray)
                                            )
                                            Spacer(Modifier.width(6.dp))
                                            Text(groupName, color = TextCrispWhite, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            // GENERATE ACTION BUTTON
                            Spacer(Modifier.height(12.dp))
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                onClick = {
                                    if (selectedGroups.isEmpty()) {
                                        Toast.makeText(context, "Por favor, selecciona al menos un grupo de juego.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    viewModel.generateCustomMatchesForCategoryAndJourney(
                                        journey = configJourney,
                                        category = configCategory,
                                        matchCount = configMatchCount,
                                        activeGroups = selectedGroups.toList()
                                    ) { success, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                        if (success) {
                                            isConfigExpanded = false
                                        }
                                    }
                                }
                            ) {
                                Text("⚡ Programar $configMatchCount Juegos de $configCategory", color = SportDarkBg, fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                        } else {
                            // Short description when closed
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Elige cuántos juegos quieres por categoría en cada jornada, qué grupos participan o edita los enfrentamientos directamente haciendo clic en ellos.",
                                color = TextMutedGray,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                // Global standard Apps Script generator button
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = SportBlue),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(10.dp),
                    onClick = { viewModel.triggerGenerateFixture() }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Generate", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Autogenerar Todo (Round-Robin Apps Script)", fontWeight = FontWeight.Black, fontSize = 13.sp, color = TextCrispWhite)
                }

                if (matches.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No hay partidos programados en el calendario.", color = TextMutedGray)
                            Spacer(Modifier.height(8.dp))
                            Text("Usa el Planificador Inteligente arriba o genera emparejamientos automáticos.", color = TextMutedGray, fontSize = 11.sp, textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Group by journey (Jornada)
                        val groupedByJourney = matches.groupBy { it.journey }.toSortedMap()

                        groupedByJourney.forEach { (journeyNum, matchesInJourney) ->
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "📅 Jornada $journeyNum",
                                            fontWeight = FontWeight.Black,
                                            color = SportOrange,
                                            fontSize = 15.sp
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "${matchesInJourney.size} partidos",
                                            color = TextMutedGray,
                                            fontSize = 11.sp
                                        )
                                    }
                                    
                                    // Add Manual Match directly to this Jornada
                                    Button(
                                        colors = ButtonDefaults.buttonColors(containerColor = SportGold.copy(alpha = 0.15f)),
                                        shape = RoundedCornerShape(4.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        onClick = {
                                            manualJourneySelected = journeyNum
                                            showAddManualDialog = true
                                        }
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = SportGold, modifier = Modifier.size(12.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Juego Manual", fontSize = 10.sp, color = SportGold)
                                    }
                                }
                            }

                            items(matchesInJourney) { match ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { editingMatch = match } // Trigger Edit Dialog
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .background(SportGold.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(match.category, color = SportGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    "Árbitro: ${match.referee.ifEmpty { "Sin asignar" }}", 
                                                    fontSize = 11.sp, 
                                                    color = TextMutedGray
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Icon(
                                                    Icons.Default.Edit, 
                                                    contentDescription = "Editar", 
                                                    tint = SportGold.copy(alpha = 0.7f), 
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                match.teamAName, 
                                                fontWeight = FontWeight.Bold, 
                                                color = TextCrispWhite, 
                                                fontSize = 13.sp, 
                                                modifier = Modifier.weight(1f), 
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                "VS", 
                                                fontWeight = FontWeight.Black, 
                                                color = SportOrange, 
                                                modifier = Modifier.padding(horizontal = 12.dp), 
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                match.teamBName, 
                                                fontWeight = FontWeight.Bold, 
                                                color = TextCrispWhite, 
                                                fontSize = 13.sp, 
                                                modifier = Modifier.weight(1f), 
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOGS FOR INTERACTIVE SCHEDULING ---

    // 1. EDIT MATCH DIALOG (Opciones de enfrentamientos sin alterar la secuencia)
    editingMatch?.let { match ->
        val categoryTeams = remember(match.category, teams) {
            teams.filter { it.category == match.category }
        }

        var selectedTeamA by remember(match) { mutableStateOf(match.teamAName) }
        var selectedTeamB by remember(match) { mutableStateOf(match.teamBName) }
        var matchReferee by remember(match) { mutableStateOf(match.referee) }

        var showADropdown by remember { mutableStateOf(false) }
        var showBDropdown by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { editingMatch = null },
            title = {
                Text(
                    "Editar Enfrentamiento (Jor. ${match.journey} - ${match.category})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportGold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Modifica el juego o selecciona nuevos enfrentamientos del listado sin alterar el orden ni la fecha de la jornada.",
                        fontSize = 11.sp,
                        color = TextMutedGray
                    )

                    // TEAM A SELECTOR dropdown
                    Column {
                        Text("Dupla A:", color = TextCrispWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                        Box {
                            OutlinedButton(
                                onClick = { showADropdown = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = SportDarkBg, contentColor = TextCrispWhite),
                                border = BorderStroke(1.dp, SportGold.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(selectedTeamA, maxLines = 1, fontSize = 12.sp)
                                    Text("▼", color = SportGold, fontSize = 10.sp)
                                }
                            }
                            DropdownMenu(
                                expanded = showADropdown,
                                onDismissRequest = { showADropdown = false },
                                modifier = Modifier
                                    .background(SportDarkSurface)
                                    .fillMaxWidth(0.7f)
                            ) {
                                categoryTeams.forEach { team ->
                                    DropdownMenuItem(
                                        text = { Text(team.name, color = TextCrispWhite, fontSize = 12.sp) },
                                        onClick = {
                                            selectedTeamA = team.name
                                            showADropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // TEAM B SELECTOR dropdown
                    Column {
                        Text("Dupla B:", color = TextCrispWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                        Box {
                            OutlinedButton(
                                onClick = { showBDropdown = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = SportDarkBg, contentColor = TextCrispWhite),
                                border = BorderStroke(1.dp, SportGold.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(selectedTeamB, maxLines = 1, fontSize = 12.sp)
                                    Text("▼", color = SportGold, fontSize = 10.sp)
                                }
                            }
                            DropdownMenu(
                                expanded = showBDropdown,
                                onDismissRequest = { showBDropdown = false },
                                modifier = Modifier
                                    .background(SportDarkSurface)
                                    .fillMaxWidth(0.7f)
                            ) {
                                categoryTeams.forEach { team ->
                                    DropdownMenuItem(
                                        text = { Text(team.name, color = TextCrispWhite, fontSize = 12.sp) },
                                        onClick = {
                                            selectedTeamB = team.name
                                            showBDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // REFEREE
                    OutlinedTextField(
                        value = matchReferee,
                        onValueChange = { matchReferee = it },
                        label = { Text("Árbitro Principal", fontSize = 11.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = TextCrispWhite, fontSize = 13.sp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SportGold,
                            unfocusedBorderColor = TextMutedGray.copy(alpha = 0.3f),
                            focusedLabelColor = SportGold,
                            unfocusedLabelColor = TextMutedGray
                        )
                    )
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Delete Button
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(6.dp),
                        onClick = {
                            viewModel.deleteMatch(match)
                            Toast.makeText(context, "Juego eliminado de la programación.", Toast.LENGTH_SHORT).show()
                            editingMatch = null
                        }
                    ) {
                        Text("Eliminar", color = TextCrispWhite)
                    }

                    // Save Button
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                        shape = RoundedCornerShape(6.dp),
                        onClick = {
                            if (selectedTeamA == selectedTeamB) {
                                Toast.makeText(context, "Error: Un equipo no puede jugar contra sí mismo.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.updateMatch(
                                match.copy(
                                    teamAName = selectedTeamA,
                                    teamBName = selectedTeamB,
                                    referee = matchReferee
                                )
                            )
                            Toast.makeText(context, "Enfrentamiento modificado correctamente.", Toast.LENGTH_SHORT).show()
                            editingMatch = null
                        }
                    ) {
                        Text("Guardar", color = SportDarkBg, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { editingMatch = null }) {
                    Text("Cancelar", color = TextMutedGray)
                }
            },
            containerColor = SportDarkSurface
        )
    }

    // 2. ADD MANUAL MATCH DIALOG FOR ACTIVE JOURNEY
    if (showAddManualDialog) {
        var manualCategory by remember(categoriesStrList) { mutableStateOf(categoriesStrList.firstOrNull() ?: "Femenino - Novatas") }
        val categoryTeams = remember(manualCategory, teams) {
            teams.filter { it.category == manualCategory }
        }

        var teamASelected by remember(manualCategory) {
            mutableStateOf(categoryTeams.getOrNull(0)?.name ?: "")
        }
        var teamBSelected by remember(manualCategory) {
            mutableStateOf(categoryTeams.getOrNull(1)?.name ?: "")
        }
        var manualReferee by remember { mutableStateOf("") }

        var showADropdown by remember { mutableStateOf(false) }
        var showBDropdown by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddManualDialog = false },
            title = {
                Text(
                    "Agregar Juego Manual (Jornada $manualJourneySelected)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportGold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Crea un partido específico dentro del fixture sin tocar los juegos ya existentes.",
                        fontSize = 11.sp,
                        color = TextMutedGray
                    )

                    // 1. Category Switch
                    Text("Filtrar por Categoría:", color = TextCrispWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        categoriesStrList.forEach { cat ->
                            val isSel = manualCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) SportGold else SportDarkBg)
                                    .clickable { manualCategory = cat }
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    cat,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) SportDarkBg else TextCrispWhite
                                )
                            }
                        }
                    }

                    // 2. Select Team A
                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        Text("Equipo A:", color = TextCrispWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 2.dp))
                        Box {
                            OutlinedButton(
                                onClick = { showADropdown = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = SportDarkBg, contentColor = TextCrispWhite),
                                border = BorderStroke(1.dp, SportGold.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(teamASelected.ifEmpty { "Seleccionar Equipo" }, fontSize = 12.sp, maxLines = 1)
                                    Text("▼", color = SportGold, fontSize = 10.sp)
                                }
                            }
                            DropdownMenu(
                                expanded = showADropdown,
                                onDismissRequest = { showADropdown = false },
                                modifier = Modifier
                                    .background(SportDarkSurface)
                                    .fillMaxWidth(0.7f)
                            ) {
                                categoryTeams.forEach { t ->
                                    DropdownMenuItem(
                                        text = { Text(t.name, color = TextCrispWhite, fontSize = 12.sp) },
                                        onClick = {
                                            teamASelected = t.name
                                            showADropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 3. Select Team B
                    Column {
                        Text("Equipo B:", color = TextCrispWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 2.dp))
                        Box {
                            OutlinedButton(
                                onClick = { showBDropdown = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = SportDarkBg, contentColor = TextCrispWhite),
                                border = BorderStroke(1.dp, SportGold.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(teamBSelected.ifEmpty { "Seleccionar Equipo" }, fontSize = 12.sp, maxLines = 1)
                                    Text("▼", color = SportGold, fontSize = 10.sp)
                                }
                            }
                            DropdownMenu(
                                expanded = showBDropdown,
                                onDismissRequest = { showBDropdown = false },
                                modifier = Modifier
                                    .background(SportDarkSurface)
                                    .fillMaxWidth(0.7f)
                            ) {
                                categoryTeams.forEach { t ->
                                    DropdownMenuItem(
                                        text = { Text(t.name, color = TextCrispWhite, fontSize = 12.sp) },
                                        onClick = {
                                            teamBSelected = t.name
                                            showBDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 4. Referee field
                    OutlinedTextField(
                        value = manualReferee,
                        onValueChange = { manualReferee = it },
                        label = { Text("Árbitro Principal", fontSize = 11.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = TextCrispWhite, fontSize = 12.sp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SportGold,
                            unfocusedBorderColor = TextMutedGray.copy(alpha = 0.3f)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                    shape = RoundedCornerShape(6.dp),
                    onClick = {
                        if (teamASelected.isBlank() || teamBSelected.isBlank()) {
                            Toast.makeText(context, "Por favor, selecciona ambos equipos.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (teamASelected == teamBSelected) {
                            Toast.makeText(context, "Un equipo no puede jugar contra sí mismo.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.addManualMatch(
                            journey = manualJourneySelected,
                            category = manualCategory,
                            teamA = teamASelected,
                            teamB = teamBSelected,
                            referee = manualReferee
                        )
                        Toast.makeText(context, "Partido manual registrado en Jornada $manualJourneySelected.", Toast.LENGTH_SHORT).show()
                        showAddManualDialog = false
                    }
                ) {
                    Text("Inscribir Juego", color = SportDarkBg, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddManualDialog = false }) {
                    Text("Cancelar", color = TextMutedGray)
                }
            },
            containerColor = SportDarkSurface
        )
    }
}


@Composable
fun SimArbitraje(
    viewModel: TournamentViewModel,
    matches: List<ScheduledMatch>
) {
    val journeys = matches.map { it.journey }.distinct().sorted()
    var selectedJourney by remember { mutableStateOf(1) }

    // On startup, adjust selection safely
    LaunchedEffect(journeys) {
        if (journeys.isNotEmpty() && !journeys.contains(selectedJourney)) {
            selectedJourney = journeys.first()
        }
    }

    // Filter matches for the selected journey (representing Tab 4 filtering formula!)
    val matchesForJourney = matches.filter { it.journey == selectedJourney }

    // Calculate total arbitraje expenses paid on selected journey (SUMIF representation!)
    val totalPaidToday = matchesForJourney.filter { it.isPaid }.sumOf { it.totalPaid }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Stats box
        Card(
            colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .border(1.dp, SportBlue.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("CÁLCULADO POR FORMULA (SUMAR.SI): REGISTRO DE ARBITRAJE", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = SportBlue)
                Spacer(Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Pago Realizado (Gasto de la Jornada)", fontSize = 11.sp, color = TextMutedGray)
                        Text("$${String.format("%.2f", totalPaidToday)} USD", fontSize = 18.sp, fontWeight = FontWeight.Black, color = SportGold)
                    }
                    Box(
                        modifier = Modifier
                            .background(SportBlue.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text("Jornada Activa: $selectedJourney", color = SportBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Horizontal scroll row of journeys to select
        if (journeys.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Text("No hay jornadas programadas. Genera el fixture primero.", color = TextMutedGray, fontSize = 13.sp)
            }
        } else {
            Text("Selecciona una Jornada para Registrar Arbitrajes:", color = TextCrispWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                journeys.forEach { jNum ->
                    val isSel = selectedJourney == jNum
                    Box(
                        modifier = Modifier
                            .size(width = 75.dp, height = 36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) SportBlue else SportDarkSurface)
                            .clickable { selectedJourney = jNum }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Jornada $jNum",
                            color = if (isSel) SportDarkBg else TextCrispWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Editable Matches with Arbiters elements
            LazyColumn(modifier = Modifier.fillMaxSize().weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(matchesForJourney) { match ->
                    var localRefName by remember(match.id) { mutableStateOf(match.referee) }
                    var localFee by remember(match.id) { mutableStateOf(match.totalPaid.toString()) }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Match title
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${match.teamAName} vs ${match.teamBName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextCrispWhite
                                )
                                Box(
                                    modifier = Modifier
                                        .background(SportOrange.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(match.category, color = SportOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            // Referee name input
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = localRefName,
                                    onValueChange = {
                                        localRefName = it
                                        viewModel.updateMatchArbitration(match, it, match.isPaid, match.paymentMethod, match.totalPaid)
                                    },
                                    label = { Text("Árbitro Asignado", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SportBlue,
                                        unfocusedBorderColor = TextMutedGray.copy(alpha = 0.4f),
                                        focusedLabelColor = SportBlue,
                                        unfocusedLabelColor = TextMutedGray
                                    )
                                )

                                Spacer(Modifier.width(10.dp))

                                // Referee Fee
                                OutlinedTextField(
                                    value = localFee,
                                    onValueChange = {
                                        localFee = it
                                        val parseVal = it.toDoubleOrNull() ?: match.totalPaid
                                        viewModel.updateMatchArbitration(match, localRefName, match.isPaid, match.paymentMethod, parseVal)
                                    },
                                    label = { Text("Monto", fontSize = 11.sp) },
                                    modifier = Modifier.width(80.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SportBlue,
                                        unfocusedBorderColor = TextMutedGray.copy(alpha = 0.4f),
                                        focusedLabelColor = SportBlue,
                                        unfocusedLabelColor = TextMutedGray
                                    )
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            // Checkbox & payment method configuration row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Column method dropdown
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Método:", color = TextMutedGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 4.dp))
                                    listOf("Efectivo", "Pago Móvil", "Transf").forEach { opt ->
                                        val isSel = match.paymentMethod == opt
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 2.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (isSel) SportGold.copy(alpha = 0.15f) else Color.Transparent)
                                                .border(0.5.dp, if (isSel) SportGold else TextMutedGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                                .clickable {
                                                    viewModel.updateMatchArbitration(match, localRefName, match.isPaid, opt, match.totalPaid)
                                                }
                                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                        ) {
                                            Text(opt, color = if (isSel) SportGold else TextMutedGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // Checkbox marking PAID State!
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("PAGADO", color = if (match.isPaid) SportGold else TextMutedGray, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                    Checkbox(
                                        checked = match.isPaid,
                                        onCheckedChange = { checked ->
                                            viewModel.updateMatchArbitration(match, localRefName, checked, match.paymentMethod, match.totalPaid)
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = SportGold,
                                            uncheckedColor = TextMutedGray
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


data class BudgetCategory(
    val id: String,
    val name: String,
    val percentage: Float
)

fun saveBudgetCategories(context: Context, categories: List<BudgetCategory>) {
    val serialized = categories.joinToString(";") { "${it.id}|${it.name}|${it.percentage}" }
    val prefs = context.getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("categories", serialized).apply()
}

fun loadBudgetCategories(context: Context): List<BudgetCategory> {
    val prefs = context.getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)
    val serialized = prefs.getString("categories", "") ?: ""
    if (serialized.isEmpty()) {
        return listOf(
            BudgetCategory("1", "Premiación", 40f),
            BudgetCategory("2", "Logística y Balones", 30f),
            BudgetCategory("3", "Apoyo de Mesa Técnica", 20f),
            BudgetCategory("4", "Hidratación e Hielo", 10f)
        )
    }
    return try {
        serialized.split(";").mapNotNull { item ->
            val parts = item.split("|")
            if (parts.size == 3) {
                BudgetCategory(parts[0], parts[1], parts[2].toFloatOrNull() ?: 0f)
            } else null
        }
    } catch (e: Exception) {
        listOf(
            BudgetCategory("1", "Premiación", 40f),
            BudgetCategory("2", "Logística y Balones", 30f),
            BudgetCategory("3", "Apoyo de Mesa Técnica", 20f),
            BudgetCategory("4", "Hidratación e Hielo", 10f)
        )
    }
}

@Composable
fun SimPresupuesto(
    viewModel: TournamentViewModel,
    settings: TournamentSettings,
    matches: List<ScheduledMatch>
) {
    val context = LocalContext.current
    val journeys = remember(matches) { matches.map { it.journey }.distinct().sorted() }
    var selectedJourneyForBudget by remember { mutableStateOf<Int?>(1) }
    
    LaunchedEffect(journeys) {
        if (selectedJourneyForBudget != null && journeys.isNotEmpty() && !journeys.contains(selectedJourneyForBudget)) {
            selectedJourneyForBudget = journeys.first()
        }
    }

    val matchesForBudget = remember(selectedJourneyForBudget, matches) {
        if (selectedJourneyForBudget == null) {
            matches
        } else {
            matches.filter { it.journey == selectedJourneyForBudget }
        }
    }

    val totalPotentialRefereeBudget = remember(matchesForBudget) { matchesForBudget.sumOf { it.totalPaid } }
    val totalCollectedRefereeBudget = remember(matchesForBudget) { matchesForBudget.filter { it.isPaid }.sumOf { it.totalPaid } }

    var budgetCategoriesList by remember { mutableStateOf(loadBudgetCategories(context)) }

    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryPercentage by remember { mutableStateOf("") }

    val totalSumPercent = remember(budgetCategoriesList) { budgetCategoriesList.sumOf { it.percentage.toDouble() }.toFloat() }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, SportOrange.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Pestaña 5: Presupuesto Basado en Arbitrajes Recogidos", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SportOrange)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Usa esta vista para calcular el presupuesto total a partir de los arbitrajes pagados (recogidos) en la jornada, y dividirlos según tus categorías manuales.",
                        fontSize = 11.sp,
                        color = TextMutedGray,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Selecciona Jornada para calcular Presupuesto:",
                        fontWeight = FontWeight.Bold,
                        color = TextCrispWhite,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isAllSelected = selectedJourneyForBudget == null
                        Box(
                            modifier = Modifier
                                .size(width = 85.dp, height = 36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isAllSelected) SportOrange else SportDarkBg)
                                .clickable { selectedJourneyForBudget = null },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Todas",
                                color = if (isAllSelected) SportDarkBg else TextCrispWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        journeys.forEach { jNum ->
                            val isSel = selectedJourneyForBudget == jNum
                            Box(
                                modifier = Modifier
                                    .size(width = 85.dp, height = 36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) SportBlue else SportDarkBg)
                                    .clickable { selectedJourneyForBudget = jNum },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Jornada $jNum",
                                    color = if (isSel) SportDarkBg else TextCrispWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Divider(color = TextMutedGray.copy(alpha = 0.1f))
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Recogido (Fisico/Pagado)", color = SportOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("$${String.format("%.2f", totalCollectedRefereeBudget)} USD", color = SportGold, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Estimado (General)", color = TextMutedGray, fontSize = 11.sp)
                            Text("$${String.format("%.2f", totalPotentialRefereeBudget)} USD", color = TextCrispWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Crear Categoría Manual", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextCrispWhite)

                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Nombre de la Categoría") },
                        shape = RoundedCornerShape(6.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SportGold,
                            unfocusedBorderColor = TextMutedGray.copy(alpha = 0.4f),
                            focusedLabelColor = SportGold,
                            unfocusedLabelColor = TextMutedGray
                        )
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCategoryPercentage,
                            onValueChange = { newCategoryPercentage = it },
                            label = { Text("Porcentaje (%)") },
                            shape = RoundedCornerShape(6.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SportGold,
                                unfocusedBorderColor = TextMutedGray.copy(alpha = 0.4f),
                                focusedLabelColor = SportGold,
                                unfocusedLabelColor = TextMutedGray
                            )
                        )

                        Button(
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                            onClick = {
                                val pct = newCategoryPercentage.toFloatOrNull() ?: 0f
                                if (newCategoryName.trim().isEmpty()) {
                                    Toast.makeText(context, "Por favor indica un nombre para la categoría.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (pct <= 0 || pct > 100) {
                                    Toast.makeText(context, "Por favor indica un porcentaje válido (1-100%).", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val updated = budgetCategoriesList + BudgetCategory(
                                    id = System.currentTimeMillis().toString(),
                                    name = newCategoryName.trim(),
                                    percentage = pct
                                )
                                budgetCategoriesList = updated
                                saveBudgetCategories(context, updated)
                                newCategoryName = ""
                                newCategoryPercentage = ""
                                Toast.makeText(context, "Categoría de presupuesto agregada.", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = SportDarkBg, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Agregar", color = SportDarkBg, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (totalSumPercent != 100f) {
                        Text(
                            text = "⚠ Porcentajes actuales suman: ${totalSumPercent.toInt()}%. No es obligatorio, pero para un reparto óptimo deben sumar exactamente 100%.",
                            color = SportOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = "✔ Perfecto! Los porcentajes suman exactamente el 100%.",
                            color = Color.Green.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        item {
            Text(
                "REPARTICIÓN DE PRESUPUESTO PERSONALIZADO",
                fontWeight = FontWeight.Black,
                color = SportOrange,
                fontSize = 12.sp
            )
        }

        if (budgetCategoriesList.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    Text("No has creado categorías. Agrega una arriba.", color = TextMutedGray, fontSize = 12.sp)
                }
            }
        } else {
            items(budgetCategoriesList) { itemCat ->
                val allocatedMoney = totalCollectedRefereeBudget * (itemCat.percentage / 100.0)
                Card(
                    colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(itemCat.name, fontWeight = FontWeight.Bold, color = TextCrispWhite, fontSize = 13.sp)
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(SportBlue.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("${itemCat.percentage}%", color = SportBlue, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Estimado general: $${String.format("%.2f", totalPotentialRefereeBudget * (itemCat.percentage / 100.0))} USD",
                                    color = TextMutedGray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "$${String.format("%.2f", allocatedMoney)} USD",
                                fontWeight = FontWeight.Black,
                                color = SportGold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            IconButton(
                                onClick = {
                                    val updated = budgetCategoriesList.filter { it.id != itemCat.id }
                                    budgetCategoriesList = updated
                                    saveBudgetCategories(context, updated)
                                    Toast.makeText(context, "Categoría eliminada.", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BypassedSimPresupuesto(
    viewModel: TournamentViewModel,
    settings: TournamentSettings
) {
    var budgetInput by remember(settings.totalBudget) { mutableStateOf(settings.totalBudget.toString()) }
    var mascPercentInput by remember(settings.mascPerc) { mutableStateOf(settings.mascPerc.toString()) }
    var femPercentInput by remember(settings.femPerc) { mutableStateOf(settings.femPerc.toString()) }
    var mixPercentInput by remember(settings.mixPerc) { mutableStateOf(settings.mixPerc.toString()) }

    var premPercInput by remember(settings.premiacionPerc) { mutableStateOf(settings.premiacionPerc.toString()) }
    var logPercInput by remember(settings.logisticaPerc) { mutableStateOf(settings.logisticaPerc.toString()) }

    val mP = mascPercentInput.toFloatOrNull() ?: 0f
    val fP = femPercentInput.toFloatOrNull() ?: 0f
    val xP = mixPercentInput.toFloatOrNull() ?: 0f
    val totalSumPercent = mP + fP + xP

    val totalBudgetCalculated = budgetInput.toDoubleOrNull() ?: 0.0
    val mascMoney = totalBudgetCalculated * (mP / 100.0)
    val femMoney = totalBudgetCalculated * (fP / 100.0)
    val mixMoney = totalBudgetCalculated * (xP / 100.0)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, SportOrange.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Pestaña 5: Presupuesto Global de Arbitraje", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SportOrange)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Usa esta vista editable para ingresar el presupuesto de arbitraje asignado y distribuirlo automáticamente entre las tres categorías de la liga.",
                        fontSize = 12.sp,
                        color = TextMutedGray
                    )
                }
            }
        }

        // Budget settings inputs
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Configurar Presupuestos", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextCrispWhite)

                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = {
                            budgetInput = it
                            val parseVal = it.toDoubleOrNull() ?: settings.totalBudget
                            viewModel.saveSettings(parseVal, mP, fP, xP, settings.premiacionPerc, settings.logisticaPerc, settings.refereeFeePerMatch)
                        },
                        label = { Text("Presupuesto de Arbitraje Global ($)") },
                        shape = RoundedCornerShape(6.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SportGold,
                            unfocusedBorderColor = TextMutedGray.copy(alpha = 0.4f),
                            focusedLabelColor = SportGold,
                            unfocusedLabelColor = TextMutedGray
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = mascPercentInput,
                            onValueChange = {
                                mascPercentInput = it
                                val parseVal = it.toFloatOrNull() ?: settings.mascPerc
                                viewModel.saveSettings(totalBudgetCalculated, parseVal, fP, xP, settings.premiacionPerc, settings.logisticaPerc, settings.refereeFeePerMatch)
                            },
                            label = { Text("Masc (%)") },
                            shape = RoundedCornerShape(6.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SportGold,
                                unfocusedBorderColor = TextMutedGray.copy(alpha = 0.4f)
                            )
                        )

                        OutlinedTextField(
                            value = femPercentInput,
                            onValueChange = {
                                femPercentInput = it
                                val parseVal = it.toFloatOrNull() ?: settings.femPerc
                                viewModel.saveSettings(totalBudgetCalculated, mP, parseVal, xP, settings.premiacionPerc, settings.logisticaPerc, settings.refereeFeePerMatch)
                            },
                            label = { Text("Fem (%)") },
                            shape = RoundedCornerShape(6.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SportGold,
                                unfocusedBorderColor = TextMutedGray.copy(alpha = 0.4f)
                            )
                        )

                        OutlinedTextField(
                            value = mixPercentInput,
                            onValueChange = {
                                mixPercentInput = it
                                val parseVal = it.toFloatOrNull() ?: settings.mixPerc
                                viewModel.saveSettings(totalBudgetCalculated, mP, fP, parseVal, settings.premiacionPerc, settings.logisticaPerc, settings.refereeFeePerMatch)
                            },
                            label = { Text("Mixto (%)") },
                            shape = RoundedCornerShape(6.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SportGold,
                                unfocusedBorderColor = TextMutedGray.copy(alpha = 0.4f)
                            )
                        )
                    }

                    // Check validation sum of percents
                    if (totalSumPercent != 100f) {
                        Text(
                            text = "⚠ La suma de porcentajes es ${totalSumPercent.toInt()}%. Para calmar la lógica, configure para sumar exactamente 100%.",
                            color = SportOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = "✔ Porcentajes correctos y equilibrados (Suman 100%).",
                            color = Color.Green.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Divider(color = TextMutedGray.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))

                    Text("Pestaña 1: Configurar porcentajes de Inscripción", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextCrispWhite)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = premPercInput,
                            onValueChange = {
                                premPercInput = it
                                val parseVal = it.toFloatOrNull() ?: settings.premiacionPerc
                                viewModel.saveSettings(totalBudgetCalculated, mP, fP, xP, parseVal, settings.logisticaPerc, settings.refereeFeePerMatch)
                            },
                            label = { Text(" Premiación (%)") },
                            shape = RoundedCornerShape(6.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SportGold, unfocusedBorderColor = TextMutedGray.copy(alpha = 0.4f))
                        )

                        OutlinedTextField(
                            value = logPercInput,
                            onValueChange = {
                                logPercInput = it
                                val parseVal = it.toFloatOrNull() ?: settings.logisticaPerc
                                viewModel.saveSettings(totalBudgetCalculated, mP, fP, xP, settings.premiacionPerc, parseVal, settings.refereeFeePerMatch)
                            },
                            label = { Text("Logística (%)") },
                            shape = RoundedCornerShape(6.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SportGold, unfocusedBorderColor = TextMutedGray.copy(alpha = 0.4f))
                        )
                    }
                }
            }
        }

        // Output monetary sums distribution card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Conversión Automática en Dinero", fontWeight = FontWeight.Black, fontSize = 14.sp, color = SportGold)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Masculino ($mP%)", color = TextCrispWhite, fontSize = 13.sp)
                        Text("$${String.format("%.2f", mascMoney)} USD", fontWeight = FontWeight.Bold, color = SportGold, fontSize = 13.sp)
                    }
                    Divider(color = TextMutedGray.copy(alpha = 0.05f))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Femenino ($fP%)", color = TextCrispWhite, fontSize = 13.sp)
                        Text("$${String.format("%.2f", femMoney)} USD", fontWeight = FontWeight.Bold, color = SportGold, fontSize = 13.sp)
                    }
                    Divider(color = TextMutedGray.copy(alpha = 0.05f))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Mixto ($xP%)", color = TextCrispWhite, fontSize = 13.sp)
                        Text("$${String.format("%.2f", mixMoney)} USD", fontWeight = FontWeight.Bold, color = SportGold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// Helper functions
fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Superliga Voleibol Code", text)
    clipboard.setPrimaryClip(clip)
}

@Composable
fun SimCategorias(viewModel: TournamentViewModel) {
    val categories by viewModel.allCategories.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("Femenino") }
    var newCatName by remember { mutableStateOf("") }

    var editingCategory by remember { mutableStateOf<TournamentCategory?>(null) }
    var editCatName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Categorías Disponibles", color = TextCrispWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${categories.size} Categorías Configuradas", color = TextMutedGray, fontSize = 12.sp)
            }
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    selectedGender = "Femenino"
                    newCatName = ""
                    showAddDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Category", modifier = Modifier.size(16.dp), tint = SportDarkBg)
                Spacer(Modifier.width(4.dp))
                Text("Añadir", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SportDarkBg)
            }
        }

        if (categories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Text("No hay categorías creadas.", color = TextMutedGray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val groupedByGender = categories.groupBy { it.gender }
                groupedByGender.forEach { (gender, cats) ->
                    item {
                        Text(
                            text = gender.uppercase(),
                            fontWeight = FontWeight.Black,
                            color = SportGold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(cats) { category ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = category.name,
                                    color = TextCrispWhite,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            editingCategory = category
                                            editCatName = category.name
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = SportBlue, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteCategory(category) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Nueva Categoría", color = SportGold, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Género / Tipo", color = TextMutedGray, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Femenino", "Masculino", "Mixto").forEach { gen ->
                            val isSel = gen == selectedGender
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) SportGold else SportDarkSurface)
                                    .clickable { selectedGender = gen }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(gen, color = if (isSel) SportDarkBg else TextCrispWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    TextField(
                        value = newCatName,
                        onValueChange = { newCatName = it },
                        label = { Text("Nombre de la Categoría") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                    onClick = {
                        if (newCatName.isNotBlank()) {
                            viewModel.addCategory(selectedGender, newCatName.trim())
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Añadir", color = SportDarkBg)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar", color = TextMutedGray)
                }
            },
            containerColor = SportDarkSurface
        )
    }

    // Edit Dialog
    if (editingCategory != null) {
        AlertDialog(
            onDismissRequest = { editingCategory = null },
            title = { Text("Editar Categoría", color = SportGold, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Género: ${editingCategory?.gender}", color = TextMutedGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    TextField(
                        value = editCatName,
                        onValueChange = { editCatName = it },
                        label = { Text("Nombre de la Categoría") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = SportGold),
                    onClick = {
                        if (editCatName.isNotBlank() && editingCategory != null) {
                            viewModel.updateCategory(editingCategory!!.copy(name = editCatName.trim()))
                            editingCategory = null
                        }
                    }
                ) {
                    Text("Guardar", color = SportDarkBg)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCategory = null }) {
                    Text("Cancelar", color = TextMutedGray)
                }
            },
            containerColor = SportDarkSurface
        )
    }
}

@Composable
fun MainConsolidatedDashboard(
    athletes: List<Athlete>,
    teams: List<Team>,
    matches: List<ScheduledMatch>,
    settings: TournamentSettings
) {
    val totalInscritos = athletes.size
    val costPerAthlete = 50.0
    val totalRecaudadoInscripcion = totalInscritos * costPerAthlete

    val totalMatches = matches.size
    val paidMatches = matches.filter { it.isPaid }
    val unpaidMatches = matches.filter { !it.isPaid }

    val totalGastoArbitraje = matches.sumOf { it.totalPaid }
    val gastoArbitrajePagado = paidMatches.sumOf { it.totalPaid }
    val gastoArbitrajePendiente = unpaidMatches.sumOf { it.totalPaid }

    val totalSurplus = totalRecaudadoInscripcion - totalGastoArbitraje
    val realSpentBalance = totalRecaudadoInscripcion - gastoArbitrajePagado

    // Breakdowns
    val mascCount = athletes.count { it.category.equals("Masculino", ignoreCase = true) }
    val femCount = athletes.count { it.category.equals("Femenino", ignoreCase = true) }
    val mixCount = athletes.count { it.category.equals("Mixto", ignoreCase = true) }

    val totalPaidCount = paidMatches.size
    val distinctClubsCount = athletes.map { it.club }.distinct().filter { it.isNotBlank() }.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SportDarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header & Concept explanation
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        border = BorderStroke(1.dp, SportGold.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumen Ejecutivo de Finanzas",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = SportGold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Consolidado total de deportistas, ingresos generados por inscripción y balance de egresos por arbitraje diario.",
                        fontSize = 12.sp,
                        color = TextMutedGray,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Row of Two KPI Cards: Inscritos & Recaudación Inscriptions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Atletas Inscritos Card
                Card(
                     colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                     shape = RoundedCornerShape(12.dp),
                     modifier = Modifier.weight(1f)
                ) {
                     Column(modifier = Modifier.padding(14.dp)) {
                         Row(
                             verticalAlignment = Alignment.CenterVertically,
                             horizontalArrangement = Arrangement.SpaceBetween,
                             modifier = Modifier.fillMaxWidth()
                         ) {
                             Icon(
                                 imageVector = Icons.Default.Person,
                                 contentDescription = null,
                                 tint = SportBlue,
                                 modifier = Modifier.size(24.dp)
                             )
                             Box(
                                 modifier = Modifier
                                     .background(SportBlue.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                     .padding(horizontal = 6.dp, vertical = 2.dp)
                             ) {
                                 Text(
                                     "Atletas",
                                     color = SportBlue,
                                     fontSize = 10.sp,
                                     fontWeight = FontWeight.Black
                                 )
                             }
                         }

                         Spacer(Modifier.height(12.dp))

                         Text(
                             text = "$totalInscritos",
                             fontSize = 28.sp,
                             fontWeight = FontWeight.Black,
                             color = TextCrispWhite
                         )

                         Text(
                             text = "Deportistas registrados",
                             fontSize = 11.sp,
                             color = TextMutedGray
                         )

                         Spacer(Modifier.height(8.dp))
                         Divider(color = TextMutedGray.copy(alpha = 0.1f))
                         Spacer(Modifier.height(8.dp))

                         // Sub Breakdown
                         Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                             Row(
                                 modifier = Modifier.fillMaxWidth(),
                                 horizontalArrangement = Arrangement.SpaceBetween
                             ) {
                                 Text("Masc:", color = TextMutedGray, fontSize = 10.sp)
                                 Text("$mascCount", color = TextCrispWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                             }
                             Row(
                                 modifier = Modifier.fillMaxWidth(),
                                 horizontalArrangement = Arrangement.SpaceBetween
                             ) {
                                 Text("Fem:", color = TextMutedGray, fontSize = 10.sp)
                                 Text("$femCount", color = TextCrispWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                             }
                             Row(
                                 modifier = Modifier.fillMaxWidth(),
                                 horizontalArrangement = Arrangement.SpaceBetween
                             ) {
                                 Text("Mix:", color = TextMutedGray, fontSize = 10.sp)
                                 Text("$mixCount", color = TextCrispWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                             }
                         }
                     }
                }

                // Recaudación Inscriptions Card
                Card(
                     colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                     shape = RoundedCornerShape(12.dp),
                     modifier = Modifier.weight(1f)
                ) {
                     Column(modifier = Modifier.padding(14.dp)) {
                         Row(
                             verticalAlignment = Alignment.CenterVertically,
                             horizontalArrangement = Arrangement.SpaceBetween,
                             modifier = Modifier.fillMaxWidth()
                         ) {
                             Icon(
                                 imageVector = Icons.Default.CheckCircle,
                                 contentDescription = null,
                                 tint = Color.Green.copy(alpha = 0.8f),
                                 modifier = Modifier.size(24.dp)
                             )
                             Box(
                                 modifier = Modifier
                                     .background(Color.Green.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                     .padding(horizontal = 6.dp, vertical = 2.dp)
                             ) {
                                 Text(
                                     "Ingresos",
                                     color = Color.Green.copy(alpha = 0.8f),
                                     fontSize = 10.sp,
                                     fontWeight = FontWeight.Black
                                 )
                             }
                         }

                         Spacer(Modifier.height(12.dp))

                         Text(
                             text = "$${String.format("%.2f", totalRecaudadoInscripcion)}",
                             fontSize = 28.sp,
                             fontWeight = FontWeight.Black,
                             color = SportGold
                         )

                         Text(
                             text = "Monto por inscripciones",
                             fontSize = 11.sp,
                             color = TextMutedGray
                         )

                         Spacer(Modifier.height(8.dp))
                         Divider(color = TextMutedGray.copy(alpha = 0.1f))
                         Spacer(Modifier.height(8.dp))

                         Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                             Text("Fórmula:", color = TextMutedGray, fontSize = 10.sp)
                             Text("$totalInscritos Atletas × $50.00 USD", color = TextCrispWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                             Text("Estado: Recaudación 100% verificada", color = TextMutedGray, fontSize = 9.sp)
                         }
                     }
                }
            }
        }

        // Gasto en Arbitraje Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = SportOrange,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Gastos Acumulados de Arbitraje",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextCrispWhite
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(SportOrange.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "Egresos",
                                color = SportOrange,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                             )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Gasto Total (Programado)", color = TextMutedGray, fontSize = 11.sp)
                            Text(
                                text = "$${String.format("%.2f", totalGastoArbitraje)} USD",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = TextCrispWhite
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Juegos Programados", color = TextMutedGray, fontSize = 11.sp)
                            Text(
                                text = "$totalMatches partidos",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SportBlue
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Divider(color = TextMutedGray.copy(alpha = 0.1f))
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.Green.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Cobrado / Pagado:", color = TextMutedGray, fontSize = 11.sp)
                            }
                            Text(
                                text = "$${String.format("%.2f", gastoArbitrajePagado)} USD",
                                color = Color.Green.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$totalPaidCount partidos liquidados",
                                color = TextMutedGray,
                                fontSize = 10.sp
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(SportOrange, RoundedCornerShape(4.dp))
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Pendiente por Pagar:", color = TextMutedGray, fontSize = 11.sp)
                            }
                            Text(
                                text = "$${String.format("%.2f", gastoArbitrajePendiente)} USD",
                                color = SportOrange,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${totalMatches - totalPaidCount} partidos pendientes",
                                color = TextMutedGray,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Financial Balance Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        border = BorderStroke(1.dp, SportBlue.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Balance Financiero General",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextCrispWhite
                    )
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Presupuesto Neto Disponible (Físico)", color = TextMutedGray, fontSize = 11.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$${String.format("%.2f", realSpentBalance)} USD",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Green.copy(alpha = 0.8f)
                                )
                            }
                            Text("Ingresos Inscriptions minus Arbitrajes Pagados", color = TextMutedGray, fontSize = 10.sp)
                        }

                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SportBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(Modifier.height(14.dp))
                    Divider(color = TextMutedGray.copy(alpha = 0.1f))
                    Spacer(Modifier.height(14.dp))

                    // Gasto versus Recaudacion bar
                    val usagePercent = if (totalRecaudadoInscripcion > 0) {
                        (totalGastoArbitraje / totalRecaudadoInscripcion).coerceIn(0.0, 1.3)
                    } else 0.0

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Proporción de gasto en Arbitraje sobre Inscripciones",
                                color = TextMutedGray,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${String.format("%.1f", usagePercent * 100)}%",
                                color = if (usagePercent > 0.5) SportOrange else SportBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        // Linear progress bar representation using Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(SportDarkBg)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction = usagePercent.toFloat())
                                    .background(if (usagePercent > 0.5) SportOrange else SportBlue)
                            )
                        }

                        Text(
                            text = "Presupuesto remanente proyectado (post todos los arbitrajes): $${String.format("%.2f", totalSurplus)} USD",
                            color = TextMutedGray,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Additional Stats Details
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SportDarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Información Adicional de la Liga",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportGold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(SportDarkBg, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text("Equipos", color = TextMutedGray, fontSize = 11.sp)
                            Text("${teams.size}", color = TextCrispWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Inscritos", color = TextMutedGray, fontSize = 10.sp)
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(SportDarkBg, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text("Clubs Activos", color = TextMutedGray, fontSize = 11.sp)
                            Text("$distinctClubsCount", color = TextCrispWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Registrados", color = TextMutedGray, fontSize = 10.sp)
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(SportDarkBg, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text("Edad Promedio", color = TextMutedGray, fontSize = 11.sp)
                            val avgAge = if (athletes.isNotEmpty()) athletes.map { it.age }.average() else 0.0
                            Text("${String.format("%.1f", avgAge)}", color = TextCrispWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("años", color = TextMutedGray, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
