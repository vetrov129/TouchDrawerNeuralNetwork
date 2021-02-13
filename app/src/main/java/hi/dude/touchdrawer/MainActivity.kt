package hi.dude.touchdrawer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    lateinit var view: View
    private lateinit var reset: FloatingActionButton
    val points = ArrayList<Point>()

    private lateinit var updater: Updater
    private lateinit var threadUpdater: Thread

    private var ignore = false
    private var isBlack = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlack)

        view = findViewById(R.id.main_view)
        view.setOnTouchListener { _, event -> onTouch(event) }

        reset = findViewById(R.id.reset_button)
        reset.setOnClickListener { resetClicked() }

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
                    points.add(Point(event.x, event.y, isBlack))
                    isBlack = !isBlack
                    if (isBlack)
                        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlack)
                    else
                        window.statusBarColor = ContextCompat.getColor(this, R.color.colorWhite)
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
}