package hi.dude.touchdrawer

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    lateinit var view: View
    val points = ArrayList<Point>()

    private var ignore = false
    private var isBlack = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view = findViewById(R.id.main_view)
        view.setOnTouchListener { _, event -> onTouch(event) }

        Thread(Updater(this)).start()
//
    //        Updater(this).run()
//
    //        runOnUiThread(Updater(this))
//
    //        val thread = Thread {
//            synchronized(this) {
//                runOnUiThread(Updater(this))
//            }
//        }
//        thread.start()


    }

    private fun onTouch(event: MotionEvent?): Boolean {
        Log.i(TAG, "onTouch: x = ${event?.x}, y = ${event?.y}")
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {}
            MotionEvent.ACTION_MOVE -> ignore = true
            MotionEvent.ACTION_UP -> {
                points.add(Point(event.x, event.y, isBlack))
                isBlack = !isBlack
            }
        }
        return true
    }


}