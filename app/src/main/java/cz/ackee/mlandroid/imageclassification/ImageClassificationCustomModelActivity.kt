package cz.ackee.mlandroid.imageclassification

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import cz.ackee.mlandroid.BottomSheetImageChooser
import cz.ackee.mlandroid.databinding.ActivityImageClassificationBinding

class ImageClassificationCustomModelActivity : AppCompatActivity(), BottomSheetImageChooser.ParentHost {

    private lateinit var binding: ActivityImageClassificationBinding

    private val localModel = LocalModel.Builder()
        .setAssetFilePath("birds_model.tflite")
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Image classification"
        binding = ActivityImageClassificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnChooseImage.setOnClickListener {
            BottomSheetImageChooser()
                .show(supportFragmentManager, "dialog")
        }
        binding.btnDetect.setOnClickListener {
            classifyImage()
        }
    }

    private fun classifyImage() {
        val labeler = ImageLabeling.getClient(CustomImageLabelerOptions.Builder(localModel)
            .setMaxResultCount(5)
            .setConfidenceThreshold(0.6f)
            .build())
        val imageBitmap = binding.imgPhoto.drawable.toBitmap()
        labeler.process(InputImage.fromBitmap(imageBitmap, 0))
            .addOnSuccessListener {
                binding.txtResult.text = it.joinToString("\n\n") { label ->
                    buildString {
                        appendLine("Label: ${label.text}")
                        appendLine("Confidence: ${label.confidence}")
                        appendLine("Index: ${label.index}")
                    }
                }
            }
    }

    override fun onImageChosen(bitmap: Bitmap) {
        binding.imgPhoto.setImageBitmap(bitmap)
    }
}