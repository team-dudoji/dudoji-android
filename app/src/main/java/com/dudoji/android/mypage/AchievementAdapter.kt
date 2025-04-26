package com.dudoji.android.mypage

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R

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

            // 카드를 동적으로 스껄~
            itemsContainer.removeAllViews() // 기존 아이템을 모두 삭제, 왜냐하면 리사이클 해야되니깐
            category.items.forEach { item ->
                val itemView = LayoutInflater.from(itemView.context).inflate(R.layout.item_card, itemsContainer, false)
                val labelText = itemView.findViewById<TextView>(R.id.cardLabel)
                val descriptionText = itemView.findViewById<TextView>(R.id.cardDescription)

                labelText.text = item.label
                descriptionText.text = item.description

                itemsContainer.addView(itemView)
            }

            // 전체보기 버튼 클릭 시 동작
            viewAllButton.setOnClickListener {
                // StatisticsActivity로 이동
                val intent = Intent(itemView.context, StatisticsActivity::class.java)
                itemView.context.startActivity(intent)
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
