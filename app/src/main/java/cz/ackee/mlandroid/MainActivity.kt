package cz.ackee.mlandroid

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cz.ackee.mlandroid.databinding.ActivityMainBinding
import cz.ackee.mlandroid.digitalink.DigitalInkActivity
import cz.ackee.mlandroid.facedetection.FaceDetectionActivity
import cz.ackee.mlandroid.imageclassification.ImageClassificationActivity
import cz.ackee.mlandroid.imageclassification.ImageClassificationCustomModelActivity
import cz.ackee.mlandroid.textrecognition.TextRecognitionActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTextRecognition.setOnClickListener {
            startActivity(Intent(this, TextRecognitionActivity::class.java))
        }

        binding.btnDigitalInk.setOnClickListener {
            startActivity(Intent(this, DigitalInkActivity::class.java))
        }

        binding.btnFaceDetection.setOnClickListener {
            startActivity(Intent(this, FaceDetectionActivity::class.java))
        }

        binding.btnImageClassification.setOnClickListener {
            startActivity(Intent(this, ImageClassificationActivity::class.java))
        }

        binding.btnImageClassificationCustomModel.setOnClickListener {
            startActivity(Intent(this, ImageClassificationCustomModelActivity::class.java))
        }
    }
}