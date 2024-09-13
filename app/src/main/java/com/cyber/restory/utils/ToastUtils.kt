package com.cyber.restory.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.cyber.restory.App
import com.cyber.restory.R

class ToastUtils {

    companion object {

        fun showToast(context: Context, message: String) {
            showToast(context, message, Toast.LENGTH_SHORT)
        }

        fun showToast(context: Context, stringResId: Int) {
            showToast(context, context.getString(stringResId))
        }

        fun showLongToast(context: Context, stringResId: Int) {
            showLongToast(context, context.getString(stringResId))
        }

        fun showLongToast(context: Context, message: String) {
            showToast(context, message, Toast.LENGTH_LONG)
        }

        private fun showToast(context: Context, message: String, duration: Int) {
            Toast.makeText(context, message, duration).show()
        }

        fun showToast(message: String) {
            showToast(App.instance, message, Toast.LENGTH_SHORT)
        }

        fun showToast(@StringRes resId: Int) {
            showToast(App.instance, App.instance.getString(resId), Toast.LENGTH_SHORT)
        }

        fun showTopToast(message: String) {
            val inflater = LayoutInflater.from(App.instance)
            val layout: View = inflater.inflate(R.layout.custom_toast, null)

            val text: TextView = layout.findViewById(R.id.message)
            text.text = message

            with(Toast(App.instance)) {
                setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 300)
                duration = Toast.LENGTH_LONG
                view = layout
                show()
            }

            /*
            Android11(API30) 이후 부터는 보안 및 일관성을 위해 Toast의 위치 조정이 제한된다고 함.
            val toastTop = Toast.makeText(App.instance, message, Toast.LENGTH_SHORT)
            toastTop.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)
            toastTop.show()*/
        }
    }
}