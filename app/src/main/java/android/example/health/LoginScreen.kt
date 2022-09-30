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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
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

class LoginScreen : AppCompatActivity() {

    private lateinit var btnLogin: MaterialButton
    private lateinit var textViewForgetPassword: AppCompatTextView

    private var mLastClickTime: Long = 0
    private val mClickWaitTime: Long = 3000
    private lateinit var editTextEmailId: TextInputEditText
    private lateinit var editTextLoginPassword: TextInputEditText
    private val TAG = LoginScreen::class.java.simpleName

    private lateinit var EmailRegex: String
    private lateinit var EmailPattern: Pattern

    private lateinit var googleSignIn: GoogleSignInClient
    private val RC_SIGN_IN = 1234
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        val client = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        auth = Firebase.auth
        googleSignIn = GoogleSignIn.getClient(this, client)
        btnLogin = findViewById<View>(R.id.btn_login) as MaterialButton
        editTextEmailId = findViewById<View>(R.id.login_email_id) as TextInputEditText
        editTextLoginPassword = findViewById<View>(R.id.login_password) as TextInputEditText
        textViewForgetPassword =
            findViewById<View>(R.id.txt_view_forgetPassword) as AppCompatTextView

        (findViewById<View>(R.id.google_button) as LinearLayout).setOnClickListener (View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < mClickWaitTime) {
                return@OnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (CheckNetworkConnection.isNetworkAvailable(this@LoginScreen)) {
                signIn()
            } else {
                GlobalData.showSnackbar(
                    this@LoginScreen,
                    findViewById<View>(android.R.id.content),
                    "No Internet Connection",
                    GlobalData.ColorType.ERROR
                )
            }
        })

        btnLogin.setOnClickListener(View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < mClickWaitTime) {
                return@OnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (CheckNetworkConnection.isNetworkAvailable(this@LoginScreen)) {
                loginMethod(editTextEmailId.text.toString(), editTextLoginPassword.text.toString())
            } else {
                GlobalData.showSnackbar(
                    this@LoginScreen,
                    findViewById(android.R.id.content),
                    "No Internet Connection",
                    GlobalData.ColorType.ERROR
                )
            }
        })
        textViewForgetPassword.setOnClickListener(View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < mClickWaitTime) {
                return@OnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this@LoginScreen, ForgotPasswordActivity::class.java)
            startActivity(intent)
        })
        //editTextEmailId.setText(LocalPersistenceManager.getLocalPersistenceManager().getUserEmail()==null? "":LocalPersistenceManager.getLocalPersistenceManager().getUserEmail());
    }

    private fun isAllFieldValidated(): Boolean {
        EmailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$"
        EmailPattern = Pattern.compile(EmailRegex)
        var isAllFieldValid = true
        if (editTextEmailId.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@LoginScreen,
                findViewById(android.R.id.content),
                "Enter Email ID",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        }else if (!EmailPattern.matcher(editTextEmailId.text.toString().trim { it <= ' ' })
                .matches()
        ) {
            GlobalData.showSnackbar(
                this@LoginScreen,
                findViewById<View>(android.R.id.content),
                "Entered Email ID is not in proper format",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        } else if (editTextLoginPassword.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@LoginScreen,
                findViewById(android.R.id.content),
                "Enter password",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        }

        return isAllFieldValid

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
                this@LoginScreen,
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
                firebaseUser.photoUrl.toString()
            )
            val usersDao = UserDao()
            usersDao.addUser(user)
            LocalPersistenceManager.getLocalPersistenceManager().userName = user.name
            val mainActivityIntent = Intent(this@LoginScreen, HomeScreenActivity::class.java)
            startActivity(mainActivityIntent)
            finish()

        }

    }

    private fun loginMethod(email : String, password : String){
        try {
            if (isAllFieldValidated()) {
                auth.signInWithEmailAndPassword(
                    email,
                    password
                ).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        update(user)
                    } else {
                        if (task.exception?.toString() == "com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.") {
                            GlobalData.showSnackbar(
                                this@LoginScreen,
                                findViewById(android.R.id.content),
                                "User does not exist.",
                                GlobalData.ColorType.ERROR
                            )
                            update(null)
                        }
                        else if (task.exception?.toString() == "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted."){
                            GlobalData.showSnackbar(
                                this@LoginScreen,
                                findViewById(android.R.id.content),
                                "Email is badly formatted.",
                                GlobalData.ColorType.ERROR
                            )
                            update(null)
                        }

                        else if (task.exception?.toString() == "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password."){
                            GlobalData.showSnackbar(
                                this@LoginScreen,
                                findViewById(android.R.id.content),
                                "Invalid username or password.",
                                GlobalData.ColorType.ERROR
                            )
                            update(null)
                        }

                    }
                }
            }
        }
        catch (e: Exception) {
            GlobalData.showSnackbar(
                this@LoginScreen,
                findViewById(android.R.id.content),
                "Please try again",
                GlobalData.ColorType.ERROR
            )
            update(null)
        }
    }

    private fun update(firebaseUser: FirebaseUser?){
        if(firebaseUser != null) {
                GlobalScope.launch {
                    val userDao = UserDao()
                    val user =
                        userDao.getUserById(firebaseUser.uid).await().toObject(User::class.java)!!
                    Log.v("Ishita", "login")
                    LocalPersistenceManager.getLocalPersistenceManager().userName = user.name
                    userDao.loginUser(user)
                }
                val mainActivityIntent = Intent(this@LoginScreen, HomeScreenActivity::class.java)
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
                    this@LoginScreen,
                    findViewById(android.R.id.content),
                    "Please try again",
                    GlobalData.ColorType.ERROR
                )
            }
        }
    }

}