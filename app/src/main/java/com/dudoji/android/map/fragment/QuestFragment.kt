package com.dudoji.android.map.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.dudoji.android.R
import com.dudoji.android.databinding.QuestModalBinding
import com.dudoji.android.map.adapter.QuestAdapter
import com.dudoji.android.mock.npc.NPC_QUEST_DTO_MOCK
import com.dudoji.android.mypage.api.dto.NpcQuestDto
import com.dudoji.android.mypage.api.dto.QuestDto
import com.dudoji.android.util.modal.ModalFragment
import kotlinx.coroutines.launch
import retrofit2.Response

class QuestFragment(val npcId: Long): ModalFragment() {
    private var _binding: QuestModalBinding? = null
    private val binding get() = _binding!!

    private lateinit var npcQuestDto: NpcQuestDto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = QuestModalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch{
            initViews()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun initViews() {
        val response = Response.success(NPC_QUEST_DTO_MOCK) // RetrofitClient.npcQuestApiService.getNpcQuest(npcId)

        if (response.isSuccessful) {
            npcQuestDto = response.body() ?: throw IllegalStateException("NPC Quest data is null")
        } else {
            close()
            return
        }

        (binding.root.parent.parent as ViewGroup).findViewById<TextView>(R.id.npc_speech_text).text = npcQuestDto.npcScript
        binding.questImage.load(npcQuestDto.imageUrl) {
            crossfade(true)
            placeholder(R.mipmap.photo_placeholder)
            error(R.mipmap.photo_placeholder)
        }
        binding.questTitle.text = "<${npcQuestDto.name}>"
        binding.questDescription.text = npcQuestDto.description

        binding.questRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.questRecyclerView.adapter = QuestAdapter(npcQuestDto.quests.map(QuestDto::toDomain))
    }
}