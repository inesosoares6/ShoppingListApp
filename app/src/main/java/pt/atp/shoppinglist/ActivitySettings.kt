package pt.atp.shoppinglist

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val db = FirebaseFirestore.getInstance()
            val mAuth: FirebaseAuth?
            mAuth= FirebaseAuth.getInstance()

            val changeUsername: EditTextPreference? = findPreference("username")
            changeUsername?.summaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
                val text = preference.text
                if (TextUtils.isEmpty(text)) {
                    getNameFromDatabase(db, mAuth)
                } else {
                    mAuth.currentUser?.email?.let {
                        db.collection("users").document(it).update(mapOf(
                                "name" to text
                        ))
                    }
                    getNameFromDatabase(db, mAuth)
                }
            }
            val changeFamilyID: EditTextPreference? = findPreference("familyID")
            changeFamilyID?.summaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
                val text = preference.text
                if (TextUtils.isEmpty(text)) {
                    getIDFromDatabase(db, mAuth)
                } else {
                    mAuth.currentUser?.email?.let {
                        db.collection("users").document(it).update(mapOf(
                                "familyId" to text
                        ))
                    }
                    getNameFromDatabase(db, mAuth)
                }
            }
        }

        private fun getNameFromDatabase(db: FirebaseFirestore, mAuth: FirebaseAuth): String {
            var name = ""
            mAuth.currentUser?.email?.let {
                db.collection("users").document(it).get()
                        .addOnSuccessListener { result ->
                            name = result["name"].toString()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, getString(R.string.error_name), Toast.LENGTH_LONG).show()
                        }
            }
            return name
        }

        private fun getIDFromDatabase(db: FirebaseFirestore, mAuth: FirebaseAuth): String {
            var familyID = ""
            mAuth.currentUser?.email?.let {
                db.collection("users").document(it).get()
                        .addOnSuccessListener { result ->
                            familyID = result["familyId"].toString()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, getString(R.string.error_name), Toast.LENGTH_LONG).show()
                        }
            }
            return familyID
        }
    }
}