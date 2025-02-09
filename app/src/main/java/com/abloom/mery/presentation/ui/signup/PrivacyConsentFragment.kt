package com.abloom.mery.presentation.ui.signup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abloom.mery.MixpanelManager
import com.abloom.mery.R
import com.abloom.mery.databinding.FragmentPrivacyConsentBinding
import com.abloom.mery.presentation.common.base.NavigationFragment
import com.abloom.mery.presentation.ui.webview.WebViewUrl
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PrivacyConsentFragment :
    NavigationFragment<FragmentPrivacyConsentBinding>(R.layout.fragment_privacy_consent) {

    @Inject
    lateinit var mixpanelManager: MixpanelManager
    private val viewModel: SignUpViewModel by viewModels({ requireParentFragment() })
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDataBinding()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
        binding.onWebViewNavigate = ::navigateToWebView
        binding.onSignUpButtonClick = ::handleSignUpButtonClick
    }

    private fun navigateToWebView(url: WebViewUrl) {
        findNavController().navigateSafely(
            SignUpFragmentDirections.actionSignUpFragmentToWebViewFragment(url)
        )
    }

    private fun handleSignUpButtonClick() {
        mixpanelManager.setPrivacyConsent()
        viewModel.join()
        findNavController().popBackStack()
    }
}
