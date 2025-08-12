package com.dudoji.android.map.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.map.api.dto.NpcMetaDto

class NpcListAdapter(val npcList: List<NpcMetaDto>, val onNpcClick: (NpcMetaDto) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_NPC = 1
    }
    val viewTypeList = mutableListOf<Int>()

    init {
        npcList.sortedBy { it.locationName }
        for (i in npcList.indices) {
            if (i == 0 || npcList[i].locationName != npcList[i - 1].locationName) {
                viewTypeList.add(VIEW_TYPE_HEADER)
            }

            viewTypeList.add(VIEW_TYPE_NPC)
        }
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_location, parent, false)
            return LocationViewHolder(view)
        } else if (viewType == VIEW_TYPE_NPC) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_npc, parent, false)
            return NpcViewHolder(view, onNpcClick)
        } else {
            throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewTypeList.getOrNull(position) ?: throw IllegalArgumentException("Invalid position: $position")
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val newPosition = getRealPosition(position)
        if (holder is NpcViewHolder) {
            holder.bind(npcList[newPosition])
        } else if (holder is LocationViewHolder) {
            val locationNpcs = npcList.filter { it.locationName == npcList[newPosition].locationName }
            holder.bind(locationNpcs)
        }
    }

    private fun getRealPosition(position: Int): Int {
        viewTypeList.subList(0, position).count { it == VIEW_TYPE_HEADER }.let { headerCount ->
            return position - headerCount
        }
    }

    override fun getItemCount(): Int {
        return npcList.size + npcList.groupBy { it.locationName }.size
    }

    class LocationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val locationNameText: TextView = itemView.findViewById(R.id.npc_location_text)
        private val locationProgressText: TextView = itemView.findViewById(R.id.npc_location_progress_text)

        fun bind(npc: List<NpcMetaDto>) {
            if (npc.isNotEmpty()) {
                locationNameText.text = npc[0].locationName
                val totalQuests = npc.sumOf { it.numOfQuests }
                val clearedQuests = npc.sumOf { it.numOfClearedQuests }
                locationProgressText.text = "${clearedQuests/totalQuests * 100}%"
            } else {
                locationNameText.text = "No NPCs available"
                locationProgressText.text = "0%"
            }
        }
    }

    class NpcViewHolder(itemView: View, val onNpcClick: (NpcMetaDto) -> Unit): RecyclerView.ViewHolder(itemView) {
        private val npcQuestText: TextView = itemView.findViewById(R.id.npc_quest_text)
        private val questProgressText: TextView = itemView.findViewById(R.id.npc_progress_text)
        private val questProgressBar = itemView.findViewById<ProgressBar>(R.id.npc_progress_bar)
        private val npcMoveButton: TextView = itemView.findViewById(R.id.npc_move_button)

        fun bind(npc: NpcMetaDto) {
            npcQuestText.text = npc.questName
            questProgressText.text = "${npc.numOfClearedQuests}/${npc.numOfQuests}"
            questProgressBar.max = npc.numOfQuests
            questProgressBar.progress = npc.numOfClearedQuests
            npcMoveButton.setOnClickListener {
                onNpcClick(npc)
            }
        }
    }
}