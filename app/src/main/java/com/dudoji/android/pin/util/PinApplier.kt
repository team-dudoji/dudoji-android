package com.dudoji.android.pin.util

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.repository.PinRepository
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
class PinApplier(val clusterManager: ClusterManager<Pin>,
                 val googleMap: GoogleMap,
                 val activity: AppCompatActivity,
                 val pinFilter: PinFilter
): OnCameraIdleListener {

    private var hasToReload = false

    companion object {
        private val appliedPins: HashSet<Pin> = HashSet()
    }
    init {
        clusterManager.setOnClusterItemClickListener{
                pin ->
            PinModal.openPinMemoModal(activity, pin)
            true
        }
        clusterManager.setOnClusterClickListener {
            if (it.size >= 1) {
                Log.d("PinApplier", "onClusterClick: $it")
                PinModal.openPinMemosModal(activity, it.items.toList())
            }
            true
        }
    }


    fun applyPins(pins: List<Pin>) {
        val filteredPins = pinFilter.filterPins(pins)

        filteredPins.forEach { pin ->
            if (!appliedPins.contains(pin)) {
                clusterManager.addItem(pin)
                appliedPins.add(pin)
            }
        }
        clusterManager.cluster()
    }

    fun markForReload() {
        hasToReload = true
    }

    fun clearPins() {
        appliedPins.clear()
        clusterManager.clearItems()
        clusterManager.cluster()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCameraIdle() {
        activity.lifecycleScope.launch {
            if (hasToReload || PinRepository.loadPins(googleMap.projection.visibleRegion.latLngBounds.center, 100.0)) {
                val pins = PinRepository.getPins()
                clearPins()
                applyPins(pins)
            }
            hasToReload = false
        }
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