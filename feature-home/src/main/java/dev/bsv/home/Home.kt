package dev.bsv.home
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import dev.bsv.settings.*
import dev.bsv.ui.*
@Composable fun Home(s: Settings, reset: ()->Unit) = Page {
 Panel("BSV",s.tr("YOUR LOOK. YOUR RULES.","ТВОЙ СТИЛЬ. ТВОИ ПРАВИЛА.")) {
 Text("Brawl Stars Visuals",style=MaterialTheme.typography.headlineMedium)
 Text("Version 0.1.0 | Android",color=MaterialTheme.colorScheme.primary)
 Text(s.tr("An independent visual playground. All previews are local.","Независимая визуальная лаборатория. Все демонстрации локальные."))
 }
 Panel(s.tr("Overview","Обзор")) {
 Text(s.tr("${s.activeCount} settings enabled · ${modules.size} demo modules","Включено настроек: ${s.activeCount} · Демо-модулей: ${modules.size}"))
 Text(s.tr("Storage: DataStore • Integration: disconnected","Хранилище: DataStore • Интеграция: отсутствует"))
 }
 Panel(s.tr("Device","Устройство")) {
 Text("${Build.MANUFACTURER} ${Build.MODEL} • Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
 Text(Build.SUPPORTED_ABIS.joinToString())
 OutlinedButton(reset) { Text(s.tr("Reset Settings","Сбросить настройки")) }
 }
}
