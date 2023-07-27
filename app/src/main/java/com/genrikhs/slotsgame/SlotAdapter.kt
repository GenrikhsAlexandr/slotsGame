package com.genrikhs.slotsgame

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.doOnNextLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView

class SlotAdapter : RecyclerView.Adapter<SlotAdapter.SlideViewHolder>() {

    private val slides = listOf(
        R.drawable.ico_1,
        R.drawable.ico_2,
        R.drawable.ico_3,
        R.drawable.ico_4,
        R.drawable.ico_5,
        R.drawable.ico_6,
        R.drawable.ico_7,
        R.drawable.ico_8,
    )

    private var itemHeight: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val viewHeight = if (itemHeight > 0) {
            itemHeight
        } else {
            ViewGroup.LayoutParams.WRAP_CONTENT
        }
        val view = AppCompatImageView(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                viewHeight
            )
        }
        parent.doOnNextLayout {
            itemHeight = parent.height / 3
            view.updateLayoutParams<RecyclerView.LayoutParams> {
                this.height = itemHeight
            }
        }
        return SlideViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        holder.bind(slide = slides[position % slides.size])
    }

    class SlideViewHolder(view: AppCompatImageView) : RecyclerView.ViewHolder(view) {

        fun bind(slide: Int) {
            (itemView as AppCompatImageView).setImageResource(slide)
        }
    }
}