package hi.dude.touchdrawer.wb

import android.graphics.*
import hi.dude.touchdrawer.GraphicsUpdater
import hi.dude.touchdrawer.NetworkTrainer
import hi.dude.touchdrawer.Point
import hi.dude.touchdrawer.network.NeuralNetwork

class GraphicsUpdaterWB(private val activity: WBActivity) : GraphicsUpdater(2, activity) {

    private val TAG = "Updater"

    override fun drawPoint(canvas: Canvas, paint: Paint, p: Point) {
        paint.color = Color.GRAY
        canvas.drawCircle(p.x, p.y, 35f, paint)
        paint.color = p.color.color
        canvas.drawCircle(p.x, p.y, 30f, paint)
    }

    override fun drawPixel(outputs: DoubleArray, bitmap: Bitmap, i: Int, j: Int) {
        val black = (255 * outputs[0]).toInt()
        bitmap.setPixel(i, j, Color.rgb(black, black, black))
    }

    override fun initTrainer(network: NeuralNetwork): NetworkTrainer {
        return NetworkTrainerWB(activity, network)
    }
}