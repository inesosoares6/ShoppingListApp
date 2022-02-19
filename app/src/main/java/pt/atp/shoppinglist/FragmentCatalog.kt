package pt.atp.shoppinglist

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.atp.shoppinglist.models.CatalogAdapter
import java.util.*
import kotlin.collections.ArrayList

class FragmentCatalog  : Fragment(R.layout.fragment_catalog) {

    private val db = FirebaseFirestore.getInstance()
    private var mAuth: FirebaseAuth? = null
    private val arrayDocs: ArrayList<String> = ArrayList()
    private val arrayItem: ArrayList<String> = ArrayList()
    private var familyId: String = String()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val rootView: View = inflater.inflate(R.layout.fragment_catalog, container, false)
        val newButton: FloatingActionButton = rootView.findViewById(R.id.newButtonCatalog)
        mAuth= FirebaseAuth.getInstance()

        mAuth!!.currentUser?.email?.let {
            db.collection("users").document(it).get()
                    .addOnSuccessListener { result ->
                        familyId = result["familyId"].toString()
                        getCatalog(rootView)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, getString(R.string.error_name), Toast.LENGTH_LONG).show()
                    }
        }

        newButton.setOnClickListener {
            showDialogNewItem(rootView)
        }

        return rootView
    }

    private fun sendData(rootView: View, arrayItem: ArrayList<String>) {
        val myListAdapter = CatalogAdapter(context as Activity, arrayItem.toTypedArray())
        val listView: ListView = rootView.findViewById(R.id.listViewCatalog)
        listView.adapter = myListAdapter

        listView.setOnItemClickListener { adapterView, _, position, _ ->
            val itemIdAtPos = adapterView.getItemIdAtPosition(position)

            val dialogBuilder = AlertDialog.Builder(context as Activity)
            dialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.add_to_list)) { _, _ ->
                    showDialogQuantity(rootView, itemIdAtPos.toInt())
                }
                .setNegativeButton(getString(R.string.deleteFromList)) { _, _ ->
                    deleteFromCatalog(rootView, itemIdAtPos.toInt())
                }
                .setNeutralButton(getString(R.string.cancelElimination)) { dialog, _ ->
                    dialog.cancel()
                }
            val alert = dialogBuilder.create()
            alert.setTitle(arrayItem[itemIdAtPos.toInt()])
            alert.show()
        }
    }

    private fun deleteFromCatalog(rootView: View, idList: Int) {
        db.collection("familyIDs").document(familyId).collection("catalog").document(arrayDocs[idList]).delete()
        getCatalog(rootView)
    }

    @SuppressLint("ResourceType")
    private fun addToList(rootView: View, idList: Int, quantity: String) {
        val newItemList = hashMapOf(
                "item" to arrayItem[idList],
                "quantity" to quantity
        )
        val itemID = arrayItem[idList].replace(" ", "_").lowercase(Locale.ROOT)
        db.collection("familyIDs").document(familyId).collection("list").document(itemID).set(newItemList)
            .addOnSuccessListener {
                deleteFromCatalog(rootView, idList)
            }
            .addOnFailureListener {
                Toast.makeText(context, activity?.getString(R.string.error_adding_item), Toast.LENGTH_LONG).show()
            }
        }

    private fun addToCatalog(rootView: View, item: String) {
        val newItemCatalog = hashMapOf(
                "item" to item,
                "quantity" to 0
        )
        val itemID = item.replace(" ", "_").lowercase(Locale.ROOT)
        db.collection("familyIDs").document(familyId).collection("catalog").document(itemID).set(newItemCatalog)
        getCatalog(rootView)
    }

    private fun getCatalog(rootView: View){
        arrayDocs.clear()
        arrayItem.clear()

        db.collection("familyIDs").document(familyId).collection("catalog").get()
                .addOnSuccessListener { result ->
                    for(document in result){
                        arrayDocs.add(document.id)
                        arrayItem.add(document["item"].toString())
                    }
                    when (arrayDocs.size) {
                        0 -> {
                            Toast.makeText(context, getString(R.string.add_new_item), Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            sendData(rootView, arrayItem)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, getString(R.string.error_getting_documents), Toast.LENGTH_LONG).show()
                }
    }

    private fun showDialogNewItem(rootView: View){
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Adding new item to catalog")

        val input = EditText(context)
        input.hint = "Item"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            addToCatalog(rootView, input.text.toString())
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun showDialogQuantity(rootView: View, idList: Int){
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Quantity")

        val input = EditText(context)
        input.hint = ""
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            addToList(rootView, idList, input.text.toString())
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }
}