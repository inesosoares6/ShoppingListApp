package pt.atp.shoppinglist

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.atp.warmupapp.models.ItemsAdapter

class FragmentCatalog  : Fragment(R.layout.fragment_list) {

    private val db = FirebaseFirestore.getInstance()
    private var mAuth: FirebaseAuth? = null
    private val arrayDocs: ArrayList<String> = ArrayList()
    private val arrayTime: ArrayList<String> = ArrayList()
    private val arrayType: ArrayList<String> = ArrayList()
    private val arrayName: ArrayList<String> = ArrayList()
    private val arrayExercises: ArrayList<String> = ArrayList()
    private var numWarmUps = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val rootView: View = inflater.inflate(R.layout.fragment_list,container,false)
        val newButton: FloatingActionButton = rootView.findViewById(R.id.newButton)
        mAuth= FirebaseAuth.getInstance()

        arrayDocs.clear()
        arrayName.clear()
        arrayType.clear()
        arrayTime.clear()
        arrayExercises.clear()

        mAuth!!.currentUser?.email?.let {
            db.collection("users").document(it).collection("warm-ups").get()
                .addOnSuccessListener { result ->
                    for(document in result){
                        arrayDocs.add(document.id)
                        arrayName.add(document["name"].toString())
                        arrayType.add(document["type"].toString())
                        arrayTime.add(document["time"].toString())
                        arrayExercises.add(document["exercises"].toString())
                    }
                    when (arrayDocs.size) {
                        0 -> {
                            Toast.makeText(context,getString(R.string.add_new_item), Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            sendData(rootView, arrayName, arrayType, arrayTime)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context,getString(R.string.error_getting_documents), Toast.LENGTH_LONG).show()
                }
        }

        mAuth!!.currentUser?.email?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { result ->
                    numWarmUps=result["numWarmUps"].toString().toInt()
                }
                .addOnFailureListener {
                    Toast.makeText(context, getString(R.string.error_getting_numItems), Toast.LENGTH_LONG).show()
                }
        }

        return rootView
    }

    private fun sendData(rootView: View, arrayName: ArrayList<String>, arrayType: ArrayList<String>, arrayTime: ArrayList<String>) {
        val myListAdapter = ItemsAdapter(context as Activity, arrayName.toTypedArray(), arrayType.toTypedArray(), arrayTime.toTypedArray())
        val listView: ListView = rootView.findViewById(R.id.listViewCatalog)
        listView.adapter = myListAdapter

        listView.setOnItemClickListener { adapterView, _, position, _ ->
            val itemIdAtPos = adapterView.getItemIdAtPosition(position)

            val dialogBuilder = AlertDialog.Builder(context as Activity)
            dialogBuilder.setMessage(getString(R.string.performAction))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.favorite)) { _, _ ->
                    setAsFavorite(itemIdAtPos.toInt())
                }
                .setNegativeButton(getString(R.string.deleteFromList)) { _, _ ->
                    deleteFromList(itemIdAtPos.toInt())
                }
                .setNeutralButton(getString(R.string.cancelElimination)) { dialog, _ ->
                    dialog.cancel()
                }
            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.item) + " #" + (itemIdAtPos+1))
            alert.show()
        }
    }

    private fun deleteFromList(idList: Int) {
        mAuth?.currentUser?.email?.let {
            db.collection("users").document(it).collection("warm-ups").document(arrayDocs[idList]).delete()
            Toast.makeText(context, getString(R.string.item_deleted_successful), Toast.LENGTH_LONG).show()
        }
        mAuth?.currentUser?.email?.let {
            db.collection("users").document(it).update(mapOf("numWarmUps" to (numWarmUps-1)))
        }
    }

    @SuppressLint("ResourceType")
    private fun setAsFavorite(idList: Int) {
        val favWarmUp = hashMapOf(
            "name" to arrayName[idList],
            "type" to arrayType[idList],
            "time" to arrayTime[idList],
            "exercises" to arrayExercises[idList]
        )
        mAuth?.currentUser?.email?.let {
            db.collection("users").document(it).collection("default").document("warm-up").set(favWarmUp)
                .addOnSuccessListener {
//                        Toast.makeText(context, activity?.getString(R.string.warm_up_addition_successful), Toast.LENGTH_LONG).show()
//                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.flFragment,FragmentList())?.commit()
                }
                .addOnFailureListener {
                    Toast.makeText(context, activity?.getString(R.string.error_adding_item), Toast.LENGTH_LONG).show()
                }
        }
    }
}