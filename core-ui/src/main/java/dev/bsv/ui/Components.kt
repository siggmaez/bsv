package dev.bsv.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.bsv.settings.Settings

fun Settings.tr(en: String, ru: String) = if (language == "ru") ru else en

@Composable
fun Panel(title: String, subtitle: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            subtitle?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            content()
        }
    }
}

@Composable
fun Page(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 70.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
    )
}

@Composable
fun Toggle(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(title, Modifier.weight(1f))
        Switch(checked, onChange)
    }
}

@Composable
fun Control(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    change: (Float) -> Unit,
) {
    Text("$label · ${"%.2f".format(value)}", style = MaterialTheme.typography.bodyMedium)
    Slider(value, onValueChange = change, valueRange = range)
}

@Composable
fun Choice(value: String, options: List<String>, change: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        OutlinedButton({ open = true }) { Text(value + " ▾") }
        DropdownMenu(open, { open = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        change(option)
                        open = false
                    },
                )
            }
        }
    }
}

@Composable
fun BsvTheme(s: Settings, content: @Composable () -> Unit) {
    val accent by
        animateColorAsState(
            Color(s.accent),
            tween(if (s.animations) (250 / s.speed).toInt() else 0),
            label = "accent",
        )
    val bg =
        when (s.theme) {
            "AMOLED" -> Color.Black
            "Slate" -> Color(0xFF17202E)
            else -> BsvBrand.Background
        }
    MaterialTheme(
        colorScheme =
            darkColorScheme(
                primary = accent,
                secondary = Color(0xFF6366F1),
                background = bg,
                surface = BsvBrand.Surface,
                surfaceVariant = Color(0xFF23232D),
                onSurface = Color.White,
                onBackground = Color.White,
                onSurfaceVariant = Color(0xFF9898AA),
            ),
        content = {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                content = content,
            )
        },
    )
}
