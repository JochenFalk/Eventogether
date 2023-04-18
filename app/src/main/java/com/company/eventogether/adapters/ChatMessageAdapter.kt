package com.company.eventogether.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.company.eventogether.R
import com.company.eventogether.databinding.ImageMessageBinding
import com.company.eventogether.databinding.MessageBinding
import com.company.eventogether.helpclasses.StringResourcesProvider
import com.company.eventogether.model.ChatMessageDTO
import com.company.eventogether.viewmodels.ChatMessageViewModel.Companion.DUMMY_TEXT
import com.company.eventogether.viewmodels.UserProfileViewModel.Companion.ANONYMOUS
import org.koin.java.KoinJavaComponent.inject

class ChatMessageAdapter(private val currentUserName: String, private val options: FirebaseRecyclerOptions<ChatMessageDTO>) :
    FirebaseRecyclerAdapter<ChatMessageDTO, ViewHolder>(options) {

    val stringResourcesProvider: StringResourcesProvider by inject(StringResourcesProvider::class.java)

    companion object {
        const val TAG = "ChatMessageAdapter"
        const val DUMMY_URL = "https://"
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_IMAGE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_TEXT) {
            val view = inflater.inflate(R.layout.message, parent, false)
            val binding = MessageBinding.bind(view)
            MessageViewHolder(binding)
        } else {
            val view = inflater.inflate(R.layout.image_message, parent, false)
            val binding = ImageMessageBinding.bind(view)
            ImageMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ChatMessageDTO) {

        if (options.snapshots[position].complaintDescription != null) {
            (holder as MessageViewHolder).bind(model)
        } else {
            (holder as ImageMessageViewHolder).bind(model)
        }
    }

    inner class MessageViewHolder(private val binding: MessageBinding) : ViewHolder(binding.root) {

        fun bind(message: ChatMessageDTO) {

            val userName = message.sender

            if (userName == currentUserName) {

                val messageText = if (message.complaintDescription == DUMMY_TEXT) {
                    binding.messageLoaderReceiver.isVisible = true
                    binding.messageTextViewReceiver.isVisible = false
                    binding.messengerTextViewReceiver.isVisible = false
                    ""
                } else {
                    binding.messageLoaderReceiver.isVisible = false
                    binding.messageTextViewReceiver.isVisible = true
                    binding.messengerTextViewReceiver.isVisible = true
                    message.complaintDescription
                }

                binding.messageTextViewReceiver.text = messageText
                binding.messengerTextViewReceiver.text = message.sender
                binding.messageHolderSender.isVisible = false
                binding.messageHolderReceiver.isVisible = true

                if (message.photoUrl != null) {
                    loadImageIntoView(
                        binding.messengerImageViewReceiver,
                        message.photoUrl
                    )
                } else {
                    binding.messengerImageViewReceiver.setImageResource(R.drawable.profile_light_blue)
                    binding.messengerImageViewReceiver.scaleType = ImageView.ScaleType.FIT_XY
                }

            } else if (userName != currentUserName && userName != ANONYMOUS) {

                val messageText = if (message.complaintDescription == DUMMY_TEXT) {
                    binding.messageLoaderSender.isVisible = true
                    binding.messageTextViewSender.isVisible = false
                    binding.messengerTextViewSender.isVisible = false
                    ""
                } else {
                    binding.messageLoaderSender.isVisible = false
                    binding.messageTextViewSender.isVisible = true
                    binding.messengerTextViewSender.isVisible = true
                    message.complaintDescription
                }

                binding.messageTextViewSender.text = messageText
                binding.messengerTextViewSender.text = message.sender
                binding.messageHolderReceiver.isVisible = false
                binding.messageHolderSender.isVisible = true

                if (message.photoUrl != null) {
                    loadImageIntoView(
                        binding.messengerImageViewSender,
                        message.photoUrl
                    )
                } else {
                    binding.messengerImageViewSender.setImageResource(R.drawable.profile_light_blue)
                    binding.messengerImageViewSender.scaleType = ImageView.ScaleType.FIT_XY
                }

            } else {

                val messageText = if (message.complaintDescription == DUMMY_TEXT) {
                    binding.messageLoaderSender.isVisible = true
                    binding.messageTextViewSender.isVisible = false
                    binding.messengerTextViewSender.isVisible = false
                    ""
                } else {
                    stringResourcesProvider.getString(R.string.greeting_message_text)
                }

                binding.messageTextViewSender.text = messageText
                binding.messengerTextViewSender.text = ANONYMOUS
                binding.messageHolderSender.isVisible = true
                binding.messageHolderReceiver.isVisible = false
            }
        }
    }

    inner class ImageMessageViewHolder(private val binding: ImageMessageBinding) :
        ViewHolder(binding.root) {

        fun bind(message: ChatMessageDTO) {

            val userName = message.sender

            if (userName == currentUserName) {

                binding.messageHolderSender.isVisible = false
                binding.messageHolderReceiver.isVisible = true

                if (message.photoUrl != null) {
                    loadImageIntoView(
                        binding.messengerImageViewReceiver,
                        message.photoUrl
                    )
                }

                if (message.imageUrl == DUMMY_URL) {
                    binding.messageLoaderReceiver.isVisible = true
                } else if (message.imageUrl != null) {
                    binding.messageLoaderReceiver.isVisible = false
                    loadImageIntoView(
                        binding.messageImageViewReceiver,
                        message.imageUrl
                    )
                    binding.messengerTextViewReceiver.text = message.sender
                }
            } else if (userName != currentUserName && userName != ANONYMOUS) {

                binding.messageHolderSender.isVisible = true
                binding.messageHolderReceiver.isVisible = false

                if (message.photoUrl != null) {
                    loadImageIntoView(
                        binding.messengerImageViewSender,
                        message.photoUrl
                    )
                }

                if (message.imageUrl == DUMMY_URL) {
                    binding.messageLoaderSender.isVisible = true
                } else if (message.imageUrl != null) {
                    binding.messageLoaderSender.isVisible = false
                    loadImageIntoView(
                        binding.messageImageViewSender,
                        message.imageUrl
                    )
                    binding.messengerTextViewSender.text = message.sender
                }
            } else {

                binding.messageHolderSender.isVisible = true
                binding.messageHolderReceiver.isVisible = false

                if (message.imageUrl == DUMMY_URL) {
                    binding.messageLoaderSender.isVisible = true
                } else if (message.imageUrl != null) {
                    binding.messageLoaderSender.isVisible = false
                    loadImageIntoView(
                        binding.messageImageViewSender,
                        message.imageUrl
                    )
                    binding.messengerTextViewSender.text = message.sender
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (options.snapshots[position].complaintDescription != null) VIEW_TYPE_TEXT else VIEW_TYPE_IMAGE
    }

    private fun loadImageIntoView(
        view: ImageView,
        url: String
    ) {
        if (url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    loadImageViewWithGlide(view, downloadUrl, false)
                }
                .addOnFailureListener { e ->
                    Log.w(
                        TAG,
                        "Getting download url was not successful.",
                        e
                    )
                }
        } else {
            loadImageViewWithGlide(view, url, true)
        }
    }

    private fun loadImageViewWithGlide(view: ImageView, url: String, circleCrop: Boolean) {

        if (circleCrop) {
            Glide.with(view.context)
                .load(url)
                .circleCrop()
                .into(view)
        } else {
            Glide.with(view.context)
                .load(url)
                .into(view)
        }
    }
}
