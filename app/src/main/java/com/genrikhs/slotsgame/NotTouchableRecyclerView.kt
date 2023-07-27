package com.genrikhs.slotsgame

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class NotTouchableRecyclerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attributeSet, defStyleAttr) {

    private val blockingTouchListener = OnTouchListener { v, event -> true }

    init {
        setOnTouchListener(blockingTouchListener)
        setHasFixedSize(true)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }
}