package hi.dude.touchdrawer.wb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hi.dude.touchdrawer.R
import hi.dude.touchdrawer.rgb.GraphicsUpdaterRGB
import hi.dude.touchdrawer.rgb.RGBActivity

class WBActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    enum class Modes {
        WHITE,
        BLACK,
        WHITE_BLACK
    }

    lateinit var view: View

    private lateinit var toRgbBtn: FloatingActionButton
    private lateinit var updateBtn: FloatingActionButton
    private lateinit var reset: FloatingActionButton

    private lateinit var whiteBlackBtn: FloatingActionButton
    private lateinit var whiteBtn: FloatingActionButton
    private lateinit var blackBtn: FloatingActionButton
    val points = ArrayList<WBPoint>()

    private lateinit var updater: GraphicsUpdaterWB
    private lateinit var threadUpdater: Thread

    private var ignore = false
    var nextIsBlack = true
    private var mode = Modes.WHITE_BLACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wb)

        supportActionBar?.hide()

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorNotSelected)

        view = findViewById(R.id.main_view)
        view.setOnTouchListener { _, event -> onTouch(event) }

        toRgbBtn = findViewById(R.id.to_rgb_button)
        updateBtn = findViewById(R.id.update_button)
        reset = findViewById(R.id.reset_button)

        toRgbBtn.setOnClickListener { toRgbClicked() }
        updateBtn.setOnClickListener { updateClicked() }
        reset.setOnClickListener { resetClicked() }

        whiteBlackBtn = findViewById(R.id.wb_button)
        whiteBtn = findViewById(R.id.w_button)
        blackBtn = findViewById(R.id.b_button)

        whiteBlackBtn.setOnClickListener { whiteBlackClicked() }
        blackBtn.setOnClickListener { blackClicked() }
        whiteBtn.setOnClickListener { whiteClicked() }

        updater = GraphicsUpdaterWB(this)
        threadUpdater = Thread(updater)
        threadUpdater.start()
    }

    private fun onTouch(event: MotionEvent?): Boolean {
        Log.i(TAG, "onTouch: x = ${event?.x}, y = ${event?.y}")
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> ignore = false
            MotionEvent.ACTION_MOVE -> ignore = true
            MotionEvent.ACTION_UP -> {
                if (!ignore) {
                    points.add(WBPoint(event.x, event.y, nextIsBlack))
                    when (mode) {
                        Modes.WHITE_BLACK -> nextIsBlack = !nextIsBlack
                        Modes.BLACK -> {}
                        Modes.WHITE -> {}
                    }
                }
            }
        }
        return true
    }

    private fun resetClicked() {
        updater.alive = false
        threadUpdater.join()
        points.clear()
        startUpdater()
    }

    private fun blackClicked() {
        mode = Modes.BLACK
        nextIsBlack = true
    }

    private fun whiteClicked() {
        mode = Modes.WHITE
        nextIsBlack = false
    }

    private fun whiteBlackClicked() {
        mode = Modes.WHITE_BLACK
    }

    override fun onBackPressed() {
        updater.stopTrainer()
        updater.alive = false
        threadUpdater.join()
        super.onBackPressed()
    }

    private fun updateClicked() {
        updater.stopTrainer()
        updater.alive = false
        threadUpdater.join()
        startUpdater()
    }

    private fun startUpdater() {
        updater = GraphicsUpdaterWB(this)
        threadUpdater = Thread(updater)
        threadUpdater.start()
    }

    private fun toRgbClicked() {
        updater.stopTrainer()
        updater.alive = false
        points.clear()
        startActivity(Intent(this, RGBActivity::class.java))
    }
}