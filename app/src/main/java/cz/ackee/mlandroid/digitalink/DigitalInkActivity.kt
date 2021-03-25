package cz.ackee.mlandroid.digitalink

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import cz.ackee.mlandroid.databinding.ActivityDigitalInkBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class DigitalInkActivity : AppCompatActivity() {

    private val digitalInkManager = DigitalInkManager(
        ModelsManager()
    )
    private lateinit var binding: ActivityDigitalInkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Digital Ink"
        binding = ActivityDigitalInkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupDropdownSpinner(binding.languagesSpinner)

        digitalInkManager.onStatusChangedListener = binding.txtStatus

        binding.btnDownload.setOnClickListener {
            lifecycleScope.launch {
                // download model
            }
        }

        binding.btnClear.setOnClickListener {
            binding.resultView.clear()
            binding.drawingView.clear()
            digitalInkManager.clear()
        }

        binding.btnRecognize.setOnClickListener {
            lifecycleScope.launch {
                val result = digitalInkManager.recognize()
                if(result != null) {
                    binding.resultView.drawRecognizedResult(result)
                }
            }
        }

        binding.drawingView.onMotionEventListener = { event ->
            // process touch event
        }

        setupDownloadedLanguagesObserving()
    }

    private fun setupDropdownSpinner(languagesSpinner: Spinner) {
        val languageAdapter = ArrayAdapter<LanguageDropdownItem>(
            this,
            android.R.layout.simple_spinner_item
        )
        languageAdapter.addAll(getDropdownItems())
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languagesSpinner.adapter = languageAdapter
        languagesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val dropdownItem = parent.adapter.getItem(position) as LanguageDropdownItem
                if (dropdownItem.languageTag == null) {
                    // just a title item
                    return
                }
                onLanguageSelected(dropdownItem.languageTag)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // nothing to do
            }
        }
    }

    private fun onLanguageSelected(languageTag: String) {
        // select language
    }

    private fun setupDownloadedLanguagesObserving() {
        lifecycleScope.launchWhenStarted {
            digitalInkManager.downloadedLanguagesStream
                .collect { downloadedModels ->
                    val adapter = binding
                        .languagesSpinner.adapter as? ArrayAdapter<LanguageDropdownItem>
                    adapter ?: return@collect
                    (0 until adapter.count).forEach {
                        val item = adapter.getItem(it)
                        adapter.getItem(it)?.isDownloaded =
                            downloadedModels.contains(item?.languageTag)
                    }
                    adapter.notifyDataSetChanged()
                }
        }
    }

    private fun getDropdownItems(): List<LanguageDropdownItem> {
        return listOf(LanguageDropdownItem("Select language"))
            .plus(listOf(LanguageDropdownItem("Non-Text Models")))
            .plus(NON_TEXT_MODELS.map { (langTag, modelTitle) ->
                LanguageDropdownItem(
                    modelTitle,
                    langTag
                )
            })
            .plus(listOf(LanguageDropdownItem("Text Models")))
            .plus(
                DigitalInkRecognitionModelIdentifier.allModelIdentifiers()
                    .filter { !NON_TEXT_MODELS.containsKey(it.languageTag) }
                    .map { modelIdentifier ->
                        val title = buildString {
                            append(Locale(modelIdentifier.languageSubtag).displayName)
                            modelIdentifier.regionSubtag?.let { region ->
                                append("($region)")
                            }
                            modelIdentifier.scriptSubtag?.let { script ->
                                append("($script)")
                            }
                        }
                        LanguageDropdownItem(
                            title = title,
                            languageTag = modelIdentifier.languageTag
                        )
                    }
                    .sortedBy { it.title }
            )
    }

    companion object {
        private val NON_TEXT_MODELS = mapOf(
            "zxx-Zsym-x-autodraw" to "Autodraw",
            "zxx-Zsye-x-emoji" to "Emoji",
            "zxx-Zsym-x-shapes" to "Shapes"
        )
    }

    data class LanguageDropdownItem(
        val title: String,
        val languageTag: String? = null,
        var isDownloaded: Boolean = false
    ) {
        override fun toString(): String {
            return "${if (isDownloaded) "[D] " else ""}$title"
        }
    }
}