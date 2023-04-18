package com.company.eventogether.viewmodels

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.company.eventogether.model.ChatMessageDTO

class ChatMessageViewModel(private val userProfileViewModel: UserProfileViewModel) : ViewModel() {

    private lateinit var db: FirebaseDatabase
    private lateinit var options: FirebaseRecyclerOptions<ChatMessageDTO>

    init {
        initiateDatabaseReference()

        // Delete all messages
//        val uid = userProfileViewModel.getCurrentUser()?.uid
//        db.reference.child("/$USERS_PARENT/$uid/${MESSAGES_CHILD}").removeValue()
    }

    companion object {
        private const val TAG = "ChatMessageViewModel"
        const val DUMMY_TEXT = "Dummy text"
        const val MESSAGES_CHILD = "messages"
        const val USERS_PARENT = "users"
    }

    fun getOptions(): FirebaseRecyclerOptions<ChatMessageDTO> {
        return options
    }

    fun pushMessage(message: ChatMessageDTO) {

        try {
            val uid = userProfileViewModel.getCurrentUser()?.uid
            db.reference.child("/$USERS_PARENT/$uid/$MESSAGES_CHILD").push().setValue(message)
        } catch (e: Exception) {
            Log.d(TAG, "A problem occurred while pushing message to database: $e")
        }
    }

    fun pushImage(activity: Activity, user: FirebaseUser, uri: Uri) {

        val tempMessage = ChatMessageDTO(
            sender = user.displayName,
            complaintDescription = null,
            photoUrl = user.photoUrl.toString()
        )

        try {
            db.reference
                .child("/$USERS_PARENT/${user.uid}/$MESSAGES_CHILD")
                .push()
                .setValue(
                    tempMessage,
                    DatabaseReference.CompletionListener { databaseError, databaseReference ->
                        if (databaseError != null) {
                            Log.w(
                                TAG, "Unable to write message to database.",
                                databaseError.toException()
                            )
                            return@CompletionListener
                        }

                        val key = databaseReference.key
                        val storageReference = Firebase.storage
                            .getReference(user.uid)
                            .child(key!!)
                            .child(uri.lastPathSegment!!)
                        putImageInStorage(activity, user, storageReference, uri, key)
                    })
        } catch (e: Exception) {
            Log.d(TAG, "A problem occurred while pushing image message to database: $e")
        }
    }

    private fun putImageInStorage(
        activity: Activity,
        user: FirebaseUser,
        storageReference: StorageReference,
        uri: Uri,
        key: String?
    ) {

        storageReference.putFile(uri)
            .addOnSuccessListener(
                activity
            ) { taskSnapshot ->

                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->

                        val chatMessage = ChatMessageDTO(
                            sender = user.displayName,
                            complaintDescription = null,
                            photoUrl = user.photoUrl.toString(),
                            imageUrl = uri.toString()
                        )

                        try {
                            db.reference
                                .child("/$USERS_PARENT/${user.uid}/$MESSAGES_CHILD")
                                .child(key!!)
                                .setValue(chatMessage)
                        } catch (e: Exception) {
                            Log.d(TAG, "A problem occurred while putting image into storage: $e")
                        }
                    }
            }
            .addOnFailureListener(activity) { e ->
                Log.w(TAG, "Image upload task was unsuccessful.", e)
            }
    }

    private fun initiateDatabaseReference() {

        db = Firebase.database
        val uid = userProfileViewModel.getCurrentUser()?.uid
        val messagesRef = db.reference.child("/$USERS_PARENT/$uid/$MESSAGES_CHILD")
        options = FirebaseRecyclerOptions.Builder<ChatMessageDTO>()
            .setQuery(messagesRef, ChatMessageDTO::class.java)
            .build()
    }
}