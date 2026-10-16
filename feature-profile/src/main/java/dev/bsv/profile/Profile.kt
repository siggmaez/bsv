package dev.bsv.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.bsv.settings.*
import dev.bsv.ui.*

@Composable
fun Profile(s: Settings, update: (Settings) -> Unit) = Page {
    Panel(
        s.tr("Custom Rank", "Свой ранг"),
        s.tr(
            "Original BSV demo emblems · local preview only",
            "Оригинальные эмблемы BSV · только локальный предпросмотр",
        ),
    ) {
        Toggle(s.tr("Enable Custom Rank", "Включить свой ранг"), s.customRank) {
            update(s.copy(customRank = it))
        }
        Choice(s.rank, ranks) { update(s.copy(rank = it)) }
        val color =
            if (s.customRank)
                listOf(
                    Color(0xFFB87C52),
                    Color(0xFFBCC5DA),
                    Color(0xFFF7C948),
                    Color(0xFF63DAFF),
                    Color(0xFFE46BE8),
                    Color(0xFFFF667D),
                )[ranks.indexOf(s.rank).coerceAtLeast(0)]
            else Color.Gray
        Canvas(Modifier.size(68.dp)) {
            val path =
                Path().apply {
                    moveTo(size.width / 2, 0f)
                    lineTo(size.width, size.height * .25f)
                    lineTo(size.width * .8f, size.height * .8f)
                    lineTo(size.width / 2, size.height)
                    lineTo(size.width * .2f, size.height * .8f)
                    lineTo(0f, size.height * .25f)
                    close()
                }
            drawPath(path, color)
            repeat(if (s.customRank) ranks.indexOf(s.rank) + 1 else 1) { i ->
                drawCircle(
                    Color.White.copy(alpha = .9f),
                    size.width * .035f,
                    androidx.compose.ui.geometry.Offset(
                        size.width * (.25f + i * .1f),
                        size.height * .55f,
                    ),
                )
            }
        }
        Text(if (s.customRank) s.rank else s.tr("Default rank", "Стандартный ранг"))
        TextButton({ update(s.copy(customRank = false, rank = "Diamond")) }) {
            Text(s.tr("Reset to Default", "Восстановить"))
        }
    }
    Panel(s.tr("Custom Trophies", "Свои кубки")) {
        Toggle(s.tr("Enable Custom Trophies", "Включить свои кубки"), s.customTrophies) {
            update(s.copy(customTrophies = it))
        }
        OutlinedTextField(
            s.trophies,
            { update(s.copy(trophies = it.filter(Char::isDigit).take(9))) },
            label = { Text(s.tr("Trophies", "Кубки")) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "◇ " + if (s.customTrophies) s.trophies.ifEmpty { "0" } else "1250",
            style = MaterialTheme.typography.headlineMedium,
        )
        TextButton({ update(s.copy(customTrophies = false, trophies = "25000")) }) {
            Text(s.tr("Reset to Default", "Восстановить"))
        }
    }
    Panel(s.tr("Custom Profile", "Оформление профиля")) {
        Text(
            s.tr(
                "Coming later: banners, frames and profile palettes.",
                "В разработке: баннеры, рамки и палитры профиля.",
            )
        )
    }
}
