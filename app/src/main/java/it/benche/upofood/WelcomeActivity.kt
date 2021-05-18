package it.benche.upofood

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.appintro.*
import java.util.jar.Manifest

class WelcomeActivity : AppIntro() {

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Make sure you don't call setContentView!

        // Call addSlide passing your Fragments.
        // You can use AppIntroFragment to use a pre-built fragment
        addSlide(AppIntroFragment.newInstance(
            title = "Benvenuto su Upofood!",
            description = "Il servizio che ti porta il cibo (e non solo...) direttamente a casa",
            imageDrawable = R.drawable.ic_icon_welcome,
            //backgroundDrawable = R.drawable.ic_appintro_fab_background,
            titleColor = getColor(R.color.md_orange_500),
            descriptionColor = Color.BLACK,
            backgroundColor = Color.WHITE,
            //titleTypefaceFontRes = R.font.opensans_regular,
            //descriptionTypefaceFontRes = R.font.opensans_regular,

        ))

        addSlide(AppIntroFragment.newInstance(
                title = "Geolocalizzazione",
                description = "Per prima cosa ti chiedo il permesso per la geolocalizzazione",
                imageDrawable = R.drawable.ic_geolocalization,
                //backgroundDrawable = R.drawable.ic_appintro_fab_background,
                titleColor = getColor(R.color.md_orange_500),
                descriptionColor = Color.BLACK,
                backgroundColor = getColor(R.color.md_orange_100),
                //titleTypefaceFontRes = R.font.opensans_regular,
                //descriptionTypefaceFontRes = R.font.opensans_regular,

        ))

        addSlide(AppIntroFragment.newInstance(
                title = "Notifiche",
                description = "Abilita il permesso alle notifiche così resterai in contatto con gli altri utenti",
                imageDrawable = R.drawable.ic_baseline_notifications_active_24,
                //backgroundDrawable = R.drawable.ic_appintro_fab_background,
                titleColor = getColor(R.color.md_orange_500),
                descriptionColor = Color.BLACK,
                backgroundColor = getColor(R.color.md_orange_100),
                //titleTypefaceFontRes = R.font.opensans_regular,
                //descriptionTypefaceFontRes = R.font.opensans_regular,

        ))

        addSlide(AppIntroFragment.newInstance(
                title = "Fotocamera",
                description = "Ci serve la fotocamera per scattare la foto ai prodotti qualora tu fossi un gestore",
                imageDrawable = R.drawable.ic_camera,
                //backgroundDrawable = R.drawable.ic_appintro_fab_background,
                titleColor = getColor(R.color.md_orange_500),
                descriptionColor = Color.BLACK,
                backgroundColor = getColor(R.color.md_orange_100),
                //titleTypefaceFontRes = R.font.opensans_regular,
                //descriptionTypefaceFontRes = R.font.opensans_regular,

        ))

        AppIntroCustomLayoutFragment.newInstance(R.layout.intro_custom_layout1)
        addSlide(AppIntroFragment.newInstance(
            title = "Ora sei pronto ad usare Upofood!",
            description = "Buona spesa \uD83D\uDE1C",
                imageDrawable = R.drawable.ic_check,
                titleColor = getColor(R.color.md_orange_500),
                descriptionColor = Color.BLACK,
                backgroundColor = getColor(R.color.md_orange_100)
        ))
        setTransformer(
            AppIntroPageTransformerType.Parallax(
            titleParallaxFactor = 1.0,
            imageParallaxFactor = -1.0,
            descriptionParallaxFactor = 2.0
        ))

        isColorTransitionsEnabled = true

        setIndicatorColor(
                selectedIndicatorColor = getColor(R.color.md_orange_500),
                unselectedIndicatorColor = getColor(R.color.md_orange_500)
        )
        isWizardMode = true

        isVibrate = true
        vibrateDuration = 50L

        askForPermissions(
                permissions = arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION),
                slideNumber = 2,
                required = true)
        askForPermissions(
                permissions = arrayOf(android.Manifest.permission.ACCESS_NOTIFICATION_POLICY),
                slideNumber = 3,
                required = true)
        askForPermissions(
                permissions = arrayOf(android.Manifest.permission.CAMERA),
                slideNumber = 4,
                required = true)
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    override fun onUserDeniedPermission(permissionName: String) {
        // User pressed "Deny" on the permission dialog
    }
    override fun onUserDisabledPermission(permissionName: String) {
        // User pressed "Deny" + "Don't ask again" on the permission dialog
    }

}