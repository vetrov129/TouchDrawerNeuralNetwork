package hi.dude.touchdrawer.wb

import hi.dude.touchdrawer.NetworkTrainer
import hi.dude.touchdrawer.Point
import hi.dude.touchdrawer.network.NeuralNetwork
import hi.dude.touchdrawer.enums.PColor

class NetworkTrainerWB(activity: WBActivity, outerNetwork: NeuralNetwork) : NetworkTrainer(2, activity, outerNetwork) {

    override fun getResults(point: Point): DoubleArray {
        val targets = DoubleArray(2)
        if (point.color == PColor.BLACK) targets[0] = 1.0
        else targets[1] = 1.0
        return targets
    }
}