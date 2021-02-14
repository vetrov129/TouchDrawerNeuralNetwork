package hi.dude.touchdrawer.wb

import android.util.DisplayMetrics
import hi.dude.touchdrawer.network.NeuralNetwork
import kotlin.math.exp

class NetworkTrainerWB(private val activity: WBActivity, private val outerNetwork: NeuralNetwork) : Runnable {

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
        innerNetwork = NeuralNetwork(0.01, sigmoid, dsigmoid, 2, 5, 5, 2)
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

            val targets = DoubleArray(2)
            if (p.isBlack) targets[0] = 1.0
            else targets[1] = 1.0

            innerNetwork.backpropagation(targets)

            synchronized(outerNetwork) {
                outerNetwork.setWeights(innerNetwork)
            }
        }
    }
}