package com.velvexlabs.tinytools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.velvexlabs.tinytools.data.FavoriteEntity
import com.velvexlabs.tinytools.data.PreferencesRepository
import com.velvexlabs.tinytools.data.ThemeChoice
import com.velvexlabs.tinytools.data.TinyToolsDatabase
import com.velvexlabs.tinytools.domain.CalculatorEngine
import com.velvexlabs.tinytools.domain.PercentageEngine
import com.velvexlabs.tinytools.domain.ConversionEngine
import com.velvexlabs.tinytools.domain.DateEngine
import com.velvexlabs.tinytools.domain.TextToolsEngine
import com.velvexlabs.tinytools.domain.CaseMode
import com.velvexlabs.tinytools.domain.RandomToolsEngine
import com.velvexlabs.tinytools.domain.QrEngine
import com.velvexlabs.tinytools.domain.ColorEngine
import com.velvexlabs.tinytools.model.ToolDefinition
import com.velvexlabs.tinytools.model.tinyToolCatalog
import com.velvexlabs.tinytools.ui.theme.TinyToolsTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TinyToolsApp() }
    }
}

@Composable
private fun TinyToolsApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { TinyToolsDatabase.get(context) }
    val preferences = remember { PreferencesRepository(context) }
    val themeChoice by preferences.theme.collectAsState(initial = ThemeChoice.SYSTEM)
    val favorites by database.favoriteDao().observeAll().collectAsState(initial = emptyList())
    val favoriteIds = favorites.map { it.toolId }
    val navController = rememberNavController()

    TinyToolsTheme(themeChoice) {
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("home") {
                HomeScreen(
                    favoriteIds = favoriteIds,
                    onTool = { navController.navigate(it) },
                    onFavorites = { navController.navigate("favorites") },
                    onSettings = { navController.navigate("settings") },
                    onToggleFavorite = { toolId ->
                        scope.launch {
                            if (toolId in favoriteIds) database.favoriteDao().delete(toolId)
                            else database.favoriteDao().insert(FavoriteEntity(toolId, favoriteIds.size))
                        }
                    }
                )
            }
            composable("favorites") {
                PageScaffold(navController, "Favorites") {
                    FavoritesScreen(
                        favoriteIds = favoriteIds,
                        onTool = { navController.navigate(it) },
                        onToggleFavorite = { toolId -> scope.launch { database.favoriteDao().delete(toolId) } }
                    )
                }
            }
            composable("settings") {
                PageScaffold(navController, "Settings") {
                    SettingsScreen(themeChoice) { scope.launch { preferences.setTheme(it) } }
                }
            }
            composable("calculator") { ToolScaffold(navController, "Calculator") { CalculatorScreen() } }
            composable("percentage") { ToolScaffold(navController, "Percentage") { PercentageScreen() } }
            composable("tip") { ToolScaffold(navController, "Tip") { TipScreen() } }
            composable("split_bill") { ToolScaffold(navController, "Split Bill") { SplitBillScreen() } }
            composable("units") { ToolScaffold(navController, "Units") { UnitConverterScreen() } }
            composable("time") { ToolScaffold(navController, "Time") { TimeConverterScreen() } }
            composable("age") { ToolScaffold(navController, "Age") { AgeCalculatorScreen() } }
            composable("date_difference") { ToolScaffold(navController, "Date Difference") { DateDifferenceScreen() } }
            composable("countdown") { ToolScaffold(navController, "Countdown") { CountdownScreen() } }
            composable("count") { ToolScaffold(navController, "Count") { TextCounterScreen() } }
            composable("clean") { ToolScaffold(navController, "Clean") { TextCleanerScreen() } }
            composable("case") { ToolScaffold(navController, "Case") { CaseConverterScreen() } }
            composable("random") { ToolScaffold(navController, "Random Picker") { RandomPickerScreen() } }
            composable("decision") { ToolScaffold(navController, "Decision Maker") { DecisionMakerScreen() } }
            composable("qr") { ToolScaffold(navController, "QR Code") { QrCodeScreen() } }
            composable("color") { ToolScaffold(navController, "Color") { ColorScreen() } }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PageScaffold(navController: NavHostController, title: String, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolScaffold(navController: NavHostController, title: String, content: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(Modifier.padding(padding)) { content() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    favoriteIds: List<String>,
    onTool: (String) -> Unit,
    onFavorites: () -> Unit,
    onSettings: () -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filtered = remember(query) {
        val normalized = query.trim().lowercase(Locale.getDefault())
        if (normalized.isBlank()) tinyToolCatalog else tinyToolCatalog.filter {
            it.title.lowercase(Locale.getDefault()).contains(normalized) ||
                it.aliases.any { alias -> alias.lowercase(Locale.getDefault()).contains(normalized) }
        }
    }
    val grouped = filtered.groupBy { it.category }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Tiny Tools", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                actions = {
                    IconButton(onClick = onFavorites) {
                        Icon(Icons.Filled.Star, contentDescription = "Favorites")
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text("Velvex Labs • spdedsec", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(18.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Search tools" },
                    singleLine = true,
                    label = { Text("Search tools") },
                    placeholder = { Text("Try percent, kg, or age") },
                    leadingIcon = { Icon(Icons.Outlined.Tune, contentDescription = null) }
                )
                Spacer(Modifier.height(20.dp))
            }
            if (favoriteIds.isNotEmpty() && query.isBlank()) {
                item { SectionLabel("FAVORITES") }
                items(tinyToolCatalog.filter { it.id in favoriteIds }) { tool ->
                    ToolRow(tool, true, onTool, onToggleFavorite)
                }
                item { Spacer(Modifier.height(10.dp)) }
            }
            grouped.forEach { (category, tools) ->
                item { SectionLabel(category) }
                items(tools) { tool -> ToolRow(tool, tool.id in favoriteIds, onTool, onToggleFavorite) }
            }
            if (filtered.isEmpty()) {
                item {
                    Text("No tools found.", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 32.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp, bottom = 2.dp))
}

@Composable
private fun ToolRow(tool: ToolDefinition, favorite: Boolean, onTool: (String) -> Unit, onToggleFavorite: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onTool(tool.id) },
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(tool.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(14.dp))
            Text(tool.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            IconButton(onClick = { onToggleFavorite(tool.id) }) {
                Icon(if (favorite) Icons.Filled.Star else Icons.Outlined.StarBorder, contentDescription = if (favorite) "Remove favorite" else "Add favorite", tint = if (favorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun FavoritesScreen(favoriteIds: List<String>, onTool: (String) -> Unit, onToggleFavorite: (String) -> Unit) {
    val tools = tinyToolCatalog.filter { it.id in favoriteIds }
    if (tools.isEmpty()) {
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
            Text("No favorites yet.", style = MaterialTheme.typography.headlineMedium)
            Text("Add tools you use often.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
        }
    } else {
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(tools) { ToolRow(it, true, onTool, onToggleFavorite) }
        }
    }
}

@Composable
private fun SettingsScreen(themeChoice: ThemeChoice, onTheme: (ThemeChoice) -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text("APPEARANCE", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text("Theme", style = MaterialTheme.typography.titleLarge)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeChoice.entries.forEach { choice ->
                FilterChip(selected = themeChoice == choice, onClick = { onTheme(choice) }, label = { Text(choice.name.lowercase().replaceFirstChar { it.titlecase() }) }, leadingIcon = {
                    Icon(when (choice) { ThemeChoice.LIGHT -> Icons.Outlined.LightMode; ThemeChoice.DARK -> Icons.Outlined.DarkMode; ThemeChoice.SYSTEM -> Icons.Outlined.Tune }, contentDescription = null)
                })
            }
        }
        Divider(Modifier.padding(vertical = 4.dp))
        Text("ABOUT", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text("Tiny Tools", style = MaterialTheme.typography.titleLarge)
        Text("Velvex Labs • spdedsec", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Version 0.1.0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CalculatorScreen() {
    var expression by rememberSaveable { mutableStateOf("") }
    var result by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf("") }
    val history = remember { mutableStateListOf<String>() }
    val context = androidx.compose.ui.platform.LocalContext.current

    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Fast arithmetic, no friction.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.End) {
                Text(if (expression.isBlank()) "0" else expression, style = MaterialTheme.typography.headlineMedium)
                Text(if (result.isBlank()) "" else result, style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 10.dp))
            }
        }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        val rows = listOf(listOf("C", "(", ")", "÷"), listOf("7", "8", "9", "×"), listOf("4", "5", "6", "−"), listOf("1", "2", "3", "+"), listOf(".", "0", "%", "="))
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { key ->
                    Button(
                        onClick = {
                            error = ""
                            when (key) {
                                "C" -> { expression = ""; result = "" }
                                "=" -> runCatching {
                                    val normalized = expression.replace("÷", "/").replace("×", "*").replace("−", "-")
                                    CalculatorEngine.evaluate(normalized)
                                }.onSuccess { value ->
                                    result = CalculatorEngine.format(value)
                                    history.add(0, "$expression = $result")
                                }.onFailure { error = it.message ?: "Enter a valid calculation." }
                                else -> expression += key
                            }
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (key == "=") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, contentColor = if (key == "=") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                    ) { Text(key, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                }
            }
        }
        if (result.isNotBlank()) OutlinedButton(onClick = { copyToClipboard(context, result) }, modifier = Modifier.fillMaxWidth()) { Text("Copy result") }
        if (history.isNotEmpty()) {
            Text("HISTORY", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
            history.take(3).forEach { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}

enum class PercentageOperation(val label: String) { OF("Percentage of"), WHAT("What percentage?"), INCREASE("Increase"), DECREASE("Decrease"), DISCOUNT("Discount"), MARKUP("Markup"), REVERSE("Reverse") }

@Composable
private fun PercentageScreen() {
    var operation by rememberSaveable { mutableStateOf(PercentageOperation.OF.name) }
    var first by rememberSaveable { mutableStateOf("") }
    var second by rememberSaveable { mutableStateOf("") }
    var result by rememberSaveable { mutableStateOf("") }
    var detail by remember { mutableStateOf<List<Pair<String, Double>>>(emptyList()) }
    var error by rememberSaveable { mutableStateOf("") }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Common percentage operations, kept clear.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PercentageOperation.entries.forEach { item -> FilterChip(selected = operation == item.name, onClick = { operation = item.name; result = ""; error = "" }, label = { Text(item.label) }) }
        }
        val current = PercentageOperation.valueOf(operation)
        NumberField(if (current == PercentageOperation.WHAT || current == PercentageOperation.OF) "First value" else if (current == PercentageOperation.REVERSE) "Final value" else "Value", first) { first = it }
        NumberField(if (current == PercentageOperation.WHAT) "Whole value" else "Percentage", second) { second = it }
        Button(onClick = {
            error = ""
            runCatching {
                val a = first.toDoubleOrNull() ?: error("Enter a valid number.")
                val b = second.toDoubleOrNull() ?: error("Enter a valid number.")
                when (current) {
                    PercentageOperation.OF -> PercentageEngine.percentageOf(a, b)
                    PercentageOperation.WHAT -> PercentageEngine.whatPercentage(a, b)
                    PercentageOperation.INCREASE -> PercentageEngine.increase(a, b)
                    PercentageOperation.DECREASE -> PercentageEngine.decrease(a, b)
                    PercentageOperation.DISCOUNT -> PercentageEngine.discount(a, b)
                    PercentageOperation.MARKUP -> PercentageEngine.markup(a, b)
                    PercentageOperation.REVERSE -> PercentageEngine.reverse(a, b)
                }
            }.onSuccess { calculated -> result = CalculatorEngine.format(calculated.primary); detail = calculated.detail }.onFailure { error = it.message ?: "Enter valid values." }
        }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text("Calculate") }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        if (result.isNotBlank()) ResultBlock(result, detail)
    }
}

@Composable
private fun TipScreen() {
    var bill by rememberSaveable { mutableStateOf("") }
    var tip by rememberSaveable { mutableStateOf("15") }
    var people by rememberSaveable { mutableStateOf("2") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("A quick, clean split for a bill.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        NumberField("Bill amount", bill) { bill = it }
        NumberField("Tip percentage", tip) { tip = it }
        NumberField("People", people) { people = it }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("10", "15", "18", "20").forEach { preset -> FilterChip(selected = tip == preset, onClick = { tip = preset }, label = { Text("$preset%") }) }
        }
        val billValue = bill.toDoubleOrNull()
        val tipValue = tip.toDoubleOrNull()
        val peopleValue = people.toDoubleOrNull()?.takeIf { it > 0 }
        if (billValue != null && tipValue != null && peopleValue != null) {
            val tipAmount = billValue * tipValue / 100
            ResultBlock(CalculatorEngine.format(tipAmount + billValue), listOf("Tip" to tipAmount, "Per person" to (billValue + tipAmount) / peopleValue))
        }
    }
}

@Composable
private fun SplitBillScreen() {
    var total by rememberSaveable { mutableStateOf("") }
    var people by rememberSaveable { mutableStateOf("2") }
    var tax by rememberSaveable { mutableStateOf("") }
    var tip by rememberSaveable { mutableStateOf("") }
    var customMode by rememberSaveable { mutableStateOf(false) }
    var customAmounts by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Equal split by default. Add detail only when needed.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        NumberField("Total bill", total) { total = it }
        NumberField("People", people) { people = it }
        NumberField("Tax percentage (optional)", tax) { tax = it }
        NumberField("Tip percentage (optional)", tip) { tip = it }
        FilterChip(selected = customMode, onClick = { customMode = !customMode }, label = { Text("Custom individual amounts") })
        if (customMode) OutlinedTextField(value = customAmounts, onValueChange = { customAmounts = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Amounts, separated by commas") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), minLines = 2)
        val base = total.toDoubleOrNull()
        val peopleValue = people.toDoubleOrNull()?.takeIf { it > 0 }
        val taxValue = tax.toDoubleOrNull() ?: 0.0
        val tipValue = tip.toDoubleOrNull() ?: 0.0
        if (base != null && peopleValue != null) {
            val calculatedTotal = base * (1 + (taxValue + tipValue) / 100)
            val custom = customAmounts.split(",").mapNotNull { it.trim().toDoubleOrNull() }
            val perPerson = if (customMode && custom.isNotEmpty()) custom else listOf(calculatedTotal / peopleValue)
            ResultBlock(CalculatorEngine.format(calculatedTotal), listOf("Per person" to perPerson.first(), "Tax + tip" to calculatedTotal - base))
        }
    }
}

@Composable
private fun NumberField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), label = { Text(label) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
}

@Composable
private fun ResultBlock(primary: String, detail: List<Pair<String, Double>>) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(primary, style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
            detail.forEach { (label, value) -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(CalculatorEngine.format(value), fontWeight = FontWeight.Bold) } }
        }
    }
}

@Composable
private fun PlaceholderTool(message: String) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Center) { Text(message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant) }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(ClipboardManager::class.java)
    clipboard?.setPrimaryClip(ClipData.newPlainText("Tiny Tools result", text))
    Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
}

@Composable
private fun UnitConverterScreen() {
    var groupIndex by rememberSaveable { mutableStateOf(0) }
    var fromIndex by rememberSaveable { mutableStateOf(0) }
    var toIndex by rememberSaveable { mutableStateOf(1) }
    var value by rememberSaveable { mutableStateOf("") }
    val group = ConversionEngine.groups[groupIndex]
    val from = group.units[fromIndex.coerceIn(group.units.indices)]
    val to = group.units[toIndex.coerceIn(group.units.indices)]

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Choose a category, then convert instantly.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ConversionEngine.groups.forEachIndexed { index, item ->
                FilterChip(selected = groupIndex == index, onClick = { groupIndex = index; fromIndex = 0; toIndex = 1.coerceAtMost(item.units.lastIndex) }, label = { Text(item.name) })
            }
        }
        NumberField("Value", value) { value = it }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            UnitPicker("From", from.label, { fromIndex = (fromIndex + 1) % group.units.size }, Modifier.weight(1f))
            UnitPicker("To", to.label, { toIndex = (toIndex + 1) % group.units.size }, Modifier.weight(1f))
        }
        val converted = value.toDoubleOrNull()?.let { runCatching { ConversionEngine.convert(it, from, to) }.getOrNull() }
        if (converted != null) ResultBlock(CalculatorEngine.format(converted), listOf("${from.symbol} → ${to.symbol}" to converted))
    }
}

@Composable
private fun UnitPicker(label: String, selected: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(onClick = onClick, modifier = modifier.height(58.dp), contentPadding = PaddingValues(horizontal = 10.dp)) {
        Column(horizontalAlignment = Alignment.Start) { Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary); Text(selected, maxLines = 1) }
    }
}

@Composable
private fun TimeConverterScreen() {
    val units = listOf("seconds", "minutes", "hours", "days", "weeks")
    val factors = listOf(1.0, 60.0, 3600.0, 86400.0, 604800.0)
    var input by rememberSaveable { mutableStateOf("") }
    var fromIndex by rememberSaveable { mutableStateOf(1) }
    var toIndex by rememberSaveable { mutableStateOf(0) }
    var error by rememberSaveable { mutableStateOf("") }
    val from = units[fromIndex]
    val to = units[toIndex]
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Convert time units or enter a practical duration such as 2h 30m.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(input, { input = it; error = "" }, Modifier.fillMaxWidth(), label = { Text("Duration or value") }, singleLine = true)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            UnitPicker("From", from, { fromIndex = (fromIndex + 1) % units.size }, Modifier.weight(1f))
            UnitPicker("To", to, { toIndex = (toIndex + 1) % units.size }, Modifier.weight(1f))
        }
        Button(onClick = { error = ""; runCatching {
            if (input.any { it.isLetter() }) ConversionEngine.parseDuration(input).toDouble() / factors[toIndex]
            else (input.toDoubleOrNull() ?: error("Enter a valid duration.")) * factors[fromIndex] / factors[toIndex]
        }.onSuccess { error = "" }.onFailure { error = it.message ?: "Enter a valid duration." } }, modifier = Modifier.fillMaxWidth()) { Text("Convert") }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        if (input.isNotBlank()) {
            val shown = runCatching {
                if (input.any { it.isLetter() }) ConversionEngine.parseDuration(input).toDouble() / factors[toIndex]
                else (input.toDoubleOrNull() ?: 0.0) * factors[fromIndex] / factors[toIndex]
            }.getOrNull()
            if (shown != null) ResultBlock(CalculatorEngine.format(shown), listOf(to to shown))
        }
    }
}

@Composable
private fun AgeCalculatorScreen() {
    var birth by rememberSaveable { mutableStateOf("") }
    var onDate by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var error by rememberSaveable { mutableStateOf("") }
    var ageResult by remember { mutableStateOf<AgeResult?>(null) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Use YYYY-MM-DD. The result stays local.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(birth, { birth = it; error = "" }, Modifier.fillMaxWidth(), label = { Text("Date of birth") }, singleLine = true)
        OutlinedTextField(onDate, { onDate = it; error = "" }, Modifier.fillMaxWidth(), label = { Text("Calculate on (optional)") }, singleLine = true)
        Button(onClick = { error = ""; runCatching {
            val birthDate = DateEngine.parseDate(birth)
            val selected = DateEngine.parseDate(onDate)
            val period = DateEngine.age(birthDate, selected)
            val next = DateEngine.nextBirthday(birthDate, selected)
            AgeResult(period.years, period.months, period.days, next, java.time.temporal.ChronoUnit.DAYS.between(selected, next))
        }.onSuccess { calculatedAge -> ageResult = calculatedAge }.onFailure { error = it.message ?: "Choose valid dates." } }, modifier = Modifier.fillMaxWidth()) { Text("Calculate") }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        ageResult?.let { result -> ResultBlock("${result.years} years", listOf("Months" to result.months.toDouble(), "Days" to result.days.toDouble(), "Days until next birthday" to result.daysUntilBirthday.toDouble())) }
    }
}

private data class AgeResult(val years: Int, val months: Int, val days: Int, val nextBirthday: LocalDate, val daysUntilBirthday: Long)

@Composable
private fun DateDifferenceScreen() {
    var first by rememberSaveable { mutableStateOf("") }
    var second by rememberSaveable { mutableStateOf("") }
    var addDays by rememberSaveable { mutableStateOf("") }
    var result by remember { mutableStateOf<com.velvexlabs.tinytools.domain.DateDifference?>(null) }
    var shifted by remember { mutableStateOf<LocalDate?>(null) }
    var error by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Compare two dates or shift a date by days.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(first, { first = it; error = "" }, Modifier.fillMaxWidth(), label = { Text("First date (YYYY-MM-DD)") }, singleLine = true)
        OutlinedTextField(second, { second = it; error = "" }, Modifier.fillMaxWidth(), label = { Text("Second date (YYYY-MM-DD)") }, singleLine = true)
        Button(onClick = { error = ""; runCatching { DateEngine.dateDifference(DateEngine.parseDate(first), DateEngine.parseDate(second)) }.onSuccess { result = it }.onFailure { error = it.message ?: "Choose valid dates." } }, modifier = Modifier.fillMaxWidth()) { Text("Compare") }
        result?.let { ResultBlock("${it.totalDays} days", listOf("Years" to it.period.years.toDouble(), "Months" to it.period.months.toDouble(), "Days" to it.period.days.toDouble(), "Weeks" to (it.totalDays / 7.0))) }
        Divider()
        OutlinedTextField(addDays, { addDays = it; error = "" }, Modifier.fillMaxWidth(), label = { Text("Days to add or subtract") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Button(onClick = { error = ""; runCatching { DateEngine.parseDate(first).plusDays(addDays.toLongOrNull() ?: error("Enter a whole number.")) }.onSuccess { shifted = it }.onFailure { error = it.message ?: "Choose a date and number of days." } }, modifier = Modifier.fillMaxWidth()) { Text("Shift date") }
        shifted?.let { Text("Result: $it", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary) }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun CountdownScreen() {
    var name by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var time by rememberSaveable { mutableStateOf("00:00") }
    var result by remember { mutableStateOf<com.velvexlabs.tinytools.domain.CountdownResult?>(null) }
    var error by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("A useful countdown without an account or server.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Name") }, singleLine = true)
        OutlinedTextField(date, { date = it; error = "" }, Modifier.fillMaxWidth(), label = { Text("Date (YYYY-MM-DD)") }, singleLine = true)
        OutlinedTextField(time, { time = it; error = "" }, Modifier.fillMaxWidth(), label = { Text("Time (HH:MM)") }, singleLine = true)
        Button(onClick = { error = ""; runCatching { DateEngine.countdown(LocalDateTime.of(DateEngine.parseDate(date), LocalTime.parse(time))) }.onSuccess { result = it }.onFailure { error = it.message ?: "Choose a valid date and time." } }, modifier = Modifier.fillMaxWidth()) { Text("Calculate") }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        result?.let { countdown ->
            if (countdown.active) ResultBlock("${countdown.days} days", listOf("Hours" to countdown.hours.toDouble(), "Minutes" to countdown.minutes.toDouble(), "Seconds" to countdown.seconds.toDouble()))
            else Text("The date has passed.", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TextCounterScreen() {
    var text by rememberSaveable { mutableStateOf("") }
    val stats = TextToolsEngine.count(text)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Counts stay visible while you type.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth().height(260.dp), label = { Text("Paste or type text") }, minLines = 8)
        ResultBlock("${stats.words} words", listOf("Characters" to stats.characters.toDouble(), "No spaces" to stats.charactersNoSpaces.toDouble(), "Lines" to stats.lines.toDouble(), "Paragraphs" to stats.paragraphs.toDouble()))
    }
}

@Composable
private fun TextCleanerScreen() {
    var text by rememberSaveable { mutableStateOf("") }
    var trimLines by rememberSaveable { mutableStateOf(true) }
    var collapseSpaces by rememberSaveable { mutableStateOf(true) }
    var removeBlankLines by rememberSaveable { mutableStateOf(false) }
    var deduplicateLines by rememberSaveable { mutableStateOf(false) }
    var cleaned by rememberSaveable { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Make pasted text clean without changing its meaning.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth().height(210.dp), label = { Text("Text to clean") }, minLines = 7)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(trimLines, { trimLines = !trimLines }, label = { Text("Trim lines") })
            FilterChip(collapseSpaces, { collapseSpaces = !collapseSpaces }, label = { Text("Spaces") })
            FilterChip(removeBlankLines, { removeBlankLines = !removeBlankLines }, label = { Text("Blank lines") })
            FilterChip(deduplicateLines, { deduplicateLines = !deduplicateLines }, label = { Text("Duplicates") })
        }
        Button(onClick = { cleaned = TextToolsEngine.clean(text, trimLines, collapseSpaces, removeBlankLines, deduplicateLines) }, modifier = Modifier.fillMaxWidth()) { Text("Clean text") }
        if (cleaned.isNotBlank()) {
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) { Text(cleaned, modifier = Modifier.padding(18.dp), style = MaterialTheme.typography.bodyLarge) }
            OutlinedButton(onClick = { copyToClipboard(context, cleaned) }, modifier = Modifier.fillMaxWidth()) { Text("Copy cleaned text") }
        }
    }
}

@Composable
private fun CaseConverterScreen() {
    var text by rememberSaveable { mutableStateOf("") }
    var modeName by rememberSaveable { mutableStateOf(CaseMode.UPPER.name) }
    val mode = CaseMode.valueOf(modeName)
    val converted = TextToolsEngine.convertCase(text, mode)
    val context = androidx.compose.ui.platform.LocalContext.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Switch between readable and code-friendly cases.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth().height(190.dp), label = { Text("Text") }, minLines = 6)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) { CaseMode.entries.forEach { item -> FilterChip(mode == item, { modeName = item.name }, label = { Text(item.label) }) } }
        if (text.isNotBlank()) {
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) { Text(converted, modifier = Modifier.padding(18.dp), style = MaterialTheme.typography.bodyLarge) }
            OutlinedButton(onClick = { copyToClipboard(context, converted) }, modifier = Modifier.fillMaxWidth()) { Text("Copy result") }
        }
    }
}

@Composable
private fun RandomPickerScreen() {
    var options by rememberSaveable { mutableStateOf("") }
    var picked by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("One option per line or separated by commas.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(options, { options = it; error = "" }, Modifier.fillMaxWidth().height(240.dp), label = { Text("Options") }, minLines = 8)
        Button(onClick = { error = ""; runCatching { RandomToolsEngine.pick(options) }.onSuccess { picked = it }.onFailure { error = it.message ?: "Add options first." } }, modifier = Modifier.fillMaxWidth()) { Text("Pick one") }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        if (picked.isNotBlank()) ResultBlock(picked, emptyList())
    }
}

@Composable
private fun DecisionMakerScreen() {
    var question by rememberSaveable { mutableStateOf("") }
    var decision by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("When the choice is small, make it small.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(question, { question = it }, Modifier.fillMaxWidth(), label = { Text("Question (optional)") }, singleLine = true)
        Button(onClick = { decision = RandomToolsEngine.decide() }, modifier = Modifier.fillMaxWidth()) { Text("Decide") }
        if (decision.isNotBlank()) Text(decision, style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun QrCodeScreen() {
    var input by rememberSaveable { mutableStateOf("") }
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var error by rememberSaveable { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Generate a QR code locally from text or a link.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(input, { input = it; error = "" }, Modifier.fillMaxWidth(), label = { Text("Text or URL") }, minLines = 3)
        Button(onClick = { error = ""; runCatching { QrEngine.generate(input) }.onSuccess { bitmap = it }.onFailure { error = it.message ?: "Enter content first." } }, modifier = Modifier.fillMaxWidth()) { Text("Generate QR") }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth())
        bitmap?.let { generated ->
            Surface(color = androidx.compose.ui.graphics.Color.White, shape = RoundedCornerShape(16.dp), modifier = Modifier.size(280.dp).padding(10.dp)) {
                Image(generated.asImageBitmap(), contentDescription = "Generated QR code", modifier = Modifier.fillMaxSize().padding(10.dp))
            }
            OutlinedButton(onClick = { copyToClipboard(context, input) }, modifier = Modifier.fillMaxWidth()) { Text("Copy encoded content") }
        }
    }
}

@Composable
private fun ColorScreen() {
    var input by rememberSaveable { mutableStateOf("#F28C28") }
    var info by remember { mutableStateOf<com.velvexlabs.tinytools.domain.ColorInfo?>(null) }
    var error by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Inspect a hex color and check readable contrast.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(input, { input = it; error = "" }, Modifier.fillMaxWidth(), label = { Text("Hex color") }, singleLine = true)
        Button(onClick = { error = ""; runCatching { ColorEngine.inspect(input) }.onSuccess { info = it }.onFailure { error = it.message ?: "Enter a valid hex color." } }, modifier = Modifier.fillMaxWidth()) { Text("Inspect color") }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        info?.let { color ->
            Surface(color = androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(color.hex)), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(110.dp)) {}
            ResultBlock(color.hex, listOf("Red" to color.red.toDouble(), "Green" to color.green.toDouble(), "Blue" to color.blue.toDouble(), "Luminance" to color.luminance, "Contrast white" to color.contrastWithWhite, "Contrast black" to color.contrastWithBlack))
            Text("Complementary: ${color.complementary}", style = MaterialTheme.typography.titleMedium)
        }
    }
}
