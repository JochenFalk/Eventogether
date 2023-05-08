package com.company.eventogether.helpclasses.events

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.company.eventogether.adapters.EventListAdapter
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EventListObserver(
    private val recycler: RecyclerView,
    private val adapter: EventListAdapter,
    private val manager: LinearLayoutManager
) : AdapterDataObserver() {

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        val count = adapter.itemCount
        val lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition()
        val loading = lastVisiblePosition == -1
        val atBottom = positionStart >= count - 1 && lastVisiblePosition == positionStart - 1
//        if (loading || atBottom) {
//            recycler.scrollToPosition(positionStart)
//        }
    }
}