package cz.ackee.mlandroid

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.exifinterface.media.ExifInterface
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cz.ackee.mlandroid.databinding.DialogImageChooserBinding


class BottomSheetImageChooser : BottomSheetDialogFragment() {

    interface ParentHost {

        fun onImageChosen(bitmap: Bitmap)
    }

    private lateinit var binding: DialogImageChooserBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DialogImageChooserBinding.inflate(inflater, container, false)
            .also {
                binding = it
            }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnChooseImage.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .start()
        }
        binding.btnTakePicture.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .start()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data ?: return
            val exif = ExifInterface(requireActivity().contentResolver.openInputStream(fileUri)!!)
            (activity as? ParentHost)?.onImageChosen(
                BitmapFactory.decodeStream(
                    requireActivity().contentResolver.openInputStream(fileUri)
                ).rotateIfNecessary(exif)
            )
            dismissAllowingStateLoss()
        }
    }
}

private fun Bitmap.rotateIfNecessary(ei: ExifInterface): Bitmap {
    return when (ei.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    )) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotate(90)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotate(180)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotate(270)
        else -> this
    }
}

private fun Bitmap.rotate(degrees: Int): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees.toFloat())
    return Bitmap.createBitmap(
        this, 0, 0, width, height,
        matrix, true
    )
}
