package com.davidshinto.fitenglish

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast

class CategorySpinnerAdapter(private val context: Context, private val items: Array<Category>, private val spinner: AdapterView<*>) :
    ArrayAdapter<Category>(context, android.R.layout.simple_spinner_item, items),
    AdapterView.OnItemSelectedListener {

    init {
        spinner.onItemSelectedListener = this
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = items[position].name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val categoryNameTextView = view.findViewById<TextView>(android.R.id.text1)
        categoryNameTextView.text = items[position].name
        return view
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = items[position].name
        Toast.makeText(context, "Selected Item: $selectedItem", Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    //Accidental override
    override fun getContext(): Context {
        return super.getContext()
    }
}