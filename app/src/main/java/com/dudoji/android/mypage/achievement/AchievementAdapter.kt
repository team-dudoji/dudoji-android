package com.dudoji.android.mypage.achievement

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.mypage.achievement.distance.DistanceActivity
import com.dudoji.android.mypage.achievement.speed.SpeedActivity
import com.dudoji.android.mypage.achievement.time.TimeActivity

//데이터 묶음 skr~
data class Category(
    val title: String,         // 카테고리 제목
    val items: List<CategoryItem> // 카테고리 항목들
)
//카테고리 안에 포함된 세부 항목
data class CategoryItem(
    val label: String,          // 카드의 제목
    val description: String     // 카드의 상세 설명
)


class AchievementAdapter(private val categories: List<Category>) :

    RecyclerView.Adapter<AchievementAdapter.CategoryViewHolder>() { //리사이클러뷰에 들어갈 데이터를 다루는 어뎁터데스요
    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {    //CategoryViewHolder는 하나의 리스트 아이템 뷰를 다루는 클래스입니당
        val categoryTitle: TextView = itemView.findViewById(R.id.categoryTitle)
        val itemsContainer: LinearLayout = itemView.findViewById(R.id.itemsContainer)
        val viewAllButton: TextView = itemView.findViewById(R.id.viewAllButton)

        // 외부에서 들어온 데이터를 리사이클뷰에 동적으로 스껄~
        fun bind(category: Category) {
            categoryTitle.text = category.title
            categoryTitle.text = category.title
            itemsContainer.removeAllViews()

            // 👉 원래 아이템 리스트를 복사해서 수정 가능하게 만듦
            val items = category.items.toMutableList()

            // 👉 항목이 3개보다 부족하면 빈 카드 추가
            while (items.size < 3) {
                items.add(CategoryItem("", "")) // label과 description이 빈 값인 아이템
            }

            // 👉 리스트 순회하며 카드 뷰 생성
            items.forEach { item ->
                val itemView = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.item_card, itemsContainer, false)

                val labelText = itemView.findViewById<TextView>(R.id.cardLabel)
                val descriptionText = itemView.findViewById<TextView>(R.id.cardDescription)

                labelText.text = item.label
                descriptionText.text = item.description

                // 👉 값이 비어 있는 카드라면 투명도 처리 (선택)
                if (item.label.isBlank() && item.description.isBlank()) {
                    itemView.alpha = 0.4f // 흐릿하게 보여줌
                }

                itemsContainer.addView(itemView)
            }

            viewAllButton.setOnClickListener {
                val intent = when (category.title) {
                    "이동거리" -> Intent(itemView.context, DistanceActivity::class.java)
                        .putExtra("CATEGORY_TITLE", category.title)
                    "시간" -> Intent(itemView.context, TimeActivity::class.java)
                        .putExtra("CATEGORY_TITLE", category.title)
                    "속도" -> Intent(itemView.context, SpeedActivity::class.java)
                        .putExtra("CATEGORY_TITLE", category.title)
                    else -> null
                }
                intent?.let { itemView.context.startActivity(it) }
            }


        }

    }

    //리사이클뷰가 새 뷰를 만들때 호출 되는 놈
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }
    //category 데이터를 holder에 연결시키는 역할
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    //몇 개의 항목을 보여줄지 리사이클뷰에 알려줌
    override fun getItemCount(): Int = categories.size


}
