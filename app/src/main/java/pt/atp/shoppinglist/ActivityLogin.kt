package pt.atp.shoppinglist

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import pt.atp.shoppinglist.models.ViewModelLogin

class ActivityLogin : AppCompatActivity() {

    private val viewModel: ViewModelLogin by viewModels()

    private var mAuth: FirebaseAuth? = null
    private var emailTV: EditText? = null
    private var passwordTV: EditText? = null
    private var loginBtn: Button? = null
    private var registerBtn: Button? = null
    private var forgotPassword: TextView? = null
    private var rememberCredentials: Boolean = false
    private var email: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        if(mAuth!!.currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        setContentView(R.layout.activity_login)
        setup()
    }

    private fun setup() {
        mAuth = FirebaseAuth.getInstance()
        initializeUI()
        loginBtn!!.setOnClickListener { validateCredentialsAndRedirect() }
        registerBtn!!.setOnClickListener{
            val intent = Intent(this, ActivityRegister::class.java)
            startActivity(intent)
        }
        forgotPassword!!.setOnClickListener{
            val intent = Intent(this, ActivityResetPassword::class.java)
            startActivity(intent)
        }
        viewModel.loginResultLiveData.observe(this){ loginResult ->
            if(!loginResult){
                Toast.makeText(applicationContext, getString(R.string.failLogin), Toast.LENGTH_LONG).show()
            } else{
                if(rememberCredentials){
                    val prefs: SharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = prefs.edit()
                    editor.putString("email",email)
                    editor.putString("password",password)
                    editor.apply()
                }
                Toast.makeText(applicationContext, getString(R.string.successLogin), Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun validateCredentialsAndRedirect(){
        email = emailTV!!.text.toString()
        password = passwordTV!!.text.toString()
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, getString(R.string.enterEmail), Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, getString(R.string.enterPassword), Toast.LENGTH_LONG).show()
            return
        }
        viewModel.areCredentialsValid(mAuth, email!!, password!!)
    }

    private fun initializeUI() {
        emailTV = findViewById(R.id.email)
        passwordTV = findViewById(R.id.password)
        loginBtn = findViewById(R.id.button_login)
        registerBtn = findViewById(R.id.button_register)
        forgotPassword = findViewById(R.id.forgot_password)
    }
}