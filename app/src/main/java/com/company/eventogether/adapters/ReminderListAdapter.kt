package com.company.eventogether.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.eventogether.R
import com.company.eventogether.databinding.CardDesignRemindersBinding
import com.company.eventogether.helpclasses.StringResourcesProvider
import com.company.eventogether.model.EventReminderDTO
import com.company.eventogether.viewmodels.EventViewModel
import org.koin.java.KoinJavaComponent.inject

class ReminderListAdapter(private val eventViewModel: EventViewModel) :
    RecyclerView.Adapter<ReminderListAdapter.ReminderViewHolder>() {

    var reminderList = ArrayList<EventReminderDTO>()
    val stringResourcesProvider: StringResourcesProvider by inject(StringResourcesProvider::class.java)

    inner class ReminderViewHolder(private val binding: CardDesignRemindersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reminder: EventReminderDTO) {

            binding.reminder = reminder

            setTextColor(reminder.isActive == true)

            binding.reminderTime.setOnClickListener {
                eventViewModel.addReminderObservable.value =
                    EventReminderDTO(
                        position = reminder.position,
                        timeString = "HH:MM",
                        isRecurring = binding.switchRecurring.isChecked
                    )
            }

            binding.btnDelete.setOnClickListener {
                eventViewModel.deleteReminderObservable.value = reminder
            }

            binding.switchRecurring.setOnCheckedChangeListener { _, isChecked ->

                setTextColor(isChecked)

                eventViewModel.updateReminderObservable.value =

                    EventReminderDTO(
                        fbKey = reminder.fbKey,
                        position = reminder.position,
                        timeString = reminder.timeString,
                        isRecurring = isChecked,
                        isActive = when (reminder.isActive == true) {
                            true -> {
                                reminder.isActive
                            }

                            false -> {
                                isChecked
                            }
                        }
                    )
            }
        }

        private fun setTextColor(boolean: Boolean) {

            when (boolean) {
                true -> {
                    binding.reminderTime.setTextColor(
                        stringResourcesProvider.getColor(R.color.dark_blue)
                    )
                }

                false -> {
                    binding.reminderTime.setTextColor(
                        stringResourcesProvider.getColor(R.color.dark_blue_faded)
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding =
            CardDesignRemindersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminderList[position]
        reminder.position = position
        holder.bind(reminder)
    }

    override fun getItemCount(): Int {
        return reminderList.size
    }
}