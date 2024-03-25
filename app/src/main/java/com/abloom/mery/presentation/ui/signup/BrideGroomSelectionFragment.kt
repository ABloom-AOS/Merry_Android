package com.abloom.mery.presentation.ui.signup

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.abloom.mery.R
import com.abloom.mery.databinding.FragmentBrideGroomSelectionBinding
import com.abloom.mery.presentation.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BrideGroomSelectionFragment :
    BaseFragment<FragmentBrideGroomSelectionBinding>(R.layout.fragment_bride_groom_selection) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()

    }

    private fun initListener() {

        binding.groomBut.setOnClickListener {

            findNavController().navigate(
                R.id.action_brideGroomSelectionFragment_to_marryDateFragment
            )

        }

        binding.brideBut.setOnClickListener {
            /*
            findNavController().navigate(
                SignUpFragmentDirections.actionSignUpFragmentToMarryDateFragment()
            )
            */
        }

    }

}