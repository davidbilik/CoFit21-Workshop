package cz.ackee.mlandroid.digitalink

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context?,
    attributeSet: AttributeSet? = null
) : View(context, attributeSet) {

    private val currentStrokePaint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        color = Color.MAGENTA
        isAntiAlias = true
        strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            STROKE_WIDTH_DP.toFloat(),
            resources.displayMetrics
        )
    }

    private val canvasPaint: Paint = Paint(Paint.DITHER_FLAG)
    private val currentStroke: Path = Path()
    private lateinit var drawCanvas: Canvas
    private lateinit var canvasBitmap: Bitmap

    var onMotionEventListener: (MotionEvent) -> Unit = {}

    override fun onSizeChanged(
        width: Int,
        height: Int,
        oldWidth: Int,
        oldHeight: Int
    ) {
        canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap)
        invalidate()
    }

    fun clear() {
        currentStroke.reset()
        onSizeChanged(
            canvasBitmap.width,
            canvasBitmap.height,
            canvasBitmap.width,
            canvasBitmap.height
        )
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)
        canvas.drawPath(currentStroke, currentStrokePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        val x = event.x
        val y = event.y
        when (action) {
            MotionEvent.ACTION_DOWN -> currentStroke.moveTo(x, y)
            MotionEvent.ACTION_MOVE -> currentStroke.lineTo(x, y)
            MotionEvent.ACTION_UP -> {
                currentStroke.lineTo(x, y)
                drawCanvas.drawPath(currentStroke, currentStrokePaint)
                currentStroke.reset()
            }
            else -> {
            }
        }
        invalidate()
        onMotionEventListener(event)
        return true
    }

    companion object {
        const val STROKE_WIDTH_DP = 3
    }
}