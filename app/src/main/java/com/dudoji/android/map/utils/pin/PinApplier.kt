package com.dudoji.android.map.utils.pin

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.map.domain.Pin
import com.dudoji.android.util.modal.Modal
import com.google.maps.android.clustering.ClusterManager


class PinApplier(val clusterManager: ClusterManager<Pin>, val activity: AppCompatActivity) {
    companion object {
        private val appliedPins: HashSet<Pin> = HashSet()
    }
   init {
       clusterManager.setOnClusterItemClickListener{
           pin ->
           showPinMemo(pin)
           true
       }
       clusterManager.setOnClusterClickListener {
           if (it.size >= 1) {
               Log.d("PinApplier", "onClusterClick: $it")
               val pins = it.items
               Modal.showCustomModal(activity, R.layout.modal_pin_memos_show) {
                   val memos = it.findViewById<RecyclerView>(R.id.memos_recycler_view)
                   memos.layoutManager = LinearLayoutManager(activity)
                   val memoAdapter = PinMemoAdapter(pins.toList())
                   memos.adapter = memoAdapter
                   val touchListener = object : RecyclerView.OnItemTouchListener {
                       override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                           val childView = rv.findChildViewUnder(e.x, e.y)
                           if (childView != null && e.action == MotionEvent.ACTION_UP) {
                               val position = rv.getChildAdapterPosition(childView)
                               val pin = pins.elementAt(position)
                               showPinMemo(pin)
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
           true
       }
   }
    fun showPinMemo(pin: Pin) {
        Modal.showCustomModal(activity, R.layout.modal_pin_memo_show) { view ->
            val pinTitle = view.findViewById<TextView>(R.id.memo_title_output)
            val pinContent = view.findViewById<TextView>(R.id.memo_content_output)
            val pinDate = view.findViewById<TextView>(R.id.memo_date_output)
            pinTitle.text = pin.title
            pinContent.text = pin.content
            pinDate.text = pin.createdDate.toString()
        }
    }

    fun clearPins() {
        appliedPins.clear()
        clusterManager.clearItems()
        clusterManager.cluster()
    }

    fun applyPin(pin: Pin) {
        if (!appliedPins.contains(pin)) {
            clusterManager.addItem(pin)
            Log.d("PinApplier", "applyPin: $pin")
            appliedPins.add(pin)
        }
        clusterManager.cluster()
    }

    fun applyPins(pins: List<Pin>) {
        pins.forEach { pin ->
            if (!appliedPins.contains(pin)) {
                clusterManager.addItem(pin)
                appliedPins.add(pin)
            }
        }
        clusterManager.cluster()
    }
}

class PinMemoAdapter(private val itemList: List<Pin>) :
    RecyclerView.Adapter<PinMemoAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.pin_memo_item_title)
        val content: TextView = itemView.findViewById(R.id.pin_memo_item_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pin_memo_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = itemList[position].title
        holder.content.text = itemList[position].content
    }

    override fun getItemCount() = itemList.size
}