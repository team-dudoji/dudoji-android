package com.dudoji.android.landmark.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R

class EditableHashtagAdapter : HashtagAdapter(mutableListOf()) {

    companion object {
        private const val VIEW_TYPE_TAG = 0
        private const val VIEW_TYPE_INPUT = 1
    }

    lateinit var editText: EditText

    fun getResult(): List<String> {
        val lastHashTag = editText.text.toString().removePrefix("#").trim()
        if (lastHashTag.isEmpty()) {
            return hashtags
        } else {
            return hashtags.plus(lastHashTag)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == hashtags.size) VIEW_TYPE_INPUT else VIEW_TYPE_TAG
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_TAG) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.hashtag_item, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.hashtag_editable_item, parent, false)
            InputViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(hashtags[position])
        } else if (holder is InputViewHolder) {
            holder.bind { tag ->
                onTagInput(tag)
            }
        }
    }

    fun onTagRemove(position: Int) {
        if (position in 0 until hashtags.size) {
            hashtags.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun onTagInput(tag: String) {
        if (hashtags.size < 5 && !hashtags.contains(tag)) {
            hashtags.add(tag)
            notifyItemInserted(hashtags.size - 1)
        }
    }

    inner class InputViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(onTagInput: (String) -> Unit) {
            editText = itemView.findViewById<EditText>(R.id.hashtag_edit_text)
            editText.requestFocus()
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s.endsWith(" ")) {
                        val text = s.toString().trim()
                        if (text.isNotEmpty()) {
                            onTagInput(text.removePrefix("#"))
                            editText.setText("#")
                            editText.setSelection(editText.text.length)
                        }
                    } else if (s != null && s.length == 0) {
                        if (hashtags.isEmpty()) {
                            editText.setText("#")
                            editText.setSelection(editText.text.length)
                        } else {
                            editText.setText("#${hashtags.last()}")
                            onTagRemove(hashtags.size - 1)
                            editText.setSelection(editText.text.length)
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            })
        }
    }
}