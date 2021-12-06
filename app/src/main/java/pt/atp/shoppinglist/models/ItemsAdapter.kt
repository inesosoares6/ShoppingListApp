package pt.atp.warmupapp.models

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import pt.atp.shoppinglist.R

class ItemsAdapter(private val context: Activity, private val name: Array<String>, private val type: Array<String>, private val time: Array<String>)
    : ArrayAdapter<String>(context, R.layout.layout_list, name) {

    @SuppressLint("ViewHolder", "InflateParams", "SetTextI18n")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.layout_list, null, true)

        val imageView = rowView.findViewById(R.id.iconList) as ImageView
        val itemText = rowView.findViewById(R.id.itemList) as TextView
        val quantityText = rowView.findViewById(R.id.quantityList) as TextView

        imageView.setImageResource(R.drawable.logo_white)
        itemText.text = name[position]
        quantityText.text = type[position]

        return rowView
    }
}