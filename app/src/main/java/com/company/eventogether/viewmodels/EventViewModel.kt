package com.company.eventogether.viewmodels

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.eventogether.R
import com.company.eventogether.helpclasses.StringResourcesProvider
import com.company.eventogether.helpclasses.Tools
import com.company.eventogether.helpclasses.Tools.convertSerpApiDateToTimeInMillis
import com.company.eventogether.helpclasses.Tools.getTimeArray
import com.company.eventogether.helpclasses.Tools.getLocalTimeStringHHMM
import com.company.eventogether.helpclasses.reminders.RemindersManager
import com.company.eventogether.model.*
import com.company.eventogether.repositories.EventRepository
import com.company.eventogether.repositories.LocationRepository
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.java.KoinJavaComponent.inject
import java.util.*
import java.util.Date
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EventViewModel(private val userProfileViewModel: UserProfileViewModel) : ViewModel() {

    private lateinit var db: FirebaseDatabase
    private lateinit var options: FirebaseRecyclerOptions<EventDTO>

    var eventObservable: MutableLiveData<EventDTO> = MutableLiveData()
    var remindersObservable: MutableLiveData<ArrayList<ReminderDTO>> = MutableLiveData()
    var addReminderObservable: MutableLiveData<ReminderDTO> = MutableLiveData()
    var deleteReminderObservable: MutableLiveData<ReminderDTO> = MutableLiveData()
    var updateReminderObservable: MutableLiveData<ReminderDTO> = MutableLiveData()
    var isDataLoaded: MutableLiveData<Boolean> = MutableLiveData()
    var isEventAddedSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var isEventDeletedSuccess: MutableLiveData<Boolean> = MutableLiveData()

    private val eventRepository: EventRepository by inject(EventRepository::class.java)
    private val locationRepository: LocationRepository by inject(LocationRepository::class.java)
    private val stringResourcesProvider: StringResourcesProvider by inject(StringResourcesProvider::class.java)
    private val remindersManager: RemindersManager by inject(RemindersManager::class.java)

    init {
        initiateDatabaseReference()
    }

    companion object {
        private const val TAG = "EventViewModel"
        private const val TAG_TIME_PICKER = "ReminderTimePicker"
        const val EVENTS_CHILD = "events"
        const val REMINDERS_CHILD = "reminders"
        const val USERS_PARENT = "users"
    }

    fun getOptions(): FirebaseRecyclerOptions<EventDTO> {
        return options
    }

    fun updateEvents(
        context: Context,
        type: EventType,
        location: LocationDTO,
        callback: (Boolean?) -> Unit
    ) {

        val currentDate = Date()
        val loadedEvents = ArrayList<EventDTO>()

        loadAllEvent { events ->

            eventRepository.retrieveEvents(type.name, location) { eventSearch ->

                if (events != null && events.size != 0) {

                    loadedEvents.addAll(events)

                    events.forEach { event ->

                        event.info?.timeInMillis.let { eventTimeInMillis ->

                            val eventDate = eventTimeInMillis?.let { Date(it) }

                            if (eventDate != null) {
                                if (eventDate.before(currentDate)) {
                                    deleteEvent(context, event)
                                    loadedEvents.remove(event)
                                }
                            }
                        }
                    }
                }

                if (eventSearch != null) {

                    eventSearch.eventsResults?.forEach { event ->

                        if (event != null) {

                            val timeInMillis =
                                event.date?.let { convertSerpApiDateToTimeInMillis(it) }
                            val date = timeInMillis?.let { Date(it) }
                            val pattern = "EEEE dd MMM' 'HH:mm"
                            val timeString = date?.let { dateToFormat ->
                                Tools.formatDateToTimeString(
                                    dateToFormat,
                                    pattern
                                ).also { Tools.capitalizeString(it) }
                            }

                            EventDTO(
                                info = InfoDTO(
                                    title = event.title,
                                    description = event.description,
                                    location = location,
                                    timeInMillis = timeInMillis,
                                    timeString = timeString,
                                    venue = event.venue
                                ),
                                links = LinksDTO(
                                    eventLink = event.link,
                                    eventImageUrl = event.image,
                                    eventThumbnailUrl = event.thumbnail,
                                    ticketUrl = event.ticketInfo?.get(0)?.link
                                ),
                                metadata = MetaDataDTO(
                                    owner = null,
                                    visibility = EventVisibility.PUBLIC,
                                    eventType = type,
                                    creationTimeInMillis = Calendar.getInstance().timeInMillis
                                ),
                                followers = ArrayList<String>().apply {
                                    userProfileViewModel.getCurrentUser()
                                        ?.let { this.add(it.uid) }
                                }
                            ).also { newEvent ->

                                var isFound = false

                                if (loadedEvents.size != 0) {

                                    loadedEvents.forEach { loadedEvent ->
                                        if (loadedEvent.info == newEvent.info) {
                                            if (isFound) {
                                                deleteEvent(context, loadedEvent)
                                            } else {
                                                isFound = true
                                            }
                                        }
                                    }

                                    if (!isFound) addEvent(newEvent) { callback(it) }
                                } else {
                                    addEvent(newEvent) { callback(it) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addEvent(event: EventDTO, callback: (Boolean?) -> Unit) {

        try {
            val uid = userProfileViewModel.getCurrentUser()?.uid
            db.reference.child("/$USERS_PARENT/$uid/${EVENTS_CHILD}").push().apply {
                event.fbKey = key
                setValue(event)
                    .addOnSuccessListener {
                        isEventAddedSuccess.value = true
                        callback(true)
                        Log.d(TAG, "Event added: ${event.info?.title}")
                    }
            }
        } catch (e: Exception) {
            Log.d(TAG, "A problem occurred while adding event: $e")
        }
    }

    private fun loadAllEvent(callback: (ArrayList<EventDTO>?) -> Unit) {

        try {
            val uid = userProfileViewModel.getCurrentUser()?.uid
            db.reference.child("/$USERS_PARENT/$uid/${EVENTS_CHILD}").get()
                .addOnSuccessListener {

                    val genericTypeIndicator: GenericTypeIndicator<HashMap<String, EventDTO>> =
                        object : GenericTypeIndicator<HashMap<String, EventDTO>>() {}

                    val eventMap = it.getValue(genericTypeIndicator)
                    val eventList: ArrayList<EventDTO> = ArrayList()

                    eventMap?.forEach { event ->
                        eventList.add(event.value)
                    }

                    callback(eventList)
                }
        } catch (e: Exception) {
            Log.d(TAG, "A problem occurred while loading events from database: $e")
        }
    }

    fun loadEvent(fbKey: String, callback: (EventDTO?) -> Unit) {

        try {
            val uid = userProfileViewModel.getCurrentUser()?.uid
            db.reference.child("/$USERS_PARENT/$uid/${EVENTS_CHILD}").child(fbKey).get()
                .addOnSuccessListener {

                    it.getValue(EventDTO::class.java).let { event ->
                        if (event != null) {
                            eventObservable.value = event
                            remindersObservable.value = event.reminders!!
                            callback(event)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.d(TAG, "A problem occurred while loading event from database: $e")
        }
    }

    fun deleteEvent(context: Context, event: EventDTO?) {

        try {
            val uid = userProfileViewModel.getCurrentUser()?.uid
            event?.fbKey?.let {
                db.reference.child("/$USERS_PARENT/$uid/${EVENTS_CHILD}").child(it)
                    .removeValue()
                    .addOnSuccessListener {
                        deleteAllReminders(context, event)
                        isEventDeletedSuccess.value = true
                        Log.d(TAG, "Event deleted: ${event.info?.title}")
                    }
            }
        } catch (e: Exception) {
            Log.d(TAG, "A problem occurred while deleting event: $e")
        }
    }

    fun getEventLocationPhoto(location: LocationDTO, callback: (Any?) -> Unit) {

        location.placeId?.let {
            locationRepository.getPlaceDetails(it) { placeDetails ->

                if (placeDetails != null) {
                    placeDetails.result?.photos.let { photoList ->

                        if (photoList != null && photoList.isNotEmpty()) {
                            photoList[0]?.photoReference.let { photoRef ->

                                if (photoRef != null) {
                                    locationRepository.getPlaceImage(photoRef) { placePhoto ->

                                        if (placePhoto != null) {
                                            Log.d(TAG, "Photo: ${placePhoto.javaClass}")
                                            callback(placePhoto)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun processReminder(
        context: Context,
        hours: Int,
        minutes: Int,
        event: EventDTO,
        reminder: ReminderDTO,
        type: String,
        callback: (Boolean?) -> Unit
    ) {

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, minutes)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        reminder.timeString = getLocalTimeStringHHMM(calendar.timeInMillis)

        val position = reminder.position
        val isRecurring = reminder.isRecurring

        if (position != null && isRecurring != null) {

            val title = stringResourcesProvider.getString(R.string.title_reminder)
            val text = stringResourcesProvider.getString(R.string.text_reminder)
            val textBig = stringResourcesProvider.getString(R.string.text_reminder_big)

            when (type) {

                "START" -> {
                    remindersManager.startReminder(
                        context = context,
                        fbKey = event.fbKey!!,
                        calendar = calendar,
                        isRecurring = isRecurring,
                        title = title,
                        text = text,
                        textBig = textBig + """ ${event.info?.title}"""
                    )
                }

                "STOP" -> {
                    remindersManager.stopReminder(
                        context = context,
                        calendar = calendar
                    )
                }
            }
            callback(true)
        } else {
            callback(false)
        }
    }

    fun createReminder(context: Context, event: EventDTO, reminder: ReminderDTO) {

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE) + 1

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText(stringResourcesProvider.getString(R.string.time_picker_title_reminder))
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .build()

        timePicker.show(
            (context as AppCompatActivity).supportFragmentManager,
            TAG_TIME_PICKER
        )

        timePicker.addOnPositiveButtonClickListener {

            processReminder(
                context = context,
                hours = timePicker.hour,
                minutes = timePicker.minute,
                event = event,
                reminder = reminder,
                type = "START"
            ) { boolean ->

                if (boolean == true) {
                    event.apply {
                        reminder.position?.let { position ->

                            if (reminder.position != -1) {
                                reminders?.removeAt(position)
                                reminders?.add(position, reminder)
                            } else {
                                reminders?.add(reminder)
                            }
                        }
                    }
                    updateEventWithReminders(event)
                } else {
                    Log.d(TAG, "A problem occurred while processing reminder: $reminder")
                }
            }
        }
    }

    private fun updateEventWithReminders(event: EventDTO) {

        event.fbKey?.let { key ->

            val list = ArrayList<ReminderDTO>()
            val hashMap: HashMap<String, Any> = HashMap()

            event.reminders?.forEach { reminder ->
                list.add(reminder)
            }
            hashMap[REMINDERS_CHILD] = list

            try {
                val uid = userProfileViewModel.getCurrentUser()?.uid
                db.reference.child("/$USERS_PARENT/$uid/${EVENTS_CHILD}").child(key)
                    .updateChildren(hashMap)
                    .addOnSuccessListener {
                        event.reminders?.let {
                            remindersObservable.value = it
                        }
                    }
            } catch (e: Exception) {
                Log.d(TAG, "A problem occurred while updating reminders: $e")
            }
        }
    }

    fun deleteReminder(context: Context, event: EventDTO, reminder: ReminderDTO) {

        reminder.timeString?.let { string ->

            val timeArray = getTimeArray(string, ":")
            val hours = timeArray[0].toInt()
            val minutes = timeArray[1].toInt()

            processReminder(
                context = context,
                hours = hours,
                minutes = minutes,
                event = event,
                reminder = reminder,
                type = "STOP"
            ) { boolean ->

                if (boolean == true) {

                    reminder.position?.let { position ->
                        event.reminders?.removeAt(position)
                        updateEventWithReminders(event)
                    }
                } else {
                    Log.d(TAG, "A problem occurred while processing reminder: $reminder")
                }
            }
        }
    }

    fun updateReminder(context: Context?, event: EventDTO?, reminder: ReminderDTO) {

        if (context != null && event != null) {

            // Handle update when alarm is set by user

            reminder.timeString?.let { string ->

                val timeArray = getTimeArray(string, ":")
                val hours = timeArray[0].toInt()
                val minutes = timeArray[1].toInt()

                processReminder(
                    context = context,
                    hours = hours,
                    minutes = minutes,
                    event = event,
                    reminder = reminder,
                    type = "START"
                ) { boolean ->

                    if (boolean == true) {

                        reminder.position?.let { position ->
                            event.apply {
                                if (reminder.position != -1) {
                                    reminders?.removeAt(position)
                                    reminders?.add(position, reminder)
                                } else {
                                    reminders?.add(reminder)
                                }
                            }
                            updateEventWithReminders(event)
                        }
                    } else {
                        Log.d(
                            TAG,
                            "A problem occurred while processing reminder: $reminder"
                        )
                    }
                }
            }
        } else {

            // Handle update when alarm notification is received

            reminder.fbKey?.let {
                loadEvent(it) { event ->

                    if (event != null) {
                        val reminderToUpdate =
                            event.reminders?.filter { reminder ->
                                reminder.timeString == reminder.timeString
                                        && reminder.isRecurring == reminder.isRecurring
                            }
                        event.reminders?.remove(reminderToUpdate?.get(0) ?: 0)
                        event.reminders?.add(reminder)

                        updateEventWithReminders(event)
                    }
                }
            }
        }
    }

    fun rescheduleAllReminders(context: Context) {

        try {
            val uid = userProfileViewModel.getCurrentUser()?.uid
            db.reference.child("/$USERS_PARENT/$uid/${EVENTS_CHILD}").get()
                .addOnCompleteListener { snapshot1 ->
                    if (snapshot1.isSuccessful) {
                        val events = snapshot1.result.children.mapNotNull { doc ->
                            doc.getValue(EventDTO::class.java)
                        }

                        events.forEach { event ->

                            event.fbKey?.let { fbKey ->
                                db.reference.child("/$USERS_PARENT/$uid/${EVENTS_CHILD}")
                                    .child(fbKey).child(REMINDERS_CHILD)
                                    .get()
                                    .addOnCompleteListener { snapshot2 ->
                                        if (snapshot2.isSuccessful) {
                                            val reminders =
                                                snapshot2.result.children.mapNotNull { doc ->
                                                    doc.getValue(ReminderDTO::class.java)
                                                }

                                            reminders.forEach { reminder ->

                                                val isActive = reminder.isActive
                                                val timeString = reminder.timeString

                                                if (isActive == true && timeString != null) {

                                                    val timeArray =
                                                        getTimeArray(timeString, ":")
                                                    val hours = timeArray[0].toInt()
                                                    val minutes = timeArray[1].toInt()

                                                    Log.d(TAG, "Hours: $hours")

                                                    processReminder(
                                                        context = context,
                                                        hours = hours,
                                                        minutes = minutes,
                                                        event = event,
                                                        reminder = reminder,
                                                        type = "START"
                                                    ) { boolean ->

                                                        if (boolean == false) {
                                                            Log.d(
                                                                TAG,
                                                                "A problem occurred while processing reminder: $reminder"
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            Log.d(
                                                TAG,
                                                snapshot2.exception?.message.toString()
                                            )
                                        }
                                    }
                            }
                        }
                    } else {
                        Log.d(TAG, snapshot1.exception?.message.toString())
                    }
                }
        } catch (e: Exception) {
            Log.d(TAG, "A problem occurred while rescheduling reminders: $e")
        }
    }

    private fun deleteAllReminders(context: Context, event: EventDTO) {

        event.reminders?.forEach { reminder ->

            val position = reminder.position
            val timeString = reminder.timeString

            if (position != null && timeString != null) {

                val timeArray = getTimeArray(timeString, ":")
                val hours = timeArray[0].toInt()
                val minutes = timeArray[1].toInt()

                val calendar = Calendar.getInstance()

                calendar.set(Calendar.HOUR_OF_DAY, hours)
                calendar.set(Calendar.MINUTE, minutes)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                remindersManager.stopReminder(
                    context = context,
                    calendar = calendar
                )
            }
        }
    }

    private fun initiateDatabaseReference() {

        db = Firebase.database
        val uid = userProfileViewModel.getCurrentUser()?.uid
        val eventsRef = db.reference.child("/$USERS_PARENT/$uid/${EVENTS_CHILD}")
            .orderByChild("info/timeInMillis")

        options = FirebaseRecyclerOptions.Builder<EventDTO>()
            .setQuery(eventsRef, EventDTO::class.java)
            .build()
    }
}