package pt.atp.shoppinglist


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Activity to register the user

class ActivityRegister : AppCompatActivity() {

    private var nameTV: EditText? = null
    private var familyIdTV: EditText? = null
    private var emailTV: EditText? = null
    private var passwordTV: EditText? = null
    private var confirmPasswordTV: EditText? = null
    private var regBtn: Button? = null
    private var mAuth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
        initializeUI()
        regBtn!!.setOnClickListener{
            registerNewUser()
        }
    }

    private fun writeNewUserDatabase(name: String, familyId: String, email: String) {
        val user = hashMapOf(
            "name" to name,
            "familyId" to familyId,
            "email" to email
        )

        // Add a new document with a generated ID
        db.collection("users").document(email)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, getString(R.string.successRegister), Toast.LENGTH_LONG).show()
                val intent = Intent(this, ActivityLogin::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, getString(R.string.failRegister), Toast.LENGTH_LONG).show()
            }
    }

    private fun registerNewUser() {
        val name: String = nameTV!!.text.toString()
        val familyID: String = familyIdTV!!.text.toString()
        val email: String = emailTV!!.text.toString()
        val password: String = passwordTV!!.text.toString()
        val confirmPassword: String = confirmPasswordTV!!.text.toString()

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(applicationContext, getString(R.string.enterName), Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(familyID)) {
            Toast.makeText(applicationContext, getString(R.string.enterFamilyID), Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, getString(R.string.enterEmail), Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, getString(R.string.enterPassword), Toast.LENGTH_LONG).show()
            return
        }
        if(password != confirmPassword){
            Toast.makeText(applicationContext, getString(R.string.matchPassword), Toast.LENGTH_LONG).show()
            return
        }

        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    writeNewUserDatabase(name, familyID, email)
                } else {
                    Toast.makeText(applicationContext, getString(R.string.failRegister), Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun initializeUI() {
        nameTV = findViewById(R.id.name)
        familyIdTV = findViewById(R.id.family_id)
        emailTV = findViewById(R.id.email)
        passwordTV = findViewById(R.id.password)
        confirmPasswordTV = findViewById(R.id.confirm_password)
        regBtn = findViewById(R.id.button_register)
    }
}