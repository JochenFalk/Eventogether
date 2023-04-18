package com.company.eventogether.viewmodels

import android.content.Context
import android.util.Log
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.company.eventogether.R

class UserProfileViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth

    var startSignInAction: MutableLiveData<Boolean> = MutableLiveData()
    var isSignedInSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var isSignedInError: MutableLiveData<Boolean> = MutableLiveData()
    var isSignedOutSuccess: MutableLiveData<Boolean> = MutableLiveData()

    init {
        checkLoginStatus()
    }

    companion object {
        private const val TAG = "UserProfileViewModel"
        const val ANONYMOUS = "Event-bot"
    }

    fun getPhotoUrl() : String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

    fun getUserName() : String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else ANONYMOUS
    }

    fun getCurrentUser() : FirebaseUser? {
        return auth.currentUser
    }

    fun setCustomMenu(context: Context, profileIcon: ImageButton?, menuIcon: ImageButton?) {

        if (profileIcon != null) {

            val popupMenu = PopupMenu(context, profileIcon).apply {
                this.menuInflater.inflate(R.menu.profile_menu, this.menu)
            }

            popupMenu.setOnMenuItemClickListener { menuItem ->

                when (menuItem.itemId) {
                    R.id.sign_out -> {
                        signOut(context)
                    }
                    R.id.sign_in -> {
                        startSignInAction.value = true
                    }
                }
                false
            }

            if (getCurrentUser() != null) {
                popupMenu.menu.findItem(R.id.sign_in).isVisible = false
                popupMenu.menu.findItem(R.id.sign_out).isVisible = true
            } else {
                popupMenu.menu.findItem(R.id.sign_in).isVisible = true
                popupMenu.menu.findItem(R.id.sign_out).isVisible = false
            }

            setProfileImage(profileIcon, getCurrentUser()?.photoUrl.toString())

            profileIcon.setOnClickListener {
                popupMenu.show()
            }
        }

        if (menuIcon != null) {

            val popupMenu = PopupMenu(context, menuIcon).apply {
                this.menuInflater.inflate(R.menu.main_menu, this.menu)
            }

            popupMenu.setOnMenuItemClickListener { false }

            menuIcon.setOnClickListener {
                popupMenu.show()
            }
        }
    }

    fun signOutOnError(context: Context) {
        AuthUI.getInstance().signOut(context)
    }

    private fun signOut(context: Context) {
        AuthUI.getInstance().signOut(context)
            .addOnSuccessListener {
                isSignedOutSuccess.value = true
            }
    }

    private fun checkLoginStatus() {

        auth = Firebase.auth
        auth.currentUser.let {
            if (it != null) {
                if (it.displayName != null) {
                    Log.d(TAG, "User is logged in: ${it.uid}")
                } else {
                    Log.d(TAG, "User display name not set!: ${it.uid}")
                    isSignedInError.value = true
                }
            }
        }
    }

    private fun setProfileImage(imageButton: ImageButton, url: String?) {

        if (url != null) {
            Glide.with(imageButton.context)
                .load(url)
                .circleCrop()
                .into(imageButton)
        } else {
            imageButton.setImageResource(R.drawable.profile_light_blue)
        }
    }
}