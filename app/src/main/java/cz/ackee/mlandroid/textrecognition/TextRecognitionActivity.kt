package cz.ackee.mlandroid.textrecognition

import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
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
        val imageBitmap = binding.imgPhoto.drawable?.toBitmap() ?: return
        // Recognize text in image
    }

    class TextResultsOverlay : Drawable() {

        var originalImageSize = Rect(0, 0, 0, 0)

        // set text: Text variable

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
            // draw bounds of text blocks
        }

        override fun setAlpha(alpha: Int) = Unit

        override fun getOpacity(): Int = PixelFormat.OPAQUE

        override fun setColorFilter(colorFilter: ColorFilter?) = Unit
    }

    override fun onImageChosen(bitmap: Bitmap) {
        binding.imgPhoto.setImageBitmap(bitmap)
    }
}