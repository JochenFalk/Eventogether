package com.company.eventogether.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.company.eventogether.R
import com.company.eventogether.adapters.ExpandableListAdapter
import com.company.eventogether.databinding.FragmentFocusEventBinding
import com.company.eventogether.model.EventDTO
import com.company.eventogether.viewmodels.EventViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import pokercc.android.expandablerecyclerview.ExpandableRecyclerView

class FocusEventFragment : Fragment() {

    private lateinit var binding: FragmentFocusEventBinding
    private lateinit var manager: LinearLayoutManager
    private lateinit var listView: ExpandableRecyclerView
    private lateinit var event: EventDTO

    private val eventViewModel: EventViewModel by activityViewModel()
    private val adapter: ExpandableListAdapter by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        event = arguments?.getSerializable("Event") as EventDTO
        binding = FragmentFocusEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    eventViewModel.eventObservable.value = null
                    parentFragmentManager.popBackStack()
                }
            })

        listView = binding.expandableListView
        manager = LinearLayoutManager(requireContext())
        listView.layoutManager = manager
        listView.adapter = adapter.apply {
            onlyOneGroupExpand = true
//            this.listGroups = event.listGroups!!
        }

        binding.event = event

        binding.btnDelete.setOnClickListener {
            eventViewModel.deleteEvent(
                requireActivity(),
                event
            )
        }

        binding.switchRecurring.setOnClickListener {

        }

        setObservers()
        setEventImage(binding.eventImage, event.links?.eventImageUrl)
    }

    private fun setObservers() {

        eventViewModel.eventObservable.observe(viewLifecycleOwner) { event ->

            if (event?.listGroups != null) {
                adapter.listGroups.clear()
                adapter.listGroups.addAll(event.listGroups)
            }
        }

        eventViewModel.isEventDeletedSuccess.observe(viewLifecycleOwner) { boolean ->

            if (boolean) {
                Toast.makeText(
                    requireActivity(),
                    R.string.text_event_delete_success,
                    Toast.LENGTH_SHORT
                ).show()
                eventViewModel.isEventDeletedSuccess.value = false
                eventViewModel.eventObservable.value = null
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun setEventImage(imageView: ImageView, url: String?) {

        if (url != null) {
            Glide.with(imageView.context)
                .load(url)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.friends_running_down_hill_outdoor_field)
        }
    }

    private fun callReminderListFragment() {

        val fragmentManager: FragmentManager = parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val reminderListFragment = ReminderListFragment().apply {
            val bundle = Bundle()
            bundle.putSerializable("Event", event)
            this.arguments = bundle
        }

        fragmentTransaction.replace(R.id.frame, reminderListFragment).addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onResume() {
        super.onResume()

        if ((event.listGroups?.size ?: 0) != 0) {
            adapter.collapseAllGroup()
//            adapter.expandGroup(0, false)
        }
    }
}