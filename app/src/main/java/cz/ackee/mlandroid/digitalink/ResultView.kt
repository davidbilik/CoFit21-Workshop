package cz.ackee.mlandroid.digitalink

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.google.mlkit.vision.digitalink.Ink

class ResultView @JvmOverloads constructor(
    context: Context?,
    attributeSet: AttributeSet? = null
) : View(context, attributeSet) {

    private val recognizedStrokePaint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
        strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DrawingView.STROKE_WIDTH_DP.toFloat(),
            resources.displayMetrics
        )
        color = Color.RED
    }

    private val canvasPaint: Paint = Paint(Paint.DITHER_FLAG)

    private val textPaint: TextPaint = TextPaint().apply {
        color = Color.GREEN
    }

    private lateinit var drawCanvas: Canvas
    private lateinit var canvasBitmap: Bitmap

    fun clear() {
        onSizeChanged(
            canvasBitmap.width,
            canvasBitmap.height,
            canvasBitmap.width,
            canvasBitmap.height
        )
    }

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

    fun drawRecognizedResult(result: RecognizedInkResult) {
        clear()
        drawInk(result.ink, recognizedStrokePaint)
        val textBoundingBox = computeBoundingBox(result.ink)
        drawTextIntoBoundingBox(result.textResult, textBoundingBox, textPaint)
        invalidate()
    }

    private fun drawTextIntoBoundingBox(text: String, bb: Rect, textPaint: TextPaint) {
        val arbitraryFixedSize = 20f
        // Set an arbitrary text size to learn how high the text will be.
        textPaint.textSize = arbitraryFixedSize
        textPaint.textScaleX = 1f

        // Now determine the size of the rendered text with these settings.
        val r = Rect()
        textPaint.getTextBounds(text, 0, text.length, r)

        // Adjust height such that target height is met.
        val textSize = arbitraryFixedSize * bb.height().toFloat() / r.height().toFloat()
        textPaint.textSize = textSize

        // Redetermine the size of the rendered text with the new settings.
        textPaint.getTextBounds(text, 0, text.length, r)

        // Adjust scaleX to squeeze the text.
        textPaint.textScaleX = bb.width().toFloat() / r.width().toFloat()

        // And finally draw the text.
        drawCanvas.drawText(text, bb.left.toFloat(), bb.bottom.toFloat(), textPaint)
    }

    private fun drawInk(ink: Ink, paint: Paint) {
        for (s in ink.strokes) {
            drawStroke(s, paint)
        }
    }

    private fun drawStroke(s: Ink.Stroke, paint: Paint) {
        val path = Path()
        path.moveTo(s.points[0].x, s.points[0].y)
        for (p in s.points.drop(1)) {
            path.lineTo(p.x, p.y)
        }
        drawCanvas.drawPath(path, paint)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)
    }

    companion object {
        const val STROKE_WIDTH_DP = 3

        private const val MIN_BB_WIDTH = 10
        private const val MIN_BB_HEIGHT = 10
        private const val MAX_BB_WIDTH = 256
        private const val MAX_BB_HEIGHT = 256

        private fun computeBoundingBox(ink: Ink): Rect {
            var top = Float.MAX_VALUE
            var left = Float.MAX_VALUE
            var bottom = Float.MIN_VALUE
            var right = Float.MIN_VALUE
            for (s in ink.strokes) {
                for (p in s.points) {
                    top = Math.min(top, p.y)
                    left = Math.min(left, p.x)
                    bottom = Math.max(bottom, p.y)
                    right = Math.max(right, p.x)
                }
            }
            val centerX = (left + right) / 2
            val centerY = (top + bottom) / 2
            val bb = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
            // Enforce a minimum size of the bounding box such that recognitions for small inks are readable
            bb.union(
                (centerX - MIN_BB_WIDTH / 2).toInt(),
                (centerY - MIN_BB_HEIGHT / 2).toInt(),
                (centerX + MIN_BB_WIDTH / 2).toInt(),
                (centerY + MIN_BB_HEIGHT / 2).toInt()
            )
            // Enforce a maximum size of the bounding box, to ensure Emoji characters get displayed
            // correctly
            if (bb.width() > MAX_BB_WIDTH) {
                bb[bb.centerX() - MAX_BB_WIDTH / 2, bb.top, bb.centerX() + MAX_BB_WIDTH / 2] =
                    bb.bottom
            }
            if (bb.height() > MAX_BB_HEIGHT) {
                bb[bb.left, bb.centerY() - MAX_BB_HEIGHT / 2, bb.right] =
                    bb.centerY() + MAX_BB_HEIGHT / 2
            }
            return bb
        }
    }

}