package com.company.eventogether.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.company.eventogether.R
import com.company.eventogether.adapters.ChatMessageAdapter
import com.company.eventogether.databinding.ActivityChatBinding
import com.company.eventogether.helpclasses.Tools
import com.company.eventogether.helpclasses.messages.ButtonObserver
import com.company.eventogether.helpclasses.messages.ScrollToBottomObserver
import com.company.eventogether.model.ChatMessageDTO
import com.company.eventogether.viewmodels.ChatMessageViewModel
import com.company.eventogether.viewmodels.UserProfileViewModel
import com.company.eventogether.viewmodels.UserProfileViewModel.Companion.ANONYMOUS
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: ChatMessageAdapter
    private lateinit var timer : CountDownTimer
    private lateinit var currentFile: File

    private val userProfileViewModel: UserProfileViewModel by viewModel()
    private val chatMessageViewModel: ChatMessageViewModel by viewModel()

    //    private val openDocument = registerForActivityResult(MyOpenDocumentContract()) { uri ->
//        uri?.let { onImageSelected(it) }
//    }

    companion object {
        private const val TAG = "ChatActivity"
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                callMainActivity()
            }
        })

        val currentUserName = userProfileViewModel.getUserName()!!

        adapter = ChatMessageAdapter(currentUserName, chatMessageViewModel.getOptions())
        manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = manager
        binding.messageRecyclerView.adapter = adapter

        adapter.registerAdapterDataObserver(
            ScrollToBottomObserver(
                binding.progressBar,
                binding.messageRecyclerView,
                adapter,
                manager
            )
        ).also {

            timer = object : CountDownTimer(1500, 500) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    sendGreeting()
                }
            }
            timer.start()
        }

        // Disable the send button when there's no text in the input field
        binding.messageEditText.addTextChangedListener(ButtonObserver(binding.sendButton))

        binding.sendButton.setOnClickListener {
            val newMessage = ChatMessageDTO(
                complaintDescription = binding.messageEditText.text.toString(),
                sender = userProfileViewModel.getUserName(),
                photoUrl = userProfileViewModel.getPhotoUrl(),
                imageUrl = null
            )
            chatMessageViewModel.pushMessage(newMessage)
            binding.messageEditText.setText("")
        }

        binding.addMessageImageView.setOnClickListener {
//            openDocument.launch(arrayOf("image/*"))
            setCameraPermissions()
        }

        setObservers()
        setCustomToolbar()
    }

    private fun sendGreeting() {

        if (adapter.itemCount == 0) {
            val newMessage = ChatMessageDTO(
                complaintDescription = getString(R.string.greeting_message_text),
                sender = ANONYMOUS,
                photoUrl = null,
                imageUrl = null
            )
            chatMessageViewModel.pushMessage(newMessage)
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

        findViewById<ImageView>(R.id.toolbarImage).setOnClickListener {
            callMainActivity()
        }

        userProfileViewModel.setCustomMenu(
            this@ChatActivity, findViewById(R.id.profileIcon), findViewById(R.id.menuIcon)
        )
    }

    private fun setCameraPermissions() {
        val permissions = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        if (permissions != PackageManager.PERMISSION_GRANTED) {
            makePermissionsRequest()
        } else {
            callCameraIntent()
        }
    }

    private fun makePermissionsRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(
                        applicationContext,
                        R.string.camera_rights_text,
                        Toast.LENGTH_LONG
                    ).show()
            }
        }
    }

    private fun callCameraIntent() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

            takePictureIntent.resolveActivity(packageManager)?.also {

                val file: File? = try {
                    Tools.createImageFile(this@ChatActivity, Locale.GERMAN)
                } catch (e: IOException) {
                    Log.d(TAG, "Unable to create file: $e")
                    null
                }

                file?.also {
                    val uri: Uri = FileProvider.getUriForFile(
                        this,
                        "com.company.eventogether",
                        it
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)

                    currentFile = it
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            val inputData = contentResolver.openInputStream(currentFile.toUri())?.readBytes()
            if (inputData != null) {
                userProfileViewModel.getCurrentUser()?.let {
                    chatMessageViewModel.pushImage(
                        this,
                        it, currentFile.toUri()
                    )
                }
            }
        }
    }

    private fun callMainActivity() {
        val intent = Intent(this@ChatActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
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

//    private fun onImageSelected(uri: Uri) {
//        userProfileViewModel.getCurrentUser().let { user ->
//            if (user != null) {
//                chatMessageViewModel.pushImage(this@ChatActivity, user, uri)
//            }
//        }
//    }
}