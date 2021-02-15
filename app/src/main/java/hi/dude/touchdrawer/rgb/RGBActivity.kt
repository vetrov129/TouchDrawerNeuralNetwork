package hi.dude.touchdrawer.rgb

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hi.dude.touchdrawer.*
import hi.dude.touchdrawer.enums.Modes
import hi.dude.touchdrawer.enums.PColor
import hi.dude.touchdrawer.wb.WBActivity

class RGBActivity : AbstractDrawActivity() {

    private val TAG = "RGBActivity"

    override lateinit var view: View
    private lateinit var wbBtn: FloatingActionButton
    private lateinit var updateBtn: FloatingActionButton
    private lateinit var reset: FloatingActionButton

    private lateinit var redBtn: FloatingActionButton
    private lateinit var greenBtn: FloatingActionButton
    private lateinit var blueBtn: FloatingActionButton
    private lateinit var yellowBtn: FloatingActionButton
    private lateinit var cyanBtn: FloatingActionButton
    private lateinit var magentaBtn: FloatingActionButton
    private lateinit var blackBtn: FloatingActionButton
    private lateinit var whiteBtn: FloatingActionButton

    private lateinit var allBtn: FloatingActionButton
    private lateinit var rgbBtn: FloatingActionButton

    override var nextColor = PColor.RED
    override var currentMode = Modes.RGB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rgb)

        supportActionBar?.hide()

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorNotSelected)

        view = findViewById(R.id.rgb_view)
        view.setOnTouchListener { _, event -> onTouch(event) }

        wbBtn = findViewById(R.id.to_wb_button_rgb)
        updateBtn = findViewById(R.id.update_button_rgb)
        reset = findViewById(R.id.reset_button_rgb)

        wbBtn.setOnClickListener { toWhiteBlackClicked() }
        updateBtn.setOnClickListener { updateClicked() }
        reset.setOnClickListener { resetClicked() }

        redBtn = findViewById(R.id.red_button_rgb)
        greenBtn = findViewById(R.id.green_button_rgb)
        blueBtn = findViewById(R.id.blue_button_rgb)
        yellowBtn = findViewById(R.id.yellow_button_rgb)
        cyanBtn = findViewById(R.id.cyan_button_rgb)
        magentaBtn = findViewById(R.id.magenta_button_rgb)
        blackBtn = findViewById(R.id.black_button_rgb)
        whiteBtn = findViewById(R.id.white_button_rgb)

        allBtn = findViewById(R.id.all_button_rgb)
        rgbBtn = findViewById(R.id.rgb_button_rgb)

        redBtn.setOnClickListener { changeMode(PColor.RED) }
        greenBtn.setOnClickListener { changeMode(PColor.GREEN)}
        blueBtn.setOnClickListener { changeMode(PColor.BLUE)}
        yellowBtn.setOnClickListener { changeMode(PColor.YELLOW)}
        cyanBtn.setOnClickListener { changeMode(PColor.CYAN)}
        magentaBtn.setOnClickListener { changeMode(PColor.MAGENTA)}
        blackBtn.setOnClickListener { changeMode(PColor.BLACK)}
        whiteBtn.setOnClickListener { changeMode(PColor.WHITE)}

        allBtn.setOnClickListener { changeMode(PColor.YELLOW, Modes.ALL) }
        rgbBtn.setOnClickListener { changeMode(PColor.RED, Modes.RGB) }
    }

    private fun toWhiteBlackClicked() {
        stopUpdater()
        startActivity(Intent(this, WBActivity::class.java))
    }

    override fun initUpdater() {
        updater = GraphicsUpdaterRGB(this)
    }
}