package cz.ackee.mlandroid.digitalink

import com.google.mlkit.common.MlKitException
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.*
import kotlinx.coroutines.tasks.await

class ModelsManager {

    var recognizer: DigitalInkRecognizer? = null
    private var model: DigitalInkRecognitionModel? = null
    private val remoteModelManager = RemoteModelManager.getInstance()

    fun setupModelForLanguage(languageTag: String) {
        val modelIdentifier = try {
            DigitalInkRecognitionModelIdentifier.fromLanguageTag(languageTag)
        } catch (e: MlKitException) {
            throw IllegalArgumentException("Language tag $languageTag does not exist")
        }
        modelIdentifier ?: throw IllegalArgumentException("No model for  $languageTag")
        val model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
        recognizer = DigitalInkRecognition.getClient(
            DigitalInkRecognizerOptions.builder(model).build()
        )
        this.model = model
    }

    val isModelSet: Boolean
        get() = model != null

    suspend fun isModelDownloaded(): Boolean {
        return model?.let {
            remoteModelManager.isModelDownloaded(it).await()
        } ?: false
    }

    suspend fun downloadModel() {
        model?.let { model ->
            remoteModelManager.download(model, DownloadConditions.Builder().build()).await()
        }
    }

    suspend fun getDownloadedModelsLanguageTags(): List<String> {
        return remoteModelManager.getDownloadedModels(DigitalInkRecognitionModel::class.java)
            .await()
            .map {
                it.modelIdentifier.languageTag
            }
    }
}