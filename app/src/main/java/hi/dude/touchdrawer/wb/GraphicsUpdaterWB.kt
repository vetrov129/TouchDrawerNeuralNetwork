package hi.dude.touchdrawer.wb

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.DisplayMetrics
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import hi.dude.touchdrawer.network.NeuralNetwork
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

class GraphicsUpdaterWB(private val activity: WBActivity) : Runnable {

    private val TAG = "Updater"

    private var network: NeuralNetwork

    private val w: Int
    private val h: Int

    private lateinit var trainer: NetworkTrainerWB
    private lateinit var trainerThread: Thread

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
        trainer = NetworkTrainerWB(activity, network)
        trainerThread = Thread(trainer)
        trainerThread.start()
        while (alive) {
            draw()
        }
    }

    private fun draw() {
        val bitmap = drawPointsOn(createBackground())
        val canvas = Canvas(bitmap)
        val paint = Paint()
        if (activity.nextIsBlack) paint.color = Color.BLACK else paint.color = Color.WHITE
        canvas.drawCircle((w / 2).toFloat(), (h + 50).toFloat(), 100f, paint)
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
                black = 0.3 + black * 0.5
                val br = (255 * black).toInt()
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
            if (p.isBlack) paint.color = Color.BLACK else paint.color = Color.WHITE
            canvas.drawCircle(p.x, p.y, 45F, paint)
        }
        return bitmap
    }

    fun stopTrainer() {
        trainer.alive = false
        trainerThread.join()
    }
}