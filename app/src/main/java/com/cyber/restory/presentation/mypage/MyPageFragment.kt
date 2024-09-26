package com.cyber.restory.presentation.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cyber.restory.databinding.FragmentMypageBinding

class MyPageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.sivRegisterSpace.setOnClickListener {
            // Handle register space click
        }

        binding.sivServiceInquiry.setOnClickListener {
            // Handle service inquiry click
        }

        binding.sivShareService.setOnClickListener {
            // Handle share service click
        }

        binding.sivNotice.setOnClickListener {
            val action = MyPageFragmentDirections.actionMypageToNotice()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}