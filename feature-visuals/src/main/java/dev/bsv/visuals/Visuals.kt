package dev.bsv.visuals

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.bsv.settings.*
import dev.bsv.ui.*
import kotlin.math.*
import kotlinx.coroutines.delay

@Composable
fun Visuals(s: Settings, update: (Settings) -> Unit) = Page {
    Text(
        s.tr("Local effect studio", "Студия локальных эффектов"),
        style = MaterialTheme.typography.headlineMedium,
    )
    Text(
        s.tr(
            "Demonstrations only. No effects are applied to a game.",
            "Только демонстрации. Эффекты не применяются к игре.",
        )
    )
    modules.forEachIndexed { index, name ->
        var expanded by remember { mutableStateOf(false) }
        Panel(
            name,
            s.tr(
                listOf(
                    "Radial attack pulse",
                    "Impact sparks",
                    "Celebration burst",
                    "Luminous projectile path",
                    "Health bar styling",
                    "Ambient particles",
                    "Orbital motion",
                )[index],
                listOf(
                    "Радиальный импульс",
                    "Искры попадания",
                    "Вспышка победы",
                    "Светящийся след",
                    "Оформление здоровья",
                    "Фоновые частицы",
                    "Орбитальное движение",
                )[index],
            ),
        ) {
            val enabled = name in s.enabled
            Toggle(s.tr("Enable preview", "Включить демонстрацию"), enabled) {
                update(s.copy(enabled = if (it) s.enabled + name else s.enabled - name))
            }
            if (enabled) EffectPreview(index, s, s.intensity[name] ?: .5f)
            TextButton({ expanded = !expanded }) {
                Text(s.tr("Adjust preview", "Настроить демонстрацию"))
            }
            AnimatedVisibility(
                expanded,
                enter =
                    fadeIn(tween(if (s.animations) 220 else 0)) +
                        expandVertically(tween(if (s.animations) 220 else 0)),
                exit =
                    fadeOut(tween(if (s.animations) 220 else 0)) +
                        shrinkVertically(tween(if (s.animations) 220 else 0)),
            ) {
                Column {
                    Control(s.tr("Intensity", "Интенсивность"), s.intensity[name] ?: .5f, 0f..1f) {
                        update(s.copy(intensity = s.intensity + (name to it)))
                    }
                }
            }
        }
    }
}

@Composable
private fun EffectPreview(index: Int, s: Settings, intensity: Float) {
    var phase by remember { mutableFloatStateOf(.4f) }
    LaunchedEffect(s.animations, s.speed) {
        if (s.animations)
            while (true) {
                delay(32)
                phase = (phase + .016f * s.speed) % 1f
            }
    }
    val color = Color(s.accent)
    Canvas(Modifier.fillMaxWidth().height(92.dp)) {
        val c = center
        val t = phase
        when (index) {
            0 -> {
                drawCircle(
                    color.copy(alpha = 1 - t),
                    12f + t * 65f,
                    c,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(3f + intensity * 5),
                )
                drawCircle(color, 8f, c)
            }
            1,
            2 ->
                repeat(if (index == 1) 10 else 22) {
                    val a = it * 2 * PI / (if (index == 1) 10 else 22)
                    val r = 10 + t * size.height * .45f
                    drawCircle(
                        color.copy(alpha = 1 - t * .8f),
                        3f + intensity * 4,
                        Offset(c.x + cos(a).toFloat() * r, c.y + sin(a).toFloat() * r),
                    )
                }
            3 ->
                repeat(18) {
                    val x = (t * size.width - it * 9 + size.width) % size.width
                    drawCircle(color.copy(alpha = 1 - it / 19f), 2f + intensity * 5, Offset(x, c.y))
                }
            4 -> {
                drawRoundRect(
                    Color(0xFF343446),
                    topLeft = Offset(size.width * .2f, c.y - 10),
                    size = androidx.compose.ui.geometry.Size(size.width * .6f, 20f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f),
                )
                drawRoundRect(
                    color,
                    topLeft = Offset(size.width * .2f, c.y - 10),
                    size =
                        androidx.compose.ui.geometry.Size(
                            size.width * .6f * (.3f + intensity * .7f),
                            20f,
                        ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f),
                )
            }
            5 ->
                repeat(24) {
                    drawCircle(
                        color.copy(alpha = .2f + intensity * .6f),
                        2f + (it % 3),
                        Offset((it * 79f) % size.width, (it * 31f + t * size.height) % size.height),
                    )
                }
            else ->
                drawCircle(
                    color,
                    10f + intensity * 8,
                    Offset(
                        c.x + cos(t * 2 * PI).toFloat() * 60,
                        c.y + sin(t * 2 * PI).toFloat() * 25,
                    ),
                )
        }
    }
}
