package hi.dude.touchdrawer

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    enum class Modes {
        WHITE,
        BLACK,
        WHITE_BLACK
    }

    lateinit var view: View
    private lateinit var reset: FloatingActionButton
    private lateinit var whiteBlackBtn: FloatingActionButton
    private lateinit var whiteBtn: FloatingActionButton
    private lateinit var blackBtn: FloatingActionButton
    val points = ArrayList<Point>()

    private lateinit var updater: Updater
    private lateinit var threadUpdater: Thread

    private var ignore = false
    private var nextIsBlack = true
    private var mode = Modes.WHITE_BLACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlack)

        view = findViewById(R.id.main_view)
        view.setOnTouchListener { _, event -> onTouch(event) }

        reset = findViewById(R.id.reset_button)
        whiteBlackBtn = findViewById(R.id.wb_button)
        whiteBtn = findViewById(R.id.w_button)
        blackBtn = findViewById(R.id.b_button)

        reset.setOnClickListener { resetClicked() }
        whiteBlackBtn.setOnClickListener { whiteBlackClicked() }
        blackBtn.setOnClickListener { blackClicked() }
        whiteBtn.setOnClickListener { whiteClicked() }

        updater = Updater(this)
        threadUpdater = Thread(updater)
        threadUpdater.start()
    }

    private fun onTouch(event: MotionEvent?): Boolean {
        Log.i(TAG, "onTouch: x = ${event?.x}, y = ${event?.y}")
        var moved = false
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> ignore = false
            MotionEvent.ACTION_MOVE -> ignore = true
            MotionEvent.ACTION_UP -> {
                if (!ignore) {
                    points.add(Point(event.x, event.y, nextIsBlack))
                    when (mode) {
                        Modes.WHITE_BLACK -> {
                            nextIsBlack = !nextIsBlack
                            if (nextIsBlack)
                                window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlack)
                            else
                                window.statusBarColor = ContextCompat.getColor(this, R.color.colorWhite)
                        }
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
        updater = Updater(this)
        threadUpdater = Thread(updater)
        threadUpdater.start()
    }

    private fun blackClicked() {
        mode = Modes.BLACK
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlack)
        nextIsBlack = true

    }

    private fun whiteClicked() {
        mode = Modes.WHITE
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorWhite)
        nextIsBlack = false
    }

    private fun whiteBlackClicked() {
        mode = Modes.WHITE_BLACK
        if (nextIsBlack)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlack)
        else
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorWhite)
    }
}