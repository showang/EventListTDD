package com.example.event_list.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.event_list.R
import com.example.event_list.model.EventItem
import me.showang.recyct.RecyctViewHolder
import me.showang.recyct.items.RecyctItemBase
import java.text.SimpleDateFormat

class EventRecyctItem : RecyctItemBase() {
    override fun create(inflater: LayoutInflater, parent: ViewGroup) =
        object : RecyctViewHolder(inflater, parent, R.layout.item_event) {
            val titleLabel: TextView by id(R.id.eventItem_titleText)
            val categoryLabel: TextView by id(R.id.eventItem_categoryText)
            val startDate: TextView by id(R.id.eventItem_startDateText)
            val endDate: TextView by id(R.id.eventItem_endDateText)

            override fun bind(data: Any, atIndex: Int) {
                val event = data as? EventItem ?: run { return }
                titleLabel.text = event.title
                event.category?.run {
                    categoryLabel.text = name
                    categoryLabel.visibility = View.VISIBLE
                } ?: run { categoryLabel.visibility = View.INVISIBLE }
                startDate.text = SimpleDateFormat.getDateInstance().format(event.startDate)
                endDate.text = SimpleDateFormat.getDateInstance().format(event.endDate)
            }

        }
}