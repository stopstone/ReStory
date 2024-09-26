package com.cyber.restory.presentation.mypage

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
            val action = MyPageFragmentDirections.actionMypageToContact()
            findNavController().navigate(action)
        }

        binding.sivServiceInquiry.setOnClickListener {
            sendServiceInquiryEmail()
        }

        binding.sivShareService.setOnClickListener {
            Toast.makeText(context, "준비중입니다.", Toast.LENGTH_SHORT).show()
        }

        binding.sivNotice.setOnClickListener {
            val action = MyPageFragmentDirections.actionMypageToNotice()
            findNavController().navigate(action)
        }
    }

    private fun sendServiceInquiryEmail() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("dev.stopstone@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "서비스 문의")
            putExtra(Intent.EXTRA_TEXT, "여기에 문의 내용을 작성해주세요.")
        }

        try {
            val gmailIntent = intent.apply {
                setPackage("com.google.android.gm")
            }
            startActivity(gmailIntent)
        } catch (e: ActivityNotFoundException) {
            try {
                startActivity(Intent.createChooser(intent, "이메일 전송하기"))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "이메일 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}