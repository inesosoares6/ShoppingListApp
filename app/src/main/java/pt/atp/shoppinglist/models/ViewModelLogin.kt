package pt.atp.shoppinglist.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

// View model used to verify login in server

class ViewModelLogin : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResultLiveData = _loginResult

    fun areCredentialsValid(mAuth: FirebaseAuth?, email: String, password: String){
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginResultLiveData.postValue(true)
                } else {
                    loginResultLiveData.postValue(false)
                }
            }
    }
}