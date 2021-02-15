package hi.dude.touchdrawer.rgb

import hi.dude.touchdrawer.NetworkTrainer
import hi.dude.touchdrawer.enums.PColor
import hi.dude.touchdrawer.Point
import hi.dude.touchdrawer.network.NeuralNetwork

class NetworkTrainerRGB(activity: RGBActivity, outerNetwork: NeuralNetwork) :
    NetworkTrainer(3, activity, outerNetwork) {
    override fun getResults(point: Point): DoubleArray {
        val targets = DoubleArray(3)
        when (point.color) {
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
        return targets
    }
}