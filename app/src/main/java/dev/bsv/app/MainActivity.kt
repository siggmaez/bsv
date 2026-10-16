package dev.bsv.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.*
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bsv.appearance.Appearance
import dev.bsv.home.Home
import dev.bsv.profile.Profile
import dev.bsv.settings.*
import dev.bsv.ui.*
import dev.bsv.visuals.Visuals
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent { BsvApp() }
    }
}

@Composable
fun BsvApp(vm: BsvViewModel = viewModel()) {
    val s by vm.settings.collectAsStateWithLifecycle()
    val status by vm.status.collectAsStateWithLifecycle()
    val ready by vm.ready.collectAsStateWithLifecycle()
    if (!ready) {
        BsvTheme(s) { BsvSplash(s.animations) }
        return
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var tab by rememberSaveableState(0)
    var visible by remember { mutableStateOf(true) }
    var reset by remember { mutableStateOf(false) }
    val export =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/json")
        ) { uri ->
            if (uri != null)
                scope.launch {
                    try {
                        val data = SettingsCodec.encode(vm.settings.value)
                        withContext(Dispatchers.IO) {
                            context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use {
                                it.write(data)
                            } ?: error("Cannot open file")
                        }
                        vm.report(s.tr("Exported", "Экспортировано"))
                    } catch (e: Exception) {
                        vm.report("Export error: ${e.message}")
                    }
                }
        }
    val import =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null)
                scope.launch {
                    try {
                        val text =
                            withContext(Dispatchers.IO) {
                                context.contentResolver.openInputStream(uri)?.use { input ->
                                    val out = java.io.ByteArrayOutputStream()
                                    val buffer = ByteArray(4096)
                                    var n = input.read(buffer)
                                    while (n != -1) {
                                        require(out.size() + n <= 65536)
                                        out.write(buffer, 0, n)
                                        n = input.read(buffer)
                                    }
                                    out.toString("UTF-8")
                                } ?: error("Cannot read file")
                            }
                        vm.importJson(text)
                    } catch (e: Exception) {
                        vm.report("Import error: ${e.message}")
                    }
                }
        }
    BsvTheme(s) {
        val density = LocalDensity.current
        CompositionLocalProvider(
            LocalDensity provides Density(density.density * s.scale, density.fontScale)
        ) {
            BoxWithConstraints(
                Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .safeDrawingPadding()
            ) {
                val viewportWidth = maxWidth
                val viewportHeight = maxHeight
                val widthPx = with(LocalDensity.current) { viewportWidth.toPx() }
                val heightPx = with(LocalDensity.current) { viewportHeight.toPx() }
                val buttonPx = with(LocalDensity.current) { s.buttonSize.dp.toPx() }
                val maxX = (widthPx - buttonPx).coerceAtLeast(1f)
                val maxY = (heightPx - buttonPx).coerceAtLeast(1f)
                Box(
                    Modifier.fillMaxSize()
                        .blur(if (Build.VERSION.SDK_INT >= 31) s.blur.dp else 0.dp)
                        .background(
                            androidx.compose.ui.graphics.Brush.radialGradient(
                                listOf(Color(s.accent).copy(alpha = .25f), Color.Transparent)
                            )
                        )
                )
                AnimatedVisibility(
                    visible,
                    enter = fadeIn(tween(if (s.animations) 250 else 0)),
                    exit = fadeOut(tween(if (s.animations) 250 else 0)),
                ) {
                    Column(
                        Modifier.fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.background.copy(alpha = s.opacity)
                            )
                    ) {
                        BrandHeader(s)
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Row(Modifier.weight(1f)) {
                            Column(
                                Modifier.width(if (viewportWidth < 650.dp) 132.dp else 174.dp)
                                    .fillMaxHeight()
                                    .verticalScroll(rememberScrollState())
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                listOf(
                                        s.tr("Home", "Главная"),
                                        s.tr("Profile", "Профиль"),
                                        s.tr("Visuals", "Эффекты"),
                                        s.tr("Interface", "Интерфейс"),
                                        s.tr("Settings", "Настройки"),
                                    )
                                    .forEachIndexed { i, label ->
                                        val color by
                                            animateColorAsState(
                                                if (tab == i)
                                                    MaterialTheme.colorScheme.primary.copy(
                                                        alpha = .22f
                                                    )
                                                else Color.Transparent,
                                                tween(if (s.animations) 220 else 0),
                                                label = "tab",
                                            )
                                        Surface(
                                            onClick = { tab = i },
                                            shape =
                                                androidx.compose.foundation.shape
                                                    .RoundedCornerShape(14.dp),
                                            color = color,
                                            modifier = Modifier.fillMaxWidth().testTag("nav-$i"),
                                        ) {
                                            Text(
                                                "${listOf("⌂","◇","✧","▦","⚙")[i]}  $label",
                                                Modifier.padding(
                                                    vertical = 14.dp,
                                                    horizontal = 10.dp,
                                                ),
                                                color =
                                                    if (tab == i) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                                style = MaterialTheme.typography.labelLarge,
                                            )
                                        }
                                    }
                            }
                            AnimatedContent(
                                tab,
                                modifier = Modifier.weight(1f),
                                transitionSpec = {
                                    val ms = if (s.animations) (240 / s.speed).toInt() else 0
                                    (fadeIn(tween(ms)) +
                                        slideInHorizontally(tween(ms)) { it / 20 }) togetherWith
                                        fadeOut(tween(ms))
                                },
                                label = "section",
                            ) { selected ->
                                when (selected) {
                                    0 -> Home(s) { reset = true }
                                    1 -> Profile(s, vm::update)
                                    2 -> Visuals(s, vm::update)
                                    3 -> Appearance(s, vm::update)
                                    else ->
                                        Page {
                                            Panel(s.tr("Preferences", "Параметры")) {
                                                Text(s.tr("Language", "Язык"))
                                                Choice(
                                                    if (s.language == "ru") "Русский"
                                                    else "English",
                                                    listOf("English", "Русский"),
                                                ) {
                                                    vm.update(
                                                        s.copy(
                                                            language =
                                                                if (it == "Русский") "ru" else "en"
                                                        )
                                                    )
                                                }
                                                Text(
                                                    s.tr(
                                                        "Settings are saved automatically.",
                                                        "Настройки сохраняются автоматически.",
                                                    )
                                                )
                                                Button(vm::save) {
                                                    Text(
                                                        s.tr("Save Settings", "Сохранить настройки")
                                                    )
                                                }
                                                OutlinedButton({
                                                    export.launch("BSV-settings.json")
                                                }) {
                                                    Text(
                                                        s.tr("Export Settings", "Экспорт настроек")
                                                    )
                                                }
                                                OutlinedButton({
                                                    import.launch(
                                                        arrayOf("application/json", "text/plain")
                                                    )
                                                }) {
                                                    Text(s.tr("Import Settings", "Импорт настроек"))
                                                }
                                                TextButton({ reset = true }) {
                                                    Text(s.tr("Reset All Settings", "Сбросить всё"))
                                                }
                                                Text(
                                                    status,
                                                    style = MaterialTheme.typography.bodySmall,
                                                )
                                            }
                                            Panel(s.tr("About BSV", "О BSV")) {
                                                Text(
                                                    s.tr(
                                                        "BSV 0.1.0. Independent UI prototype. Not affiliated with Supercell. No game assets, account access or gameplay modifications.",
                                                        "BSV 0.1.0. Независимый прототип интерфейса. Не связан с Supercell. Без игровых ресурсов, доступа к аккаунту или изменения игры.",
                                                    )
                                                )
                                            }
                                            Panel(
                                                s.tr("Debug Information", "Отладочная информация")
                                            ) {
                                                Text(
                                                    "API ${Build.VERSION.SDK_INT} • ${Build.MODEL}\n${Build.SUPPORTED_ABIS.joinToString()}\nDataStore • schema v1 • local preview\n${viewportWidth.value.toInt()} × ${viewportHeight.value.toInt()} dp"
                                                )
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
                val latest by rememberUpdatedState(s)
                var dragX by remember { mutableFloatStateOf(0f) }
                var dragY by remember { mutableFloatStateOf(0f) }
                Surface(
                    onClick = { visible = !visible },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 10.dp,
                    modifier =
                        Modifier.offset {
                                IntOffset(
                                    (s.buttonX * maxX).roundToInt(),
                                    (s.buttonY * maxY).roundToInt(),
                                )
                            }
                            .size(s.buttonSize.dp)
                            .testTag("bsv-button")
                            .alpha(s.buttonOpacity)
                            .pointerInput(maxX, maxY) {
                                detectDragGestures(
                                    onDragStart = {
                                        dragX = latest.buttonX * maxX
                                        dragY = latest.buttonY * maxY
                                    },
                                    onDragEnd = {
                                        vm.update(
                                            latest.copy(
                                                buttonX = dragX / maxX,
                                                buttonY = dragY / maxY,
                                            )
                                        )
                                    },
                                ) { change, delta ->
                                    change.consume()
                                    dragX = (dragX + delta.x).coerceIn(0f, maxX)
                                    dragY = (dragY + delta.y).coerceIn(0f, maxY)
                                    vm.update(
                                        latest.copy(buttonX = dragX / maxX, buttonY = dragY / maxY)
                                    )
                                }
                            },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("BSV", style = MaterialTheme.typography.labelLarge)
                    }
                }
                if (reset)
                    AlertDialog(
                        onDismissRequest = { reset = false },
                        title = { Text(s.tr("Reset settings?", "Сбросить настройки?")) },
                        text = {
                            Text(
                                s.tr(
                                    "All local preferences will return to defaults.",
                                    "Все локальные параметры вернутся к исходным значениям.",
                                )
                            )
                        },
                        confirmButton = {
                            TextButton({
                                vm.update(Settings())
                                reset = false
                            }) {
                                Text(s.tr("Reset", "Сбросить"))
                            }
                        },
                        dismissButton = {
                            TextButton({ reset = false }) { Text(s.tr("Cancel", "Отмена")) }
                        },
                    )
            }
        }
    }
}

@Composable
private fun rememberSaveableState(value: Int) =
    androidx.compose.runtime.saveable.rememberSaveable { mutableIntStateOf(value) }
