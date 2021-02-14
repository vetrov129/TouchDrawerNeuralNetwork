package hi.dude.touchdrawer.rgb

import android.util.DisplayMetrics
import hi.dude.touchdrawer.network.NeuralNetwork
import kotlin.math.exp

class NetworkTrainerRGB(private val activity: RGBActivity, private val outerNetwork: NeuralNetwork) : Runnable {

    private val w: Int
    private val h: Int
    private val innerNetwork: NeuralNetwork

    var alive = true

    init {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        w = metrics.widthPixels
        h = metrics.heightPixels

        val sigmoid = { x: Double -> 1 / (1 + exp(-x)) }
        val dsigmoid = { y: Double -> y * (1 - y) }
        innerNetwork = NeuralNetwork(0.01, sigmoid, dsigmoid, 2, 5, 5, 3)
    }

    override fun run() {
        while (alive) train()
    }

    private fun train() {
        if (activity.points.size > 0) {
            val p = activity.points[(Math.random() * activity.points.size).toInt()] ?: return
            val nx = p.x / w - 0.5
            val ny = p.y / h - 0.5

            innerNetwork.feedForward(doubleArrayOf(nx, ny))

            val targets = DoubleArray(3)
//            if (p.isBlack) targets[0] = 1.0
//            else targets[1] = 1.0

//            targets[p.color.ordinal] = 1.0

            when (p.color) {
                PColor.RED -> {
                    targets[0] = 1.0
                    targets[1] = 0.0
                    targets[2] = 0.0
                }
                PColor.GREEN -> {
                    targets[0] = 0.0
                    targets[1] = 1.0
                    targets[2] = 0.0
                }
                PColor.BLUE -> {
                    targets[0] = 0.0
                    targets[1] = 0.0
                    targets[2] = 1.0
                }
                PColor.YELLOW -> {
                    targets[0] = 1.0
                    targets[1] = 1.0
                    targets[2] = 0.0
                }
                PColor.CYAN -> {
                    targets[0] = 0.0
                    targets[1] = 1.0
                    targets[2] = 1.0
                }
                PColor.MAGENTA -> {
                    targets[0] = 1.0
                    targets[1] = 0.0
                    targets[2] = 1.0
                }
                PColor.WHITE -> {
                    targets[0] = 1.0
                    targets[1] = 1.0
                    targets[2] = 1.0
                }
                PColor.BLACK -> {
                    targets[0] = 0.0
                    targets[1] = 0.0
                    targets[2] = 0.0
                }
            }
            innerNetwork.backpropagation(targets)

            synchronized(outerNetwork) {
                outerNetwork.setWeights(innerNetwork)
            }
        }
    }
}