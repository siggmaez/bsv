package dev.bsv.settings

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val modules =
    listOf(
        "Attack FX",
        "Hit Effects",
        "Kill Effects",
        "Projectile Trails",
        "Custom HP Bars",
        "Background FX",
        "Smooth Animations",
    )
val ranks = listOf("Bronze", "Silver", "Gold", "Diamond", "Mythic", "Legend")

@Serializable
data class Settings(
    val language: String = "en",
    val customRank: Boolean = false,
    val rank: String = "Diamond",
    val customTrophies: Boolean = false,
    val trophies: String = "25000",
    val enabled: Set<String> = emptySet(),
    val intensity: Map<String, Float> = emptyMap(),
    val theme: String = "Midnight",
    val accent: Long = 0xFF8B5CF6,
    val scale: Float = 1f,
    val speed: Float = 1f,
    val opacity: Float = 1f,
    val blur: Float = 0f,
    val animations: Boolean = true,
    val buttonSize: Float = 56f,
    val buttonOpacity: Float = 0.9f,
    val buttonX: Float = 0.9f,
    val buttonY: Float = 0.8f,
) {
    fun normalized() =
        copy(
            language = if (language == "ru") "ru" else "en",
            rank = rank.takeIf { it in ranks } ?: "Diamond",
            trophies = trophies.filter { it.isDigit() }.take(9),
            enabled = enabled.intersect(modules.toSet()),
            intensity =
                intensity.filterKeys { it in modules }.mapValues { (_, v) -> v.safe(0f, 1f, 0.5f) },
            theme = theme.takeIf { it in listOf("Midnight", "AMOLED", "Slate") } ?: "Midnight",
            accent = accent.coerceIn(0xFF000000, 0xFFFFFFFF),
            scale = scale.safe(.8f, 1.2f, 1f),
            speed = speed.safe(.5f, 2f, 1f),
            opacity = opacity.safe(.65f, 1f, 1f),
            blur = blur.safe(0f, 24f, 0f),
            buttonSize = buttonSize.safe(40f, 80f, 56f),
            buttonOpacity = buttonOpacity.safe(.3f, 1f, .9f),
            buttonX = buttonX.safe(0f, 1f, .9f),
            buttonY = buttonY.safe(0f, 1f, .8f),
        )

    val activeCount: Int
        get() = enabled.size + (if (customRank) 1 else 0) + (if (customTrophies) 1 else 0)
}

private fun Float.safe(min: Float, max: Float, fallback: Float) =
    if (isFinite()) coerceIn(min, max) else fallback

object SettingsCodec {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    fun encode(settings: Settings) =
        json.encodeToString(Settings.serializer(), settings.normalized())

    fun decode(value: String): Settings {
        require(value.length <= 65536) { "File too large" }
        return json.decodeFromString(Settings.serializer(), value).normalized()
    }
}

interface SettingsRepository {
    val settings: Flow<Settings>

    suspend fun save(value: Settings)
}

private val Context.store by preferencesDataStore("bsv")

class DataStoreSettingsRepository(context: Context) : SettingsRepository {
    private val store = context.applicationContext.store
    private val key = stringPreferencesKey("settings_v1")
    override val settings =
        store.data.map { prefs -> prefs[key]?.let(SettingsCodec::decode) ?: Settings() }

    override suspend fun save(value: Settings) {
        store.edit { it[key] = SettingsCodec.encode(value) }
    }
}

// Independent boundary: any future client adapter must implement this explicitly.
interface VisualPreviewPort {
    val connected: Boolean

    fun preview(settings: Settings)
}

class LocalPreviewPort : VisualPreviewPort {
    override val connected = false

    override fun preview(settings: Settings) = Unit
}
