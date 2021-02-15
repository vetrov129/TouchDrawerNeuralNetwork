package hi.dude.touchdrawer

import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import hi.dude.touchdrawer.enums.Modes
import hi.dude.touchdrawer.enums.PColor
import kotlin.random.Random

abstract class AbstractDrawActivity: AppCompatActivity() {

    abstract var view: View
    abstract var nextColor : PColor
    abstract var currentMode: Modes

    val points = ArrayList<Point>()
    private var ignore = false

    protected lateinit var updater: GraphicsUpdater
    protected lateinit var threadUpdater: Thread

    abstract fun initUpdater()

    override fun onResume() {
        initUpdater()
        threadUpdater = Thread(updater)
        threadUpdater.start()
        super.onResume()
    }

    private fun startUpdater() {
        initUpdater()
        threadUpdater = Thread(updater)
        threadUpdater.start()
    }

    protected fun stopUpdater() {
        updater.stopTrainer()
        updater.alive = false
        threadUpdater.join()
    }

    protected fun updateClicked() {
        stopUpdater()
        startUpdater()
    }

    protected fun resetClicked() {
        stopUpdater()
        points.clear()
        startUpdater()
    }

    protected fun onTouch(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> ignore = false
            MotionEvent.ACTION_MOVE -> ignore = true
            MotionEvent.ACTION_UP -> if (!ignore) {
                points.add(Point(event.x, event.y, nextColor))
                setNextColor()
            }
        }
        return true
    }

    protected fun changeMode(color: PColor, mode: Modes = Modes.ONE) {
        currentMode = mode
        nextColor = color
    }

    private fun setNextColor() {
        when (currentMode) {
            Modes.ONE -> {}
            Modes.ALL -> nextColor = PColor.values()[Random.nextInt(7)]
            Modes.RGB -> {
                nextColor = when (nextColor) {
                    PColor.RED -> PColor.GREEN
                    PColor.GREEN -> PColor.BLUE
                    PColor.BLUE -> PColor.RED
                    else -> PColor.GREEN
                }
            }
            Modes.WHITE_BLACK -> {
                nextColor = when (nextColor) {
                    PColor.WHITE -> PColor.BLACK
                    PColor.BLACK -> PColor.WHITE
                    else -> PColor.BLACK
                }
            }
            else -> {}
        }
    }
}