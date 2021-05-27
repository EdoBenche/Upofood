package it.benche.upofood

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import java.util.*

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {

    private lateinit var splashImg : ImageView
    private lateinit var lottieAnimationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashImg = findViewById(R.id.img)
        lottieAnimationView = findViewById(R.id.lottie)

        loadLocate()

        splashImg.animate().translationY((-2000.00).toFloat()).setDuration(1000).startDelay = 3000
        lottieAnimationView.animate().translationY((1400.00).toFloat()).setDuration(1000).startDelay = 3000

        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
                              }, 4000)


    }
    private fun loadLocate() {
        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        setLocate(language!!)
    }
    private fun setLocate(Lang:String) {
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", Lang)
        editor.apply()
    }

}


