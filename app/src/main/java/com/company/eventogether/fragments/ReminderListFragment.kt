package com.company.eventogether.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.company.eventogether.adapters.ReminderListAdapter
import com.company.eventogether.databinding.FragmentRemindersBinding
import com.company.eventogether.model.EventDTO
import com.company.eventogether.model.EventReminderDTO
import com.company.eventogether.viewmodels.EventViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ReminderListFragment : Fragment() {

    private lateinit var binding: FragmentRemindersBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReminderListAdapter
    private lateinit var event: EventDTO

    private val eventViewModel: EventViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        event = arguments?.getSerializable("Event") as EventDTO
        binding = FragmentRemindersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            })

        recyclerView = binding.recyclerView
        adapter = activity.let {
            recyclerView.layoutManager = LinearLayoutManager(it)
            ReminderListAdapter(eventViewModel)
        }
        recyclerView.adapter = adapter

        binding.btnAdd.setOnClickListener {
            eventViewModel.createReminder(
                requireActivity(),
                event,
                EventReminderDTO(
                    fbKey = null,
                    position = -1,
                    timeString = "HH:MM",
                    isRecurring = true,
                    isActive = true
                )
            )
        }

        setObservers()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setObservers() {

        eventViewModel.remindersObservable.observe(viewLifecycleOwner) { reminders ->

            reminders?.let {
                adapter.reminderList.clear()
                adapter.reminderList.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }

        eventViewModel.addReminderObservable.observe(viewLifecycleOwner) { reminder ->

            if (reminder != null) {
                eventViewModel.createReminder(requireActivity(), event, reminder)
                eventViewModel.addReminderObservable.value = null
            }
        }

        eventViewModel.deleteReminderObservable.observe(viewLifecycleOwner) { reminder ->

            if (reminder != null) {
                eventViewModel.deleteReminder(requireActivity(), event, reminder)
                eventViewModel.deleteReminderObservable.value = null
            }
        }

        eventViewModel.updateReminderObservable.observe(viewLifecycleOwner) { reminder ->

            if (reminder != null) {
                eventViewModel.updateReminder(requireActivity(), event, reminder)
                eventViewModel.updateReminderObservable.value = null
            }
        }
    }
}