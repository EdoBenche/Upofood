package it.benche.upofood

import android.content.Intent
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

        splashImg.animate().translationY((-2000.00).toFloat()).setDuration(1000).startDelay = 3000
        lottieAnimationView.animate().translationY((1400.00).toFloat()).setDuration(1000).startDelay = 3000

        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
                              }, 4000)


    }
}

private fun Timer.schedule(i: Int) {

}


