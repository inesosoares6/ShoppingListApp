package pt.atp.shoppinglist

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ActivitySettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if(id==android.R.id.home){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private var mAuth: FirebaseAuth? = null
        private val db = FirebaseFirestore.getInstance()

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            mAuth= FirebaseAuth.getInstance()

            val changeUsername: Preference? = findPreference("username")
            changeUsername?.setOnPreferenceClickListener {
                showDialogChangeUsername()
            }

            val newList: Preference? = findPreference("newList")
            newList?.setOnPreferenceClickListener {
                showDialogCreateList()
            }

            val deleteList: Preference? = findPreference("deleteList")
            deleteList?.setOnPreferenceClickListener {
                Toast.makeText(context, "TODO", Toast.LENGTH_LONG).show()
                showDialogDeleteList()
            }

        }

        private fun showDialogChangeUsername(): Boolean {
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Change username")

            val input = EditText(context)
            input.hint = "  new username"
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            builder.setPositiveButton("OK") { _, _ ->
                mAuth?.currentUser?.email?.let {
                    db.collection("users").document(it).update(mapOf(
                        "name" to input.text.toString()
                    ))
                }
            }
            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            builder.show()

            return true
        }

        private fun showDialogCreateList(): Boolean  {
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Create new list")

            val input = EditText(context)
            input.hint = "  list name"
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            builder.setPositiveButton("OK") { _, _ ->
                mAuth?.currentUser?.email?.let {
                    db.collection("users").document(it).update(mapOf(
                        "familyId" to input.text.toString()
                    ))
                }
                val listID = input.text.toString().replace(" ", "_").lowercase(Locale.ROOT)
                val newList = hashMapOf(
                    "name" to input.text.toString()
                )
                db.collection("lists").document(listID).set(newList)
            }
            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            builder.show()

            return true
        }

        private fun showDialogDeleteList(): Boolean  {


            return true
        }
    }
}