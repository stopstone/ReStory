package com.cyber.restory.presentation.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.cyber.restory.R
import com.cyber.restory.databinding.ViewSettingsItemBinding

class SettingsItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewSettingsItemBinding

    init {
        orientation = HORIZONTAL // 수평 방향으로 설정

        // 변경된 부분
        binding = ViewSettingsItemBinding.inflate(LayoutInflater.from(context), this)

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SettingsItemView,
            0, 0
        ).apply {
            try {
                binding.tvTitle.text = getString(R.styleable.SettingsItemView_settingsItemText)
                binding.ivIcon.setImageDrawable(getDrawable(R.styleable.SettingsItemView_settingsItemIcon))
            } finally {
                recycle()
            }
        }
    }
}