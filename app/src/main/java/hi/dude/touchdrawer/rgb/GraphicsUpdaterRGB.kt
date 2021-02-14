package hi.dude.touchdrawer.rgb

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.DisplayMetrics
import android.util.Log
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import hi.dude.touchdrawer.network.NeuralNetwork
import kotlin.math.exp

class GraphicsUpdaterRGB(private val activity: RGBActivity) : Runnable  {

    private val TAG = "Updater"

    private var network: NeuralNetwork

    private val w: Int
    private val h: Int

    var alive = true
    private lateinit var trainer: NetworkTrainerRGB
    private lateinit var trainerThread: Thread

    init {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        w = metrics.widthPixels
        h = metrics.heightPixels

        val sigmoid = { x: Double -> 1 / (1 + exp(-x)) }
        val dsigmoid = { y: Double -> y * (1 - y) }
        network = NeuralNetwork(0.01, sigmoid, dsigmoid, 2, 5, 5, 3)
    }

    override fun run() {
        trainer = NetworkTrainerRGB(activity, network)
        trainerThread = Thread(trainer)
        trainerThread.start()
        while (alive) {
            draw()
        }
    }

    private fun draw() {
        try {
            val bitmap = drawPointsOn(createBackground())
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.color = Color.GRAY
            canvas.drawCircle((w / 2).toFloat(), (h + 50).toFloat(), 107f, paint)
            paint.color = activity.nextColor.color
            canvas.drawCircle((w / 2).toFloat(), (h + 50).toFloat(), 100f, paint)
            synchronized(activity) {
                activity.runOnUiThread { activity.view.background = BitmapDrawable(activity.resources, bitmap) }
            }
        } catch (e: ConcurrentModificationException) {
            Log.e(TAG, "draw: ConcurrentModificationException")
        }
    }

    private fun createBackground(): Bitmap {
        val bitmap = createBitmap(w / 20 + 1, h / 20 + 1)
        for (i in 0..(w / 20)) {
            for (j in 0..(h / 20)) {
                val nx = i.toDouble() / w * 20 - 0.5
                val ny = j.toDouble() / h * 20 - 0.5
                val outputs = network.feedForward(doubleArrayOf(nx, ny))

//                var black = max(0.0, min(1.0, outputs[0] - outputs[1] + 0.5))
//                black = 0.3 + black * 0.5
//                val br = (255 * black).toInt()
                val red = (outputs[0] * 255).toInt()
                val green = (outputs[1] * 255).toInt()
                val blue = (outputs[2] * 255).toInt()
                bitmap.setPixel(i, j, Color.rgb(red, green, blue))
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
            paint.color = p.color.color
            canvas.drawCircle(p.x, p.y, 45F, paint)
        }
        return bitmap
    }

    fun stopTrainer() {
        trainer.alive = false
        trainerThread.join()
    }

}