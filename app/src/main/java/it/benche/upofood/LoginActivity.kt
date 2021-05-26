package it.benche.upofood

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.button_signin
import kotlinx.android.synthetic.main.activity_login.et_password
import kotlinx.android.synthetic.main.activity_login.loadingPanel
import kotlinx.android.synthetic.main.activity_requests.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.w3c.dom.Text


class LoginActivity : AppCompatActivity() {



    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    //lateinit var signin: SignInButton
    private val RC_SIGN_IN = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setStatusBarTransparent(this@LoginActivity)

        checkFirstRun()

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance()

        button_signin.setOnClickListener{
            loginInUser()
        }

        //signin = findViewById(R.id.sign_in_button)
    }

    public override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if(currentUser != null) {
            var uid = mAuth.uid

            db.collection("users")
                    .get()
                    .addOnSuccessListener { result ->
                        for(document in result) {
                            if(document.id == uid.toString()) {
                                if(document.getString("account").toString() == "Rider") {
                                    setRiderAvailable(document.id)
                                    Handler().postDelayed({startActivity(Intent(this, DrawerActivityRider2::class.java))}, 500)
                                }
                                else if(document.getString("account").toString() == "Cliente") {
                                    startActivity(Intent(this, DrawerActivityClient::class.java))
                                }
                                else if(document.getString("account").toString() == "Gestore") {
                                    startActivity(Intent(this, DrawerActivity::class.java))
                                }
                            }
                        }
                    }
            //startActivity(Intent(this, DrawerActivity::class.java))
        } else {
        loadingPanel.visibility = RelativeLayout.GONE }
    }

    override fun onBackPressed() {

    }


    private fun setRiderAvailable(id: String) {
        db.collection("users")
                .document(id)
                .update(mapOf("available" to "yes"))
    }

    private fun setStatusBarTransparent(activity: AppCompatActivity){
        //Make Status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        //Make status bar icons color dark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.statusBarColor = Color.WHITE
        }
    }

    fun onClick(view: View) {
        @Override
        if(view.id == R.id.button_signup){
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
        } else if(view.id == R.id.button_forgot_password){
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
        else if(view.id == R.id.button_signin) {
            loginInUser()
        }

    }

    private fun loginInUser() {
        mAuth.signInWithEmailAndPassword(et_username.text.toString(), et_password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth.currentUser
                        var uid = mAuth.uid

                        db.collection("users")
                                .get()
                                .addOnSuccessListener { result ->
                                    for(document in result) {
                                        if(document.id == uid.toString()) {
                                            if(document.getString("account").toString() == "Rider") {
                                                setRiderAvailable(document.id)
                                                Handler().postDelayed({startActivity(Intent(this, DrawerActivityRider2::class.java))}, 500)
                                            }
                                            else if(document.getString("account").toString() == "Cliente") {
                                                startActivity(Intent(this, DrawerActivityClient::class.java))
                                            }
                                            else if(document.getString("account").toString() == "Gestore") {
                                                startActivity(Intent(this, DrawerActivity::class.java))
                                            }
                                        }
                                    }
                                }

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            this, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        //fai qualcosa
                    }

                    // ...
                }
    }

    //Controlla se l'app viene aperta per la prima volta o no
    private fun checkFirstRun() {
        val PREFS_NAME: String = "Upofood"
        var PREF_VERSION_CODE_KEY = "version_code"
        val DOESNT_EXIST = -1

        val currentVersionCode = BuildConfig.VERSION_CODE

        val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val savedVersionCode: Int = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST)

        if(currentVersionCode == savedVersionCode) {
            return
        }
        else if(savedVersionCode == DOESNT_EXIST) {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }

        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    private fun loginWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        if(account != null) {
            startActivity(Intent(this, DrawerActivity::class.java))
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            addDataInCloud()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
    }

    private fun addDataInCloud() {
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            val personPhoto: Uri? = acct.photoUrl

            // Create a new user with a first and last name
            var mSpinner: Spinner = findViewById(R.id.spinner)

            val user = hashMapOf(
                "nome" to personName,
                "cognome" to personFamilyName,
                "indirizzo" to "",
                "account" to mSpinner.selectedItem.toString(),
                "telefono" to et_phone.text.toString()
            )


// Add a new document with a generated ID

// Add a new document with a generated ID
            val TAG: String = "SignupActivity";
            var uid = mAuth.uid
            if (uid != null) {
                db.collection("users")
                    .document(uid)
                    .set(user)
                    .addOnSuccessListener { Log.d(TAG, "Utente aggiunto correttamente")}
                    .addOnFailureListener(OnFailureListener { e ->
                        Log.w(
                            TAG,
                            "Error adding document",
                            e
                        )
                    })
            }
        }



    }
}



