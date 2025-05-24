package com.dudoji.android.pin.util

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.util.WeekTranslator
import com.dudoji.android.util.modal.Modal
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.Pair

object PinModal {
    @RequiresApi(Build.VERSION_CODES.O)
    fun openPinMemoModal(activity: AppCompatActivity, pin: Pin) {
        Modal.showCustomModal(activity, R.layout.show_pin_memo_modal) { view ->
            val pinContent = view.findViewById<TextView>(R.id.memo_content_output)
            val pinDate = view.findViewById<TextView>(R.id.memo_date_output)
            val pinLikeButton = view.findViewById<ImageView>(R.id.memo_like_button)
            val pinLikeCount = view.findViewById<TextView>(R.id.memo_like_count)
            val isLiked = pin.isLiked

            pinLikeButton.setImageDrawable(
                activity.getDrawable(
                    if (isLiked) R.drawable.heart_like
                    else R.drawable.heart_unlike))

            pinLikeCount.text = pin.likeCount.toString()
            pinContent.text = pin.content
            pinDate.text = pin.createdDate.toString()

            pinLikeButton.setOnClickListener {
                if (isLiked) {
                    pinLikeButton.setImageDrawable(activity.getDrawable(R.drawable.heart_unlike))
                    pinLikeCount.text = (pin.likeCount - 1).toString()
                    activity.lifecycleScope.launch {
                        RetrofitClient.pinApiService.unlikePin(pin.pinId)
                    }
                } else {
                    pinLikeButton.setImageDrawable(activity.getDrawable(R.drawable.heart_like))
                    pinLikeCount.text = (pin.likeCount + 1).toString()
                    activity.lifecycleScope.launch {
                        RetrofitClient.pinApiService.likePin(pin.pinId)
                    }
                }
            }
        }
    }

    fun openPinMemosModal(activity: AppCompatActivity, pins: List<Pin>) {
        Modal.showCustomModal(activity, R.layout.show_pin_memos_modal) { view ->
            val memos = view.findViewById<RecyclerView>(R.id.memos_recycler_view)
            memos.layoutManager = LinearLayoutManager(activity)
            val memoAdapter = PinMemoAdapter(pins.toList())
            memos.adapter = memoAdapter
            val touchListener = object : RecyclerView.OnItemTouchListener {
                @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun openPinDataModal(activity: AppCompatActivity, onComplete: (Pair<String, LocalDate>) -> Unit) {
        Modal.showCustomModal(activity, R.layout.edit_pin_memo_modal) { view ->
            val pinContent = view.findViewById<EditText>(R.id.memo_content_input)
            val pinDate = view.findViewById<TextView>(R.id.memo_date)
            val pinDateEditButton = view.findViewById<ImageView>(R.id.memo_date_edit_button)
            val saveButton = view.findViewById<Button>(R.id.memo_save_button)
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                activity,
                { _, year, month, dayOfMonth ->
                    val selectedDate = "$year.${month + 1}.$dayOfMonth (${WeekTranslator.translateWeekToKorean(calendar.get(Calendar.DAY_OF_WEEK))})"
                    pinDate.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            pinDateEditButton.setOnClickListener {
                datePicker.show()
            }

            saveButton.setOnClickListener {
                onComplete(
                    Pair(
                        pinContent.text.toString(),
                        LocalDate.of(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH) + 1,
                            calendar.get(Calendar.DAY_OF_MONTH))
                    )
                )

                // Close the modal
                (view.parent.parent.parent as? ViewGroup)?.removeView(view.parent.parent as View?)
                true
            }
        }
    }
}
