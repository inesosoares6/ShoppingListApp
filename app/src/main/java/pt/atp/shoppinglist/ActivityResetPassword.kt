package pt.atp.shoppinglist


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

// Activity to send email instructions to reset the password

class ActivityResetPassword : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var emailTV: EditText? = null
    private var resetBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        initializeUI()
        mAuth = FirebaseAuth.getInstance()
        resetBtn!!.setOnClickListener{
            val email: String = emailTV!!.text.toString()
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, getString(R.string.enterEmail), Toast.LENGTH_LONG).show()
            }
            mAuth!!.sendPasswordResetEmail(email).addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, getString(R.string.sent_email_recover), Toast.LENGTH_LONG).show()
                    val intent = Intent(this, ActivityLogin::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, getString(R.string.fail_email_recover), Toast.LENGTH_LONG).show()
                }}
        }
    }

    private fun initializeUI() {
        emailTV = findViewById(R.id.recover_email)
        resetBtn = findViewById(R.id.reset_button)
    }
}