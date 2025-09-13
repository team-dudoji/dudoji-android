package com.dudoji.android.map.fragment

import RetrofitClient
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.dudoji.android.databinding.NpcListModalBinding
import com.dudoji.android.map.adapter.NpcListAdapter
import com.dudoji.android.map.api.dto.NpcMetaDto
import com.dudoji.android.presentation.map.MapActivity
import com.dudoji.android.util.modal.ModalFragment
import retrofit2.Callback

class NpcListFragment(val mapActivity: MapActivity): ModalFragment() {
    private var _binding: NpcListModalBinding? = null
    private val binding get() = _binding!!

    private lateinit var npcMetas: List<NpcMetaDto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = NpcListModalBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initViews() {
        RetrofitClient.npcQuestApiService.getQuestMetaData().enqueue(
            object : Callback<List<NpcMetaDto>> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: retrofit2.Call<List<NpcMetaDto>>, response: retrofit2.Response<List<NpcMetaDto>>) {
                    Log.d("NpcListFragment", "onResponse: ${response.raw()} ${response.body().toString()}")
                    if (response.isSuccessful) {
                        npcMetas = response.body() ?: emptyList()
                        binding.npcRecyclerView.adapter = NpcListAdapter(npcMetas){
                            mapActivity.moveTo(it.lat, it.lng)
                            close()
                        }
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<NpcMetaDto>>, t: Throwable) {
                    // Handle failure
                }
            }
        )
//        binding.npcRecyclerView.adapter = NpcListAdapter(NPC_META_DTO_MOCK) {
//            mapActivity.moveTo(it.lat, it.lng)
//            close()
//        }
        binding.npcRecyclerView.layoutManager = LinearLayoutManager(context)
    }
}
