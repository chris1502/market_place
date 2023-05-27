package com.example.marketplace.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.Models.ChatMessage

import com.example.marketplace.R

class ChatMessageAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderNameTextView: TextView = itemView.findViewById(R.id.senderNameTextView)
        val messageContentTextView: TextView = itemView.findViewById(R.id.messageContentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.senderNameTextView.text = message.senderId
        holder.messageContentTextView.text = message.message
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}
