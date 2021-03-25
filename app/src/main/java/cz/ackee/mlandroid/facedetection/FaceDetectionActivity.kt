package cz.ackee.mlandroid.facedetection

import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        // detect faces in image
    }

    override fun onImageChosen(bitmap: Bitmap) {
        binding.imgPhoto.setImageBitmap(bitmap)
    }

    class BoundingBoxDrawable : Drawable() {

        // set faces result

        var originalImageSize = Rect(0, 0, 0, 0)

        private val boxPaint = Paint().apply {
            strokeWidth = 3f
            style = Paint.Style.STROKE
            color = Color.GREEN
        }

        override fun draw(canvas: Canvas) {
            // draw faces
        }

        override fun setAlpha(alpha: Int) = Unit

        override fun getOpacity(): Int = PixelFormat.OPAQUE

        override fun setColorFilter(colorFilter: ColorFilter?) = Unit
    }
}
