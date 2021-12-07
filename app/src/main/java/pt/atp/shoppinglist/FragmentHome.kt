package pt.atp.shoppinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FragmentHome : Fragment(R.layout.fragment_home) {

    private val db = FirebaseFirestore.getInstance()
    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_home,container,false)
        val userNameText: TextView = rootView.findViewById(R.id.userNameText)
        val familyNameText: TextView = rootView.findViewById(R.id.family)
        mAuth= FirebaseAuth.getInstance()

        mAuth!!.currentUser?.email?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { result ->
                    userNameText.text = result["name"].toString()
                    familyNameText.text = result["familyId"].toString()
                }
                .addOnFailureListener {
                    Toast.makeText(context,getString(R.string.error_name), Toast.LENGTH_LONG).show()
                }
        }

        return rootView
    }
}