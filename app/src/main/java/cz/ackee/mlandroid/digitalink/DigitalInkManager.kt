package cz.ackee.mlandroid.digitalink

import android.view.MotionEvent
import com.google.mlkit.vision.digitalink.Ink
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DigitalInkManager(
    private val modelsManager: ModelsManager,
) {

    interface StatusChangedListener {
        fun onStatusChange(status: String)
    }

    var onStatusChangedListener: StatusChangedListener? = null

    private val _downloadedLanguagesStream = MutableStateFlow<List<String>>(emptyList())
    val downloadedLanguagesStream: Flow<List<String>> get() = _downloadedLanguagesStream

    private var strokeBuilder = Ink.Stroke.builder()
    private var inkBuilder = Ink.builder()

    init {
        GlobalScope.launch {
            updateDownloadedLanguages()
        }
    }

    private suspend fun updateDownloadedLanguages() {
        _downloadedLanguagesStream.value = modelsManager.getDownloadedModelsLanguageTags()
    }

    fun languageSelected(languageTag: String) {
        // select language in models manager
    }

    private fun updateStatus(message: String?) {
        onStatusChangedListener?.onStatusChange(message ?: "")
    }

    suspend fun downloadModel() {
        if (modelsManager.isModelSet) {
            updateStatus("Model downloading")
            modelsManager.downloadModel()
            updateStatus("Model downloaded")
            updateDownloadedLanguages()
        } else {
            updateStatus("No model set")
        }
    }

    fun processTouchEvent(event: MotionEvent) {
        // process touch event
    }

    fun clear() {
        resetCurrentInk()
        updateStatus("")
    }

    private fun resetCurrentInk() {
        inkBuilder = Ink.builder()
        strokeBuilder = Ink.Stroke.builder()
    }

    suspend fun recognize(): RecognizedInkResult? {
        if (!modelsManager.isModelSet) {
            updateStatus("Model is not set. Choose a language")
            return null
        }
        if (!modelsManager.isModelDownloaded()) {
            updateStatus("Model not downloaded")
            return null
        }
        // recognize ink in image
        return null
    }
}


data class RecognizedInkResult(
    val ink: Ink,
    val textResult: String
)