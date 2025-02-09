package com.abloom.mery.presentation.ui.writeanswer

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abloom.mery.MixpanelManager
import com.abloom.mery.R
import com.abloom.mery.databinding.FragmentWriteAnswerBinding
import com.abloom.mery.presentation.MainViewModel
import com.abloom.mery.presentation.common.base.NavigationFragment
import com.abloom.mery.presentation.common.view.ConfirmDialog
import com.abloom.mery.presentation.common.view.setOnActionClick
import com.abloom.mery.presentation.common.view.setOnNavigationClick
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WriteAnswerFragment :
    NavigationFragment<FragmentWriteAnswerBinding>(R.layout.fragment_write_answer) {

    @Inject
    lateinit var mixpanelManager: MixpanelManager

    private val writeAnswerViewModel: WriteAnswerViewModel by viewModels()

    private val mainViewModel: MainViewModel by activityViewModels()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (writeAnswerViewModel.answer.value.isNotBlank()) {
                showBackConfirmDialog()
            } else {
                findNavController().popBackStack()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWindowInsetsListener(view)
        setupOnBackPressed()
        setupAppBar()
        setupDataBinding()

        checkMoveWithDeepLink()
    }

    private fun setupWindowInsetsListener(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val navigatorBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            if (imeVisible) {
                binding.root.setPadding(0, 0, 0, imeHeight - navigatorBarHeight)
            } else {
                binding.root.setPadding(0, 0, 0, 0)
            }
            insets
        }
    }

    private fun setupOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            owner = viewLifecycleOwner,
            onBackPressedCallback = onBackPressedCallback
        )
    }

    private fun showBackConfirmDialog() {
        ConfirmDialog(
            context = requireContext(),
            title = getString(R.string.writeanswer_popback_confirm_dialog_title),
            message = getString(R.string.writeanswer_popback_confirm_dialog_message),
            positiveButtonLabel = getString(R.string.writeanswer_exit),
            onPositiveButtonClick = { findNavController().popBackStack() },
            negativeButtonLabel = getString(R.string.all_cancel),
        ).show()
    }

    private fun setupAppBar() {
        binding.appbarWriteAnswer.setOnNavigationClick {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.appbarWriteAnswer.setOnActionClick { showCompleteConfirmDialog() }
    }

    private fun showCompleteConfirmDialog() {
        ConfirmDialog(
            context = requireContext(),
            title = getString(R.string.writeanswer_complete_confirm_dialog_title),
            message = getString(R.string.writeanswer_complete_confirm_dialog_message),
            positiveButtonLabel = getString(R.string.writeanswer_complete),
            onPositiveButtonClick = ::handleWriteAnswerConfirm,
            negativeButtonLabel = getString(R.string.all_cancel),
        ).show()
    }

    private fun handleWriteAnswerConfirm() {
        mixpanelManager.writeAnswer(
            writeAnswerViewModel.question.value?.id ?: 0,
            writeAnswerViewModel.answer.value.length
        )
        writeAnswerViewModel.answerQna()
        mainViewModel.dispatchAnswerEvent()
        val isNavigateToHomeSuccess =
            findNavController().popBackStack(R.id.homeFragment, false)
        if (!isNavigateToHomeSuccess) findNavController().popBackStack()
    }

    private fun setupDataBinding() {
        binding.viewModel = writeAnswerViewModel
    }

    private fun checkMoveWithDeepLink() {
        if (writeAnswerViewModel.moveWithDeepLink.value) {
            mixpanelManager.moveDeepLinkToWriteFragment()
        }
    } // 오늘의 추천 질문 알림을 눌러서 WriteAnswerFragment로 올때만 실행됨.

    companion object {

        const val KEY_QUESTION_ID = "question_id"
        fun createArguments(questionId: Long): Bundle =
            Bundle().apply { putLong(KEY_QUESTION_ID, questionId) }
    }
}


