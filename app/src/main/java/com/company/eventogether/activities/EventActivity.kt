package com.company.eventogether.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.company.eventogether.R
import com.company.eventogether.databinding.ActivityEventBinding
import com.company.eventogether.fragments.FocusEventFragment
import com.company.eventogether.fragments.EventListFragment
import com.company.eventogether.fragments.ReminderListFragment
import com.company.eventogether.model.EventDTO
import com.company.eventogether.viewmodels.EventViewModel
import com.company.eventogether.viewmodels.UserProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventBinding
    private var event: EventDTO? = null

    private val userProfileViewModel: UserProfileViewModel by viewModel()
    private val eventViewModel: EventViewModel by viewModel()

    companion object {
        private const val TAG = "EventActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent: Intent = intent
        event = intent.getSerializableExtra("Event") as EventDTO?

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                return@addOnBackStackChangedListener
            }

            when (supportFragmentManager.fragments.last()) {

                is EventListFragment -> {

                    findViewById<ImageView>(R.id.toolbarImage).setOnClickListener {
                        callMainActivity()
                    }
                }

                is FocusEventFragment -> {

                    eventViewModel.eventObservable.value = null

                    findViewById<ImageView>(R.id.toolbarImage).setOnClickListener {
                        supportFragmentManager.popBackStack()
                    }
                }

                is ReminderListFragment -> {

                    findViewById<ImageView>(R.id.toolbarImage).setOnClickListener {
                        supportFragmentManager.popBackStack()
                    }
                }
            }
        }

        setCustomToolbar()

        if (event != null) {
            callFocusEventFragment(event)
        } else {
            setObservers()
            callEventListFragment()
        }
    }

    private fun setObservers() {

        userProfileViewModel.isSignedOutSuccess.observe(this) { boolean ->
            if (boolean) {
                callMainActivity()
                userProfileViewModel.isSignedOutSuccess.value = false
            }
        }
    }

    private fun setCustomToolbar() {

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.secondary_toolbar)

        userProfileViewModel.setCustomMenu(
            this@EventActivity, findViewById(R.id.profileIcon), findViewById(R.id.menuIcon)
        )
    }

    private fun callMainActivity() {

        val intent = Intent(this@EventActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun callEventListFragment() {

        intent?.getStringExtra("FbKey").toString().also {
            eventViewModel.loadEvent(it) { event ->
                Log.d(TAG, "Event loaded from notification: $event")
            }
        }

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val eventListFragment = EventListFragment()

        fragmentTransaction.replace(R.id.frame, eventListFragment).addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun callFocusEventFragment(event: EventDTO?) {

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val focusEventFragment = FocusEventFragment().apply {
            val bundle = Bundle()
            bundle.putSerializable("Event", event)
            this.arguments = bundle
        }

        fragmentTransaction.replace(R.id.frame, focusEventFragment).addToBackStack(null)
        fragmentTransaction.commit()
    }
}