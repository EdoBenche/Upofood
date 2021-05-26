package it.benche.upofood

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        btn_log_out.setOnClickListener{
            Toast.makeText(
                baseContext, "Click on signOUT btn.",
                Toast.LENGTH_SHORT
            ).show()
            signOut()
            //FirebaseAuth.getInstance().signOut();

            startActivity(Intent(this, LoginActivity::class.java))
        }

        val signout: Button = findViewById(R.id.btn_log_out)
        signout.setOnClickListener({ signOut() })
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this
            ) { startActivity(Intent(this, LoginActivity::class.java)) }
    }
}