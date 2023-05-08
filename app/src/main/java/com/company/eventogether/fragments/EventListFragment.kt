package com.company.eventogether.fragments

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.company.eventogether.R
import com.company.eventogether.activities.MainActivity
import com.company.eventogether.adapters.EventListAdapter
import com.company.eventogether.databinding.FragmentEventListBinding
import com.company.eventogether.helpclasses.events.EventListObserver
import com.company.eventogether.model.EventDTO
import com.company.eventogether.viewmodels.EventViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class EventListFragment : Fragment() {

    private lateinit var binding: FragmentEventListBinding
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: EventListAdapter
    private lateinit var timer : CountDownTimer

    private val eventViewModel: EventViewModel by activityViewModel()

    companion object {
        private const val TAG = "EventListFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    callMainActivity()
                }
            })

        manager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = manager
        adapter = EventListAdapter(eventViewModel)
        binding.recyclerView.adapter = adapter

        adapter.registerAdapterDataObserver(
            EventListObserver(binding.recyclerView, adapter, manager)
        ).also {

            timer = object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    context?.let {
                        checkListSize()
                    }
                }
            }
            timer.start()
        }

        binding.btnAddNewEvent.setOnClickListener {
//            eventViewModel.retrieveEvent()
            binding.progressBar.isVisible = true
        }

        setObservers()
    }

    private fun checkListSize() {

        try {

            if (adapter.itemCount == 0) {
                binding.progressBar.isVisible = false
                Toast.makeText(
                    requireContext(),
                    R.string.load_empty_event_list,
                    Toast.LENGTH_SHORT
                ).show()
            }

        } catch (e: Exception) {
            Log.d(TAG, "Fragment is likely loaded from notification intent: $e")
        }
    }

    private fun setObservers() {

        eventViewModel.isDataLoaded.observe(viewLifecycleOwner) { boolean ->
            if (boolean) {
                binding.progressBar.isVisible = false
                eventViewModel.isDataLoaded.value = false
            }
        }

        eventViewModel.eventObservable.observe(viewLifecycleOwner) { event ->
            if (event != null) {
                callFocusEventFragment(event)
            }
        }

        eventViewModel.isEventAddedSuccess.observe(viewLifecycleOwner) { boolean ->
            if (boolean) {
                Toast.makeText(
                    requireActivity(),
                    R.string.text_event_add_succes,
                    Toast.LENGTH_SHORT
                ).show()
                eventViewModel.isEventAddedSuccess.value = false
            }
        }
    }

    private fun callMainActivity() {

        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun callFocusEventFragment(event: EventDTO?) {

        val fragmentManager: FragmentManager = parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val focusEventFragment = FocusEventFragment().apply {
            val bundle = Bundle()
            bundle.putSerializable("Event", event)
            this.arguments = bundle
        }

        fragmentTransaction.replace(R.id.frame, focusEventFragment).addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onDestroy() {
        adapter.stopListening()
        timer.cancel()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }
}