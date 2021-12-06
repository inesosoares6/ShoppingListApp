package pt.atp.shoppinglist

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import java.util.*

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val lang = preferences.getString("lang", "en")
        val locale = Locale(lang.toString())
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }
}