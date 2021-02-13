package hi.dude.touchdrawer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.DisplayMetrics
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

class Updater(private val activity: MainActivity) : Runnable {

    private val TAG = "Updater"

    private var network: NeuralNetwork

    private val w: Int
    private val h: Int

    var alive = true

    init {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        w = metrics.widthPixels
        h = metrics.heightPixels

        val sigmoid = { x: Double -> 1 / (1 + exp(-x)) }
        val dsigmoid = { y: Double -> y * (1 - y) }
        network = NeuralNetwork(0.01, sigmoid, dsigmoid, 2, 5, 5, 2)
    }

    override fun run() {
        while (alive) {
            draw()
        }
    }

    private fun draw() {
        if (activity.points.size > 0) train()
        val bitmap = drawPointsOn(createBackground())
        synchronized(activity) {
            activity.runOnUiThread { activity.view.background = BitmapDrawable(activity.resources, bitmap) }
        }
    }

    private fun createBackground(): Bitmap {
        val bitmap = createBitmap(w / 20 + 1, h / 20 + 1)
        for (i in 0..(w / 20)) {
            for (j in 0..(h / 20)) {
                val nx = i.toDouble() / w * 20 - 0.5
                val ny = j.toDouble() / h * 20 - 0.5
                val outputs = network.feedForward(doubleArrayOf(nx, ny))

                var black = max(0.0, min(1.0, outputs[0] - outputs[1] + 0.5))
                var white = 1 - black
                black = 0.3 + black * 0.5
                white = 0.5 + white * 0.5

//                val color = 100 shl 20 or ((black * 255).toInt() shl 20) or (white * 255).toInt()
//                bitmap.setPixel(i, j, color)
                val br = (255 * white).toInt()
                bitmap.setPixel(i, j, Color.rgb(br, br, br))
            }
        }
        return bitmap
    }

    private fun drawPointsOn(source: Bitmap): Bitmap {
        val bitmap = source.scale(w, h)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        for (p in activity.points) {
            paint.color = Color.GRAY
            canvas.drawCircle(p.x, p.y, 50F, paint)

            if (p.isBlack) paint.color = Color.BLACK else paint.color = Color.WHITE
            canvas.drawCircle(p.x, p.y, 45F, paint)

        }
        return bitmap
    }

    private fun train() {
        for (i in 0..10000) {

            val p = activity.points[(Math.random() * activity.points.size).toInt()] ?: continue
            val nx = p.x / w - 0.5
            val ny = p.y / h - 0.5

            network.feedForward(doubleArrayOf(nx, ny))

            val targets = DoubleArray(2)
            if (p.isBlack) targets[0] = 1.0
            else targets[1] = 1.0

            network.backpropagation(targets)
        }
    }

    fun resetNetwork() {
        val sigmoid = { x: Double -> 1 / (1 + exp(-x)) }
        val dsigmoid = { y: Double -> y * (1 - y) }
        network = NeuralNetwork(0.01, sigmoid, dsigmoid, 2, 5, 5, 2)
    }
}