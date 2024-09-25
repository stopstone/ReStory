package com.cyber.restory.presentation.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
            // Handle notice click
        }

        binding.sivTerms.setOnClickListener {
            // Handle terms click
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}