package com.cyber.restory.utils

import android.content.res.AssetManager
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class AssetLoader(private val assetLoader: AssetManager) {
    fun loadAsset(path: String): String? {
        return runCatching {
            try {
                val inputStream = assetLoader.open(path)
                BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
            } catch (e: IOException) {
                null
            }
        }.getOrNull()
    }
}