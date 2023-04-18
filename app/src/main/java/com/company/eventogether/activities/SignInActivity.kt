package com.company.eventogether.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.company.eventogether.AppSettings.PRIVACY_URL
import com.company.eventogether.AppSettings.TOS_URL
import com.company.eventogether.R
import com.company.eventogether.databinding.ActivitySignInBinding
import com.company.eventogether.viewmodels.UserProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    private val userProfileViewModel: UserProfileViewModel by viewModel()
    private val signIn: ActivityResultLauncher<Intent> =
        registerForActivityResult(FirebaseAuthUIActivityResultContract(), this::onSignInResult)

    companion object {
        private const val TAG = "SignInActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        signIn()
    }

    private fun signIn() {

        val currentUser = userProfileViewModel.getCurrentUser()

        if (currentUser == null) {
            val customLayout =
                AuthMethodPickerLayout.Builder(R.layout.activity_custom_authmethodpicker)
                    .setEmailButtonId(R.id.btnSignInEmail)
                    .setGoogleButtonId(R.id.btnSignInGoogle)
                    .build()
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.mipmap.ic_launcher)
                .setTheme(R.style.Theme_FireBaseAuth)
                .setLockOrientation(true)
                .setAuthMethodPickerLayout(customLayout)
                .setTosAndPrivacyPolicyUrls(TOS_URL, PRIVACY_URL)
                .setAvailableProviders(
                    listOf(
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.GoogleBuilder().build(),
                    )
                )
                .build().apply {
                    supportActionBar?.hide()
                }

            signIn.launch(signInIntent)
        } else {
            callMainActivity()
        }
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {

        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, "Sign in successful!")
            userProfileViewModel.isSignedInSuccess.value = true
            callMainActivity()
        } else {
            Log.d(TAG, "Sign in unsuccessful!")
            userProfileViewModel.isSignedInError.value = true
            callMainActivity()

            if (result.idpResponse == null) {
                Log.d(TAG, "Sign in canceled")
            } else {
                Log.d(TAG, "Sign in error", result.idpResponse!!.error)
            }
        }
    }

    private fun callMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
