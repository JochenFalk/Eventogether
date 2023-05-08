package com.company.eventogether.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.company.eventogether.R
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
            binding.eventTitle.isSelected = true

            setEventImage(binding.eventImage, event)
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

    private fun setEventImage(imageView: ImageView, event: EventDTO) {

        val eventImageUrl = event.links?.eventImageUrl
        val venueImageUrl = event.info?.venue?.link

        if (eventImageUrl != null) {
            Glide.with(imageView.context)
                .load(eventImageUrl)
                .into(imageView)
        } else if (venueImageUrl != null){
            Glide.with(imageView.context)
                .load(venueImageUrl)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.friends_running_down_hill_outdoor_field)
        }
    }
}