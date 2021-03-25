package cz.ackee.mlandroid.digitalink

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class StatusTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatTextView(context, attributeSet), DigitalInkManager.StatusChangedListener {

    override fun onStatusChange(status: String) {
        this.text = status
    }
}