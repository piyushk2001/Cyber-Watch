package eu.tutorials.cyberwatch

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapterr(private val context: Context, private val senderEmail: String) : RecyclerView.Adapter<MessageAdapterr.MessageHolder>() {
    private val messages = mutableListOf<Message>()

    fun setMessages(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    fun getMessages(): List<Message> {
        return messages
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false)
        return MessageHolder(view)
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val message = messages[position]

        if (message.senderEmail == senderEmail) {
            holder.senderTextView.visibility = View.VISIBLE
            holder.receiverTextView.visibility = View.INVISIBLE
            holder.senderTextView.text = message.messageText
        } else if (message.receiverEmail== senderEmail) {
            holder.senderTextView.visibility = View.INVISIBLE
            holder.receiverTextView.visibility = View.VISIBLE
            holder.receiverTextView.text = message.messageText
        }
    }



    override fun getItemCount(): Int {
        return messages.size
    }

    inner class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderTextView: TextView = itemView.findViewById(R.id.senderTextView)
        val receiverTextView: TextView = itemView.findViewById(R.id.receiverTextView)
    }
}

data class Message(
    val senderEmail: String = "",
    val receiverEmail: String = "",
    val messageText: String = "",
    val timestamp: Long = 0
)


