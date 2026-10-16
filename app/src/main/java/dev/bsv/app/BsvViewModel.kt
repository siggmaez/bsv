package dev.bsv.app
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.bsv.settings.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class BsvViewModel(app: Application): AndroidViewModel(app) {
 private val repo=DataStoreSettingsRepository(app)
 private val pending=Channel<Settings>(Channel.CONFLATED)
 private val _settings=MutableStateFlow(Settings())
 val settings=_settings.asStateFlow()
 private val _status=MutableStateFlow("")
 val status=_status.asStateFlow()
 var ready=false; private set
 init { viewModelScope.launch {
 try { _settings.value=repo.settings.first();ready=true } catch(e: Exception) { _status.value="Storage read error: ${e.message}"; ready=true }
 for(value in pending) try { repo.save(value);_status.value=if(value.language=="ru") "Сохранено" else "Saved" } catch(e: Exception) { _status.value="Save error: ${e.message}" }
 } }
 fun update(s: Settings) { if(!ready) return;_settings.value=s.normalized();pending.trySend(_settings.value) }
 fun save() {pending.trySend(_settings.value)}
 fun importJson(text: String) { try { update(SettingsCodec.decode(text)) } catch(e: Exception) {report("Import error: ${e.message}")} }
 fun report(message: String) {_status.value=message}
}
