package hi.dude.touchdrawer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.DisplayMetrics
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import hi.dude.touchdrawer.network.NeuralNetwork
import kotlin.math.exp

abstract class GraphicsUpdater(
    countOuts: Int,
    private val activity: AbstractDrawActivity
) : Runnable {

    private var network: NeuralNetwork

    private val w: Int
    private val h: Int

    private lateinit var trainer: NetworkTrainer
    private lateinit var trainerThread: Thread

    var alive = true

    init {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        w = metrics.widthPixels
        h = metrics.heightPixels

        val sigmoid = { x: Double -> 1 / (1 + exp(-x)) }
        val dsigmoid = { y: Double -> y * (1 - y) }
        network = NeuralNetwork(0.01, sigmoid, dsigmoid, 2, 5, 5, countOuts)
    }

    override fun run() {
        trainer = initTrainer(network)
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
        drawNextColor(paint, canvas)
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

                drawPixel(outputs, bitmap, i, j)
            }
        }
        return bitmap
    }

    private fun drawPointsOn(source: Bitmap): Bitmap {
        val bitmap = source.scale(w, h)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        for (p in activity.points) {
            drawPoint(canvas, paint, p)
        }
        return bitmap
    }

    fun stopTrainer() {
        trainer.alive = false
        trainerThread.join()
    }

    private fun drawNextColor(paint: Paint, canvas: Canvas) {
        paint.color = Color.GRAY
        canvas.drawCircle((w / 2).toFloat(), (h + 50).toFloat(), 107f, paint)
        paint.color = activity.nextColor.color
        canvas.drawCircle((w / 2).toFloat(), (h + 50).toFloat(), 100f, paint)
    }

    abstract fun drawPoint(canvas: Canvas, paint: Paint, p: Point)

    abstract fun drawPixel(outputs: DoubleArray, bitmap: Bitmap, i: Int, j: Int)

    abstract fun initTrainer(network: NeuralNetwork): NetworkTrainer
}