package android.example.health

import android.content.Intent
import android.example.health.Constants.GlobalData
import android.example.health.daos.UserDao
import android.example.health.models.User
import android.example.health.networkUtils.CheckNetworkConnection
import android.example.health.utils.LocalPersistenceManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class SignupScreen : AppCompatActivity() {

    private lateinit var signup_btn: MaterialButton

    private var mLastClickTime: Long = 0
    private val mClickWaitTime: Long = 3000
    private lateinit var edit_text_FullName: TextInputEditText
    private lateinit var edit_text_email_id: TextInputEditText
    private lateinit var edit_text_mobile: TextInputEditText
    private lateinit var edit_text_password: TextInputEditText
    private val TAG = SignupScreen::class.java.simpleName
    private val RC_SIGN_IN = 1234
    private lateinit var googleSignIn: GoogleSignInClient

    private lateinit var EmailRegex: String
    private lateinit var EmailPattern: Pattern
    private lateinit var PasswordRegex: String
    private lateinit var PasswordPattern: Pattern
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_signup_screen)
        val client = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        auth = Firebase.auth
        googleSignIn = GoogleSignIn.getClient(this, client)
        signup_btn = findViewById<View>(R.id.signup_next_btn) as MaterialButton
        edit_text_FullName = findViewById<View>(R.id.signup_full_name) as TextInputEditText
        edit_text_email_id = findViewById<View>(R.id.signup_email_id) as TextInputEditText
        edit_text_mobile = findViewById<View>(R.id.signup_mobile_no) as TextInputEditText
        edit_text_password = findViewById<View>(R.id.signup_password) as TextInputEditText
        (findViewById<View>(R.id.google_button) as LinearLayout).setOnClickListener (View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < mClickWaitTime) {
                return@OnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (CheckNetworkConnection.isNetworkAvailable(this@SignupScreen)) {
                signIn()
            } else {
                GlobalData.showSnackbar(
                    this@SignupScreen,
                    findViewById<View>(android.R.id.content),
                    "No Internet Connection",
                    GlobalData.ColorType.ERROR
                )
            }
        })
        signup_btn.setOnClickListener(View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < mClickWaitTime) {
                return@OnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (CheckNetworkConnection.isNetworkAvailable(this@SignupScreen)) {
                signupMethod(edit_text_email_id.text.toString(), edit_text_password.text.toString())
            } else {
                GlobalData.showSnackbar(
                    this@SignupScreen,
                    findViewById<View>(android.R.id.content),
                    "No Internet Connection",
                    GlobalData.ColorType.ERROR
                )
            }
        })
    }

    private fun isAllFieldValidated(): Boolean {
        EmailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$"
        EmailPattern = Pattern.compile(EmailRegex)
        PasswordRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$"
        PasswordPattern = Pattern.compile(PasswordRegex)
        var isAllFieldValid = true
        if (edit_text_FullName.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@SignupScreen,
                findViewById<View>(android.R.id.content),
                "Enter Full Name",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        } else if (edit_text_email_id.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@SignupScreen,
                findViewById<View>(android.R.id.content),
                "Enter Email ID",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        } else if (!EmailPattern.matcher(edit_text_email_id.text.toString().trim { it <= ' ' })
                .matches()
        ) {
            GlobalData.showSnackbar(
                this@SignupScreen,
                findViewById<View>(android.R.id.content),
                "Entered Email ID is not in proper format",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        } else if (edit_text_mobile.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@SignupScreen,
                findViewById<View>(android.R.id.content),
                "Enter Mobile  Number",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        } else if (edit_text_password.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@SignupScreen,
                findViewById<View>(android.R.id.content),
                "Enter Password",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        } else if (edit_text_password.text.toString().trim { it <= ' ' }.length < 6) {
            GlobalData.showSnackbar(
                this@SignupScreen,
                findViewById<View>(android.R.id.content),
                "Password Length should be more than 6",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        }
        return isAllFieldValid
    }

    private fun signupMethod(email : String, password : String) {
        try {
            if (isAllFieldValidated()) {

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                        task ->
                    if(task.isSuccessful){
                        val user = auth.currentUser
                        updateUI(user)
                    }
                    else{
                        if(task.exception?.toString() == "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account."){
                            GlobalData.showSnackbar(
                                this@SignupScreen,
                                findViewById(android.R.id.content),
                                "The email address is already in use by another account",
                                GlobalData.ColorType.ERROR
                            )
                            updateUI(null)
                        }
                        else {
                            GlobalData.showSnackbar(
                                this@SignupScreen,
                                findViewById(android.R.id.content),
                                "Invalid email or password",
                                GlobalData.ColorType.ERROR
                            )
                        }
                        updateUI(null)
                    }

                }

            }
        } catch (e: Exception) {
            GlobalData.showSnackbar(
                this@SignupScreen,
                findViewById(android.R.id.content),
                "Please try again",
                GlobalData.ColorType.ERROR
            )
            updateUI(null)
        }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if(firebaseUser != null) {
                val userName = edit_text_FullName.text.toString()
            val user = User(
                firebaseUser.uid,
                userName,
                firebaseUser.photoUrl.toString(),
                edit_text_mobile.text.toString()
            )
            LocalPersistenceManager.getLocalPersistenceManager().userName = userName
            val usersDao = UserDao()
            usersDao.addUser(user)

            val welcomeIntent = Intent(this@SignupScreen, LoginScreen::class.java)
            startActivity(welcomeIntent)
            finish()

        }

    }

    private fun signIn() {
        val signInIntent = googleSignIn.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
        try {
            val account =
                task?.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        }
        catch (e: ApiException){

            GlobalData.showSnackbar(
                this@SignupScreen,
                findViewById(android.R.id.content),
                "Please try again",
                GlobalData.ColorType.ERROR
            )

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        //Sign-in
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main) {
                updateGoogleUI(firebaseUser)
            }
        }

    }

    private fun updateGoogleUI(firebaseUser: FirebaseUser?) {
        if(firebaseUser != null) {
            val user = User(
                firebaseUser.uid,
                firebaseUser.displayName.toString(),
                firebaseUser.photoUrl.toString(),
                firebaseUser.phoneNumber
            )
            val usersDao = UserDao()
            LocalPersistenceManager.getLocalPersistenceManager().userName = firebaseUser.displayName.toString()
            usersDao.addUser(user)

            val mainActivityIntent = Intent(this@SignupScreen, HomeScreenActivity::class.java)
            startActivity(mainActivityIntent)
            finish()

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        googleSignIn.signOut().addOnCompleteListener(this) {
            task ->

            if(task.isSuccessful){
                updateGoogleUI(null)
            }
            else {
                GlobalData.showSnackbar(
                    this@SignupScreen,
                    findViewById(android.R.id.content),
                    "Please try again",
                    GlobalData.ColorType.ERROR
                )
            }
        }
    }





}