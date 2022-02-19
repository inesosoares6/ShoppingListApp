package pt.atp.shoppinglist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FragmentHome : Fragment(R.layout.fragment_home) {

    private val db = FirebaseFirestore.getInstance()
    private var mAuth: FirebaseAuth? = null
    private val arrayDocs: ArrayList<String> = ArrayList()
    private var listId: String = String()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_home,container,false)
        val usernameText: TextView = rootView.findViewById(R.id.usernameText)
        val listText: TextView = rootView.findViewById(R.id.list)
        mAuth= FirebaseAuth.getInstance()

        getCurrentID(usernameText, listText)


        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, getListIds())
        val autocompleteTV: AutoCompleteTextView = rootView.findViewById(R.id.autoCompleteTextView)
        autocompleteTV.setAdapter(arrayAdapter)
        autocompleteTV.setOnItemClickListener { adapterView, _, position, _ ->
            val itemIdAtPos = adapterView.getItemIdAtPosition(position)
            changeList(itemIdAtPos.toInt())
            startActivity(Intent(context,MainActivity::class.java))
        }

        return rootView
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentID(usernameText: TextView, listText: TextView) {
        mAuth!!.currentUser?.email?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { result ->
                    usernameText.text = result["name"].toString()
                    listId = result["familyId"].toString()
                    listText.text = "List: $listId"
                }
                .addOnFailureListener {
                    Toast.makeText(context,getString(R.string.error_name), Toast.LENGTH_LONG).show()
                }
        }

    }

    private fun changeList(itemIdAtPos: Int) {
        mAuth?.currentUser?.email?.let {
            db.collection("users").document(it).update(mapOf(
                "familyId" to arrayDocs[itemIdAtPos]
            ))
        }
    }

    private fun getListIds(): ArrayList<String> {
        arrayDocs.clear()
        db.collection("lists").get()
            .addOnSuccessListener { result ->
                for (document in result){
                    arrayDocs.add(document["name"].toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(context,getString(R.string.error_getting_documents), Toast.LENGTH_LONG).show()
            }
        return arrayDocs
    }

}


