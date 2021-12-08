package pt.atp.shoppinglist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ActivityDeleteUser : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var cancelButton: Button? = null
    private var deleteButton: Button? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_user)
        initializeUI()
        mAuth = FirebaseAuth.getInstance()

        cancelButton!!.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        deleteButton!!.setOnClickListener {
            mAuth!!.currentUser?.email?.let { it1 ->
                db.collection("users").document(it1).delete()
            }
            mAuth!!.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(applicationContext,getString(R.string.userDeleted), Toast.LENGTH_LONG).show()
                    val intent = Intent(this, ActivityLogin::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun initializeUI() {
        cancelButton = findViewById(R.id.cancelButton)
        deleteButton = findViewById(R.id.deleteButton)
    }
}