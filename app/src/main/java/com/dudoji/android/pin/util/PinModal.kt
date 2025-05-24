package com.dudoji.android.pin.util

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.util.modal.Modal

object PinModal {
    fun openPinMemoModal(activity: AppCompatActivity, pin: Pin) {
        Modal.showCustomModal(activity, R.layout.modal_pin_memo_show) { view ->
            val pinTitle = view.findViewById<TextView>(R.id.memo_title_output)
            val pinContent = view.findViewById<TextView>(R.id.memo_content_output)
            val pinDate = view.findViewById<TextView>(R.id.memo_date_output)
            pinTitle.text = pin.title
            pinContent.text = pin.content
            pinDate.text = pin.createdDate.toString()
        }
    }

    fun openPinMemosModal(activity: AppCompatActivity, pins: List<Pin>) {
        Modal.showCustomModal(activity, R.layout.modal_pin_memos_show) { view ->
            val memos = view.findViewById<RecyclerView>(R.id.memos_recycler_view)
            memos.layoutManager = LinearLayoutManager(activity)
            val memoAdapter = PinMemoAdapter(pins.toList())
            memos.adapter = memoAdapter
            val touchListener = object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    val childView = rv.findChildViewUnder(e.x, e.y)
                    if (childView != null && e.action == MotionEvent.ACTION_UP) {
                        val position = rv.getChildAdapterPosition(childView)
                        val pin = pins.elementAt(position)
                        openPinMemoModal(activity, pin)
                        return true
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            }

            memos.addOnItemTouchListener(touchListener)
        }
    }

    fun openPinDataModal(activity: AppCompatActivity, onComplete: (Pair<String, String>) -> Unit) {
        Modal.showCustomModal(activity, R.layout.modal_pin_memo) { view ->
            val pinTitle = view.findViewById<EditText>(R.id.memo_title_input)
            val pinContent = view.findViewById<EditText>(R.id.memo_content_input)
            val saveButton = view.findViewById<Button>(R.id.memo_save_button)

            saveButton.setOnClickListener {
                onComplete(
                    Pair(
                        pinTitle.text.toString(),
                        pinContent.text.toString()
                    )
                )

                // Close the modal
                (view.parent.parent.parent as? ViewGroup)?.removeView(view.parent.parent as View?)
                true
            }
        }
    }
}