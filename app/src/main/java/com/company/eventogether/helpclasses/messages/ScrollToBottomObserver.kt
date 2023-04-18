package com.company.eventogether.helpclasses.messages

import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.company.eventogether.adapters.ChatMessageAdapter

class ScrollToBottomObserver(
    private val progressBar: ProgressBar,
    private val recycler: RecyclerView,
    private val adapter: ChatMessageAdapter,
    private val manager: LinearLayoutManager
) : AdapterDataObserver() {

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        val count = adapter.itemCount
        val lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition()
        val loading = lastVisiblePosition == -1
        val atBottom = positionStart >= count - 1 && lastVisiblePosition == positionStart - 1
        if (loading || atBottom) {
            recycler.scrollToPosition(positionStart)
            progressBar.isVisible = false
        }
    }
}