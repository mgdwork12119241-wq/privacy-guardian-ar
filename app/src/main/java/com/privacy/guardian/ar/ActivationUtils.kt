package com.privacy.guardian.ar

import android.content.Context
import android.provider.Settings
import java.security.MessageDigest

object ActivationUtils {
    private const val SALT = "PrivacyGuardianAR_2026"

    fun isPro(context: Context): Boolean {
        val prefs = context.getSharedPreferences("activation_prefs", Context.MODE_PRIVATE)
        val code = prefs.getString("activation_code", null) ?: return false
        return verifyCode(context, code)
    }

    fun verifyCode(context: Context, code: String): Boolean {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val expectedCode = generateCode(androidId)
        return code == expectedCode
    }

    fun saveCode(context: Context, code: String) {
        val prefs = context.getSharedPreferences("activation_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("activation_code", code).apply()
    }

    private fun generateCode(androidId: String): String {
        val input = androidId + SALT
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }.substring(0, 12).uppercase()
    }
    
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}
