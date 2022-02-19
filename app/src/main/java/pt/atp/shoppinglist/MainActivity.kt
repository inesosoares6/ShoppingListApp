package pt.atp.shoppinglist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment=FragmentHome()
        val listFragment=FragmentList()
        val catalogFragment=FragmentCatalog()

        setCurrentFragment(homeFragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home->{
                    setCurrentFragment(homeFragment)
                    finish()
                    val intent = Intent(applicationContext,MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.list->setCurrentFragment(listFragment)
                R.id.catalog->setCurrentFragment(catalogFragment)
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_action, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val mAuth: FirebaseAuth?
        mAuth=FirebaseAuth.getInstance()
        return when (item.itemId) {
            R.id.action_logout ->{
                mAuth.signOut()
                Toast.makeText(applicationContext,getString(R.string.successSignOut), Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext,ActivityLogin::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_settings ->{
                val intent = Intent(applicationContext, ActivitySettings::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setCurrentFragment(fragment:Fragment)=
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment,fragment)
                commit()
            }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}