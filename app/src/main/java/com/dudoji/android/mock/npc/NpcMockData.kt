package com.dudoji.android.mock.npc

import com.dudoji.android.mypage.api.dto.NpcDto
import com.dudoji.android.mypage.api.dto.NpcQuestDto
import com.dudoji.android.mypage.api.dto.QuestDto
import com.dudoji.android.mypage.domain.MissionUnit
import com.dudoji.android.mypage.domain.QuestType

val NPC_DTO_MOCK: List<NpcDto> = listOf<NpcDto>(
    NpcDto(
        npcId = 1L,
        lat = 35.230985,
        lng = 129.082111,
        name = "NPC One",
        npcSkinUrl = "https://avatars.githubusercontent.com/u/88422717?v=4",
        hasQuest = true
    ),
    NpcDto(
        npcId = 1L,
        lat = 35.231985,
        lng = 129.083111,
        name = "NPC Two",
        npcSkinUrl = "https://avatars.githubusercontent.com/u/88422717?v=4",
        hasQuest = true
    ),
)

val NPC_QUEST_DTO_MOCK: NpcQuestDto = NpcQuestDto (
    npcId = 1L,
    name = "NPC One",
    imageUrl = "https://avatars.githubusercontent.com/u/88422717?v=4",
    npcScript = "Hello, I am NPC One. How can I help you?",
    description = "This is a quest description for NPC One.",
    quests = listOf(
        QuestDto(
            title = "First Quest",
            currentValue = 0,
            goalValue = 10,
            unit = MissionUnit.DISTANCE,
            type = QuestType.NPC,
        ),
        QuestDto(
            title = "Second Quest",
            currentValue = 0,
            goalValue = 10,
            unit = MissionUnit.DISTANCE,
            type = QuestType.NPC,
        )
    )
)