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
import hi.dude.touchdrawer.GraphicsUpdater
import hi.dude.touchdrawer.NetworkTrainer
import hi.dude.touchdrawer.Point
import hi.dude.touchdrawer.network.NeuralNetwork
import kotlin.math.exp

class GraphicsUpdaterRGB(private val activity: RGBActivity) : GraphicsUpdater(3, activity) {

    private val TAG = "Updater"

    override fun drawPoint(canvas: Canvas, paint: Paint, p: Point) {
        paint.color = Color.GRAY
        canvas.drawCircle(p.x, p.y, 35f, paint)
        paint.color = p.color.color
        canvas.drawCircle(p.x, p.y, 30f, paint)
    }

    override fun drawPixel(outputs: DoubleArray, bitmap: Bitmap, i: Int, j: Int) {
        val red = (255 * outputs[0]).toInt()
        val green = (255 * outputs[1]).toInt()
        val blue = (255 * outputs[2]).toInt()

        bitmap.setPixel(i, j, Color.rgb(red, green, blue))
    }

    override fun initTrainer(network: NeuralNetwork): NetworkTrainer {
        return NetworkTrainerRGB(activity, network)
    }
}