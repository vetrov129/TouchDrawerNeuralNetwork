package hi.dude.touchdrawer.wb

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hi.dude.touchdrawer.AbstractDrawActivity
import hi.dude.touchdrawer.enums.Modes
import hi.dude.touchdrawer.R
import hi.dude.touchdrawer.enums.PColor
import hi.dude.touchdrawer.rgb.RGBActivity

class WBActivity : AbstractDrawActivity() {

    private val TAG = "MainActivity"

    override lateinit var view: View

    private lateinit var toRgbBtn: FloatingActionButton
    private lateinit var updateBtn: FloatingActionButton
    private lateinit var reset: FloatingActionButton

    private lateinit var whiteBlackBtn: FloatingActionButton
    private lateinit var whiteBtn: FloatingActionButton
    private lateinit var blackBtn: FloatingActionButton

    override var nextColor = PColor.BLACK
    override var currentMode = Modes.WHITE_BLACK

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

        whiteBlackBtn.setOnClickListener { changeMode(PColor.BLACK, Modes.WHITE_BLACK) }
        blackBtn.setOnClickListener { changeMode(PColor.BLACK) }
        whiteBtn.setOnClickListener { changeMode(PColor.WHITE) }
    }

    private fun toRgbClicked() {
        stopUpdater()
        startActivity(Intent(this, RGBActivity::class.java))
    }

    override fun initUpdater() {
        updater = GraphicsUpdaterWB(this)
    }

    override fun onBackPressed() {
        stopUpdater()
        super.onBackPressed()
    }
}