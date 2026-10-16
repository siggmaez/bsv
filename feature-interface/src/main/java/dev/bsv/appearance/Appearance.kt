package dev.bsv.appearance
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import dev.bsv.settings.*
import dev.bsv.ui.*
@Composable fun Appearance(s: Settings,update: (Settings)->Unit) = Page {
 Panel(s.tr("Appearance","Оформление"),s.tr("Changes are applied immediately","Изменения применяются сразу")) {
 Text(s.tr("Theme","Тема")); Choice(s.theme,listOf("Midnight","AMOLED","Slate")) {update(s.copy(theme=it))}
 Text(s.tr("Accent color","Цвет акцента"))
 Choice(when(s.accent) {0xFF6366F1 -> "Indigo";0xFF14B8A6 -> "Mint";0xFFF472B6 -> "Rose";else -> "Violet"},listOf("Violet","Indigo","Mint","Rose")) {update(s.copy(accent=when(it){"Indigo"->0xFF6366F1;"Mint"->0xFF14B8A6;"Rose"->0xFFF472B6;else->0xFF8B5CF6}))}
 Control(s.tr("UI Scale","Масштаб интерфейса"),s.scale,.8f..1.2f) {update(s.copy(scale=it))}
 Control(s.tr("Menu Opacity","Прозрачность меню"),s.opacity,.65f..1f) {update(s.copy(opacity=it))}
 Control(s.tr("Blur Intensity (Android 12+)","Размытие (Android 12+)"),s.blur,0f..24f) {update(s.copy(blur=it))}
 }
 Panel(s.tr("Motion","Анимации")) {
 Toggle(s.tr("Enable animations","Включить анимации"),s.animations) {update(s.copy(animations=it))}
 Control(s.tr("Animation Speed","Скорость анимаций"),s.speed,.5f..2f) {update(s.copy(speed=it))}
 }
 Panel(s.tr("Floating BSV button","Плавающая кнопка BSV")) {
 Text(s.tr("Drag the button to move it. Tap to open or close the menu.","Перетащите кнопку пальцем. Нажмите для открытия или закрытия меню."))
 Control(s.tr("Size","Размер"),s.buttonSize,40f..80f) {update(s.copy(buttonSize=it))}
 Control(s.tr("Opacity","Прозрачность"),s.buttonOpacity,.3f..1f) {update(s.copy(buttonOpacity=it))}
 TextButton({update(s.copy(buttonX=.9f,buttonY=.8f))}) {Text(s.tr("Reset position","Сбросить позицию"))}
 }
}
