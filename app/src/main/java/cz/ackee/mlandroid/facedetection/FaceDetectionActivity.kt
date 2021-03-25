package cz.ackee.mlandroid.facedetection

import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.times
import androidx.core.graphics.toRectF
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import cz.ackee.mlandroid.BottomSheetImageChooser
import cz.ackee.mlandroid.databinding.ActivityFaceDetectionBinding


class FaceDetectionActivity : AppCompatActivity(), BottomSheetImageChooser.ParentHost {

    private lateinit var boundingBoxDrawable: BoundingBoxDrawable
    private lateinit var binding: ActivityFaceDetectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Face detection"
        binding = ActivityFaceDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnChooseImage.setOnClickListener {
            BottomSheetImageChooser()
                .show(supportFragmentManager, "dialog")
        }
        binding.btnDetect.setOnClickListener {
            detectInImage()
        }
        binding.imgPhoto.foreground = BoundingBoxDrawable()
            .also {
                boundingBoxDrawable = it
            }
    }

    private fun detectInImage() {
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .enableTracking()
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        val detector = FaceDetection.getClient(highAccuracyOpts)
        boundingBoxDrawable.faces = emptyList()

        val bitmap = binding.imgPhoto.drawable.toBitmap()
        boundingBoxDrawable.originalImageSize = Rect(0, 0, bitmap.width, bitmap.height)
        detector.process(InputImage.fromBitmap(bitmap, 0))
            .addOnSuccessListener {
                boundingBoxDrawable.faces = it
                binding.txtResult.text = it.toPresentableText()
            }
    }

    override fun onImageChosen(bitmap: Bitmap) {
        binding.imgPhoto.setImageBitmap(bitmap)
        boundingBoxDrawable.faces = emptyList()
    }

    class BoundingBoxDrawable : Drawable() {

        var faces: List<Face> = emptyList()
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

        override fun draw(canvas: Canvas) {
            faces.forEach {
                val faceRect = it.boundingBox.toRectF().times(
                    bounds.width() / originalImageSize.width().toFloat()
                )
                canvas.drawRect(faceRect, boxPaint)
            }
        }

        override fun setAlpha(alpha: Int) = Unit

        override fun getOpacity(): Int = PixelFormat.OPAQUE

        override fun setColorFilter(colorFilter: ColorFilter?) = Unit
    }
}

private fun List<Face>.toPresentableText(): String {
    return joinToString("\n\n") { face ->
        buildString {
            appendLine("id: ${face.trackingId}")
            appendLine("Smiling probability: ${face.smilingProbability}")
            appendLine("Left eye opened probability: ${face.leftEyeOpenProbability}")
            appendLine("Right eye opened probability: ${face.rightEyeOpenProbability}")
        }
    }
}
