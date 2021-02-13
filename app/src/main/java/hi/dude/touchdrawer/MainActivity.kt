package hi.dude.touchdrawer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    lateinit var view: View
    val points = ArrayList<Point>()

    private var ignore = false
    private var isBlack = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlack)

        view = findViewById(R.id.main_view)
        view.setOnTouchListener { _, event -> onTouch(event) }

        Thread(Updater(this)).start()
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


}