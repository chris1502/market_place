package com.example.marketplace.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.marketplace.Adapter.InventoryItem
import com.example.marketplace.R
import com.squareup.picasso.Picasso

class InventoryAdapter(context: Context, inventoryList: List<InventoryItem>) :
    ArrayAdapter<InventoryItem>(context, 0, inventoryList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false)
        }

        val currentItem = getItem(position)
        val inventoryImageView = itemView?.findViewById<ImageView>(R.id.inventoryImageView)
        val nameTextView = itemView?.findViewById<TextView>(R.id.nameTextView)
        val descriptionTextView = itemView?.findViewById<TextView>(R.id.descriptionTextView)
        val priceTextView = itemView?.findViewById<TextView>(R.id.priceTextView)
        val locationTextView = itemView?.findViewById<TextView>(R.id.locationTextView)

        nameTextView?.text = currentItem?.name
        descriptionTextView?.text = currentItem?.description.toString()
        priceTextView?.text = currentItem?.price.toString()
        locationTextView?.text = currentItem?.location

        if (currentItem?.image != null) {
            Picasso.get().load(currentItem.image).into(inventoryImageView)
        } else {
            inventoryImageView?.setImageResource(R.drawable.placeholder_image)
        }

        return itemView!!
    }
}
