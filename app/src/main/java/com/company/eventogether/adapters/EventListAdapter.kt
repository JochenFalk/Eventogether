package com.company.eventogether.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.company.eventogether.databinding.CardDesignEventBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.company.eventogether.model.EventDTO
import com.company.eventogether.viewmodels.EventViewModel

class EventListAdapter(private val viewModel: EventViewModel) :
    FirebaseRecyclerAdapter<EventDTO, ViewHolder>(viewModel.getOptions()) {

    inner class EventViewHolder(private val binding: CardDesignEventBinding) :
        ViewHolder(binding.root) {

        fun bind(event: EventDTO) {
            binding.event = event
            binding.cardView.setOnClickListener {
                viewModel.eventObservable.value = event
                viewModel.remindersObservable.value = event.reminders
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewModel.getOptions().snapshots.size > 0) viewModel.isDataLoaded.value = true
        val binding =
            CardDesignEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, event: EventDTO) {
        (holder as EventViewHolder).bind(event)
    }
}