package com.cyber.restory.presentation.contact

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.cyber.restory.databinding.ActivityContactBinding
import com.cyber.restory.presentation.contact.viewmodel.ContactViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactActivity : AppCompatActivity() {
    private val binding: ActivityContactBinding by lazy { ActivityContactBinding.inflate(layoutInflater) }
    private val viewModel: ContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupListeners()
        observeUiState()
        observeSubmitResult()
    }

    private fun setupListeners() {
        binding.etSpaceName.addTextChangedListener {
            viewModel.updateSpaceName(it.toString(), binding.etSpaceName.selectionStart)
        }
        binding.etSpaceAddress.addTextChangedListener {
            viewModel.updateSpaceAddress(it.toString(), binding.etSpaceAddress.selectionStart)
        }
        binding.etRequestDetails.addTextChangedListener {
            viewModel.updateRequestDetails(it.toString(), binding.etRequestDetails.selectionStart)
        }
        binding.btnSubmit.setOnClickListener {
//            viewModel.submitRequest()
            /*
            * 서버 요청 필요
            * */
            Toast.makeText(this@ContactActivity, "준비중입니다.", Toast.LENGTH_SHORT).show()
        }

        binding.toolbarContact.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observeSubmitResult() {
        lifecycleScope.launch {
            viewModel.submitSuccess.collect {
                Toast.makeText(this@ContactActivity, "요청이 성공적으로 제출되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        lifecycleScope.launch {
            viewModel.submitError.collect { errorMessage ->
                Toast.makeText(this@ContactActivity, "요청 제출에 실패했습니다: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateEditTextIfNeeded(binding.etSpaceName, state.spaceName, state.spaceNameCursor)
                updateEditTextIfNeeded(binding.etSpaceAddress, state.spaceAddress, state.spaceAddressCursor)
                updateEditTextIfNeeded(binding.etRequestDetails, state.requestDetails, state.requestDetailsCursor)
                binding.tvCharCount.text = state.charCount
                binding.btnSubmit.isEnabled = state.isSubmitEnabled
            }
        }
    }

    private fun updateEditTextIfNeeded(editText: EditText, newText: String, newCursorPosition: Int) {
        if (editText.text.toString() != newText) {
            editText.setText(newText)
            editText.setSelection(newCursorPosition.coerceIn(0, newText.length))
        }
    }
}