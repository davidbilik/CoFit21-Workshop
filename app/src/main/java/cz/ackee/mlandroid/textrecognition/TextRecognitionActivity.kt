package cz.ackee.mlandroid.textrecognition

import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.times
import androidx.core.graphics.toRectF
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import cz.ackee.mlandroid.BottomSheetImageChooser
import cz.ackee.mlandroid.databinding.ActivityTextRecognitionBinding


class TextRecognitionActivity : AppCompatActivity(), BottomSheetImageChooser.ParentHost {

    private lateinit var textResultsOverlay: TextResultsOverlay
    private lateinit var binding: ActivityTextRecognitionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Text recognition"
        binding = ActivityTextRecognitionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnChooseImage.setOnClickListener {
            BottomSheetImageChooser()
                .show(supportFragmentManager, "dialog")
        }
        binding.btnRecognize.setOnClickListener {
            recognizeFromImage()
        }
        binding.imgPhoto.foreground = TextResultsOverlay()
            .also {
                textResultsOverlay = it
            }
    }

    private fun recognizeFromImage() {
        val imageBitmap = binding.imgPhoto.drawable.toBitmap()
        textResultsOverlay.originalImageSize = Rect(0, 0, imageBitmap.width, imageBitmap.height)
        textResultsOverlay.text = null
        val image = InputImage.fromBitmap(imageBitmap, 0)
        recognizeText(image)
    }

    private fun recognizeText(image: InputImage) {
        val recognizer = TextRecognition.getClient()

        recognizer.process(image)
            .addOnSuccessListener { textResult ->
                binding.txtResult.text = textResult.text
                textResultsOverlay.text = textResult
            }
    }

    class TextResultsOverlay : Drawable() {

        var text: Text? = null
            set(value) {
                field = value
                invalidateSelf()
            }

        var originalImageSize = Rect(0, 0, 0, 0)

        private val boxPaint = Paint().apply {
            strokeWidth = 3f
            style = Paint.Style.STROKE
            color = Color.GREEN
        }

        private val textPaint = Paint().apply {
            color = Color.RED
            textSize = 48f
        }

        override fun draw(canvas: Canvas) {
            text?.let { currentText ->
                currentText.textBlocks.forEach { textBlock ->
                    textBlock.boundingBox?.let { boundingBox ->
                        val boxScaled = boundingBox.toRectF().times(
                            bounds.width() / originalImageSize.width().toFloat()
                        )
                        canvas.drawRect(
                            boxScaled,
                            boxPaint
                        )

                        canvas.drawText(
                            textBlock.recognizedLanguage,
                            boxScaled.left,
                            boxScaled.top,
                            textPaint
                        )
                    }
                }
            }
        }

        override fun setAlpha(alpha: Int) = Unit

        override fun getOpacity(): Int = PixelFormat.OPAQUE

        override fun setColorFilter(colorFilter: ColorFilter?) = Unit
    }

    override fun onImageChosen(bitmap: Bitmap) {
        binding.imgPhoto.setImageBitmap(bitmap)
        textResultsOverlay.text = null
    }
}