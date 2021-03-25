package cz.ackee.mlandroid.imageclassification

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cz.ackee.mlandroid.BottomSheetImageChooser
import cz.ackee.mlandroid.databinding.ActivityImageClassificationBinding

class ImageClassificationCustomModelActivity : AppCompatActivity(), BottomSheetImageChooser.ParentHost {

    private lateinit var binding: ActivityImageClassificationBinding

    // define local model

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
        // label image
    }

    override fun onImageChosen(bitmap: Bitmap) {
        binding.imgPhoto.setImageBitmap(bitmap)
    }
}