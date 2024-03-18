package com.company.eventogether.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.company.eventogether.R
import com.company.eventogether.adapters.ExpandableListAdapter
import com.company.eventogether.databinding.FragmentFocusEventBinding
import com.company.eventogether.model.EventDTO
import com.company.eventogether.viewmodels.EventViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.parameter.parametersOf
import pokercc.android.expandablerecyclerview.ExpandableRecyclerView

class FocusEventFragment : Fragment() {

    private lateinit var binding: FragmentFocusEventBinding
    private lateinit var manager: LinearLayoutManager
    private lateinit var listView: ExpandableRecyclerView
    private lateinit var event: EventDTO

    private val eventViewModel: EventViewModel by activityViewModel()
    private val callback: (String) -> Unit = { url ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
    private val adapter: ExpandableListAdapter by inject {
        parametersOf(callback)
    }

    companion object {
        private const val TAG = "FocusEventFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        event = arguments?.getSerializable("Event") as EventDTO
        binding = FragmentFocusEventBinding.inflate(inflater, container, false)
        binding.event = event
        Log.d(TAG, "Event loaded: $event")
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
        }

        binding.switchRecurring.setOnClickListener {

        }

        setObservers()
        setEventImage(binding.eventImage, event.info?.thumbnailUrl)
    }

    private fun setObservers() {

        eventViewModel.eventObservable.observe(viewLifecycleOwner) { event ->

            if (event?.expandableLists != null) {
                adapter.expandableList.clear()
                adapter.expandableList.addAll(event.expandableLists!!)
                adapter.expandGroup(0, false)
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
}