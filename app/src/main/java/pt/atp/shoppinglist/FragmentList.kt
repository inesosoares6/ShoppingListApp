package pt.atp.shoppinglist

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.atp.shoppinglist.models.ItemsAdapter
import java.util.*
import kotlin.collections.ArrayList

class FragmentList : Fragment(R.layout.fragment_list) {

    private val db = FirebaseFirestore.getInstance()
    private var mAuth: FirebaseAuth? = null
    private val arrayDocs: ArrayList<String> = ArrayList()
    private val arrayItem: ArrayList<String> = ArrayList()
    private val arrayQuantity: ArrayList<String> = ArrayList()
    private var familyId: String = String()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val rootView: View = inflater.inflate(R.layout.fragment_list,container,false)
        val copyButton: FloatingActionButton = rootView.findViewById(R.id.copyList)
        mAuth= FirebaseAuth.getInstance()

        mAuth!!.currentUser?.email?.let {
            db.collection("users").document(it).get()
                    .addOnSuccessListener { result ->
                        familyId = result["familyId"].toString()
                        getList(rootView)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context,getString(R.string.error_name), Toast.LENGTH_LONG).show()
                    }
        }

        copyButton.setOnClickListener {
            createListString()
        }

        return rootView
    }

    private fun sendData(rootView: View, arrayItem: ArrayList<String>, arrayQuantity: ArrayList<String>) {
        val myListAdapter = ItemsAdapter(context as Activity, arrayItem.toTypedArray(), arrayQuantity.toTypedArray())
        val listView: ListView = rootView.findViewById(R.id.listViewList)
        listView.adapter = myListAdapter

        listView.setOnItemClickListener { adapterView, _, position, _ ->
            val itemIdAtPos = adapterView.getItemIdAtPosition(position)
            deleteFromList(rootView, itemIdAtPos.toInt())
        }
    }

    private fun deleteFromList(rootView: View, idList: Int) {
        db.collection("familyIDs").document(familyId).collection("list").document(arrayDocs[idList]).delete()
        addToCatalog(rootView, arrayItem[idList])
    }

    private fun getList(rootView: View){
        arrayDocs.clear()
        arrayItem.clear()
        arrayQuantity.clear()

        db.collection("familyIDs").document(familyId).collection("list").get()
                .addOnSuccessListener { result ->
                    for(document in result){
                        arrayDocs.add(document.id)
                        arrayItem.add(document["item"].toString())
                        arrayQuantity.add(document["quantity"].toString())
                    }
                    when (arrayDocs.size) {
                        0 -> {
                            Toast.makeText(context,getString(R.string.add_new_item), Toast.LENGTH_LONG).show()
                            startActivity(Intent(context,MainActivity::class.java))
                        }
                        else -> {
                            sendData(rootView, arrayItem, arrayQuantity)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context,getString(R.string.error_getting_documents), Toast.LENGTH_LONG).show()
                }
    }

    private fun addToCatalog(rootView: View, item: String) {
        val newItemCatalog = hashMapOf(
                "item" to item,
                "quantity" to 0
        )
        val itemID = item.replace(" ", "_").lowercase(Locale.ROOT)
        db.collection("familyIDs").document(familyId).collection("catalog").document(itemID).set(newItemCatalog)
        getList(rootView)
    }

    private fun createListString(){
        var message = "Shopping List\n"
        var i = 0
        for (item in arrayItem){
            message = message + " - " + item + ": " + arrayQuantity[i] + "\n"
            i += 1
        }
        copyTextToClipboard(message)
    }

    private fun copyTextToClipboard(listText: String) {
        val clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", listText)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, "List copied to clipboard", Toast.LENGTH_LONG).show()
    }
}